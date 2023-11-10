import json
import time
import requests
import socket


def update_temp(temp: int):
    url_encoded_data = {
        "type": "update_temp",
        "temp": str(temp),
        "time_primary": int(time.time())
    }
    if url_encoded_data["temp"] == "0":
        return
    request = requests.post("http://server.uitprojects.com/update_temp", data=url_encoded_data)
    print(request.json())


server = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
server.bind(("0.0.0.0", 80))
server.listen(1)


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

client_socket, ip_address = server.accept()
while True:
    temp = int.from_bytes(receive(4), byteorder="little", signed=True)
    print(f"temp: + {temp}")
