import json
import time

import requests
import socket
import numpy
import binascii

server = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
server.bind(("0.0.0.0", 80))
server.listen(5)


def receive(length: int) -> bytes:
    byte_read = 0
    actual_data = b""
    while byte_read < length:
        actual_data += client_socket.recv(length - byte_read)
        if actual_data == b"":
            return b""
        byte_read = len(actual_data)
    return actual_data


print("Server is listening from Arduino")

while True:
    client_socket, ip_address = server.accept()
    client_socket.settimeout(30)
    while True:
        try:
            temp_bytes = receive(4)
            if temp_bytes == b"":
                client_socket.shutdown(socket.SHUT_RDWR)
                client_socket.close()
                break

            humidity_bytes = receive(4)
            temp_float = numpy.frombuffer(temp_bytes, dtype=numpy.float32)
            humidity_float = numpy.frombuffer(humidity_bytes, dtype=numpy.float32)

            temp_str = str(temp_float[0])
            humidity_str = str(humidity_float[0])
            is_make_change = False
            client_socket.send(is_make_change.to_bytes(length=1,byteorder="little"))
       #     client_socket.send(int(5000).to_bytes(length=2, byteorder="little", signed=False))
            # is_ack_bytes = receive(1)
            # is_ack_bool = bool.from_bytes(is_ack_bytes, byteorder="little")
            headers = {
                "Content-type": "application/json"
            }
            json_dict = {
                "humidity": humidity_str,
                "temperature": temp_str,
            }
            json_data = json.dumps(json_dict)
            # response1 = requests.post("https://thingsboard.uitprojects.com/api/v1/xNX9FiLyWenmKNaj2pXV/telemetry",
            #                           data=json_data,
            #                           headers=headers)

            headers["Content-type"] = "application/x-www-form-urlencoded"

            json_dict = {
                "humidity": humidity_str,
                "temperature": temp_str,
                "time_primary": int(time.time())
            }
            # response2 = requests.post("https://server.uitprojects.com/update_temp",
            #                           data=json_dict,
            #                           headers=headers)

            print(humidity_str)
            print(temp_str)


        except socket.timeout:
            client_socket.shutdown(socket.SHUT_RDWR)
            client_socket.close()
            print("close connection because timeout")
            break
