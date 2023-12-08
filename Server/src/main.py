import uvicorn

import socket
from threading import Thread
from time import sleep

server_socket = socket.socket()
server_socket.bind(("0.0.0.0", 2509))
from api import *
import api


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


def listen_on(current_socket: socket.socket, func1, func2, func3, func4):
    current_socket.listen(5)
    print("Socket 2509 is listening ")
    while True:
        manager_socket, address = current_socket.accept()
        api.command_code_iot = 200
        api.iot_delay = database_module.access_database(general_statements["get_device_info"], ("esp8266",))[0][1]
        print("new slave connection from " + address[0])
        manager_socket.settimeout(30)
        while True:
            try:
                is_device_alive_bytes = receive(1, manager_socket)
                if is_device_alive_bytes == b"":
                    manager_socket.shutdown(socket.SHUT_RDWR)
                    manager_socket.close()
                    print("Ras has close connection, drop Connection")
                    break
                is_device_alive_bool = bool.from_bytes(is_device_alive_bytes, byteorder="little", signed=False)
                if is_device_alive_bool is False:
                    print("Device esp8266 off: waiting device to connect")
                    sleep(2)
                    continue
                manager_socket.send(func1().to_bytes(length=4, byteorder="little", signed=False))
                if func1() == 200:
                    manager_socket.send(func2().to_bytes(length=4, byteorder="little", signed=False))
                    is_ack = bool.from_bytes(receive(1, manager_socket), byteorder="little", signed=False)
                    if is_ack is False:
                        breakpoint()
                    func3()
            except:
                manager_socket.shutdown(socket.SHUT_RDWR)
                manager_socket.close()
                print("something is not right from ras, connection drop")
                break


server_socket_thread = Thread(target=listen_on, args=(
    server_socket, get_common_code_iot, get_iot_delay, api.reset_common_code_iot, get_device_name))
server_socket_thread.start()

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=80, proxy_headers=True)
