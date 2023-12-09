import uvicorn

import socket
from threading import Thread
from time import sleep

import share
from smtp import send_email_warning_device_off

server_socket = socket.socket()
server_socket.bind(("0.0.0.0", 2509))
from api import *
import api
from cipher_module import *


def receive(length: int, current_socket: socket.socket) -> bytes:
    byte_read = 0
    actual_data = b""
    while byte_read < length:
        try:
            actual_data += current_socket.recv(length - byte_read)
        except ConnectionError:
            print("Ras close connection")
            return b""
        if actual_data == b"":
            return b""
        byte_read = len(actual_data)
    return actual_data


def listen_on(current_socket: socket.socket):
    current_socket.listen(5)
    print("Socket 2509 is listening ")
    while True:
        manager_socket, address = current_socket.accept()
        share.command_code = share.MAKE_CHANGES
        share.iot_delay = database_module.access_database(general_statements["get_device_info"], ("esp8266",))[0][1]
        print("new slave connection from " + address[0])
        database_module.access_database(general_statements["update_device_status"], (True, "ras"))
        manager_socket.settimeout(5)
        first_time = True
        notify = False
        while True:
            sleep(2)
            if first_time:
                manager_socket.send(encrypt(share.REQUEST_STATUS.to_bytes(length=4, byteorder="little", signed=False)))
                response = receive(16, manager_socket)
                is_device_alive = bool.from_bytes(decrypt(response))
                if is_device_alive is False:
                    print("esp8266 offline")
                    continue
                manager_socket.send(encrypt(share.MAKE_CHANGES.to_bytes(byteorder="little", signed=False, length=4)))
                with share_lock:
                    manager_socket.send(encrypt(share.iot_delay.to_bytes(byteorder="little", signed=False, length=4)))
                first_time = False
                share.command_code = share.KEEP_CONFIG
            elif share.command_code == share.KEEP_CONFIG:
                manager_socket.send(encrypt(share.KEEP_CONFIG.to_bytes(byteorder="little", signed=False, length=4)))
            elif share.command_code == share.MAKE_CHANGES:
                manager_socket.send(encrypt(share.MAKE_CHANGES.to_bytes(byteorder="little", signed=False, length=4)))
                manager_socket.send(encrypt(share.iot_delay.to_bytes(byteorder="little", signed=False, length=4)))
                with share_lock:
                    share.command_code = share.KEEP_CONFIG
            elif share.command_code == share.REBOOT_ESP:
                manager_socket.send(encrypt(share.REBOOT_ESP.to_bytes(byteorder="little",signed=False,length=4)))
                with share_lock:
                    share.command_code = share.KEEP_CONFIG


server_socket_thread = Thread(target=listen_on, args=(server_socket,))
server_socket_thread.start()

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=80, proxy_headers=True)
