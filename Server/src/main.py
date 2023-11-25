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
        actual_data += current_socket.recv(length - byte_read)
        if actual_data == b"":
            return b""
        byte_read = len(actual_data)
    return actual_data


def listen_on(current_socket: socket.socket, func1, func2,func3,func4):
    current_socket.listen(5)
    print("Socket 2509 is listening ")
    while True:
        slave_socket, address = current_socket.accept()
        print("new slave connection from " + address[0])
        slave_socket.settimeout(10)
        while True:
            try:
                is_device_alive = bool.from_bytes(receive(1, slave_socket), byteorder="little", signed=False)
                if is_device_alive is False:
                    print("Device esp8266 off: waiting device to connect")
                    sleep(2)
                    continue
                slave_socket.send(func1().to_bytes(length=4, byteorder="little", signed=False))
                if func1() == 200:
                    slave_socket.send(func2().to_bytes(length=4, byteorder="little", signed=False))
                    is_ack = bool.from_bytes(receive(1, slave_socket), byteorder="little", signed=False)
                    if is_ack is False:
                        breakpoint()
                    func3()
            except:
                slave_socket.shutdown(socket.SHUT_RDWR)
                slave_socket.close()
                print("something is not right from ras, connection drop")
                break


server_socket_thread = Thread(target=listen_on, args=(server_socket, get_common_code_iot, get_iot_delay,api.reset_common_code_iot,get_device_name))
server_socket_thread.start()

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=80)
