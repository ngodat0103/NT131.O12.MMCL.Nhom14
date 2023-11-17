import json
import requests
import socket

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

while True:
    client_socket, ip_address = server.accept()
    client_socket.settimeout(5)
    while True:
        try:
            temp_bytes = receive(2)
            if temp_bytes == b"":
                client_socket.shutdown(socket.SHUT_RDWR)
                client_socket.close()
                break
            humidity_bytes = receive(2)

            temp_int = int.from_bytes(temp_bytes, byteorder="little", signed=True)
            humidity_int = int.from_bytes(humidity_bytes, byteorder="little", signed=True)

            header = {
                "Content-type": "application/json"
            }
            json_dict = {
                "humidity": str(humidity_int),
                "temperature": str(temp_int),
            }
            json_data = json.dumps(json_dict)
            response = requests.post("https://thingsboard.uitprojects.com/api/v1/xNX9FiLyWenmKNaj2pXV/telemetry",
                                     data=json_data,
                                     headers=header)
            print("https code: " + str(response.status_code))
            print(f"Temp: {temp_int}")
            print(f"Humidity: {humidity_int}")
        except socket.timeout:
            client_socket.shutdown(socket.SHUT_RDWR)
            client_socket.close()
            print("close connection because timeout")
            break


