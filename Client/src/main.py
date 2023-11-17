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
print("Connect from " + ip_address[0])
while True:
    temp_bytes = receive(2)
    if temp_bytes == b"":
        client_socket.shutdown(socket.SHUT_RDWR)
        client_socket.close()
        break
    humidity_bytes = receive(2)

    temp_int = int.from_bytes(temp_bytes, byteorder="little", signed=True)
    humidity_int = int.from_bytes(humidity_bytes, byteorder="little", signed=True)

    header = {
        "Content-type":"application/json"
    }
    json_dict = {
        "humidity":str(humidity_int),
        "temperature":str(temp_int),
    }
    json_data = json.dumps(json_dict)
    response = requests.post("https://thingsboard.uitprojects.com/api/v1/xNX9FiLyWenmKNaj2pXV/telemetry", data=json_data,
                  headers=header)
    print(response.status_code)
    print(f"Temp: {temp_int}")
    print(f"Humidity: {humidity_int}")
