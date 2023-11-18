import json
import requests
import socket
import numpy

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

            header = {
                "Content-type": "application/json"
            }
            json_dict = {
                "humidity": humidity_str,
                "temperature": temp_str,
            }
            json_data = json.dumps(json_dict)
            response = requests.post("https://thingsboard.uitprojects.com/api/v1/xNX9FiLyWenmKNaj2pXV/telemetry",
                                     data=json_data,
                                     headers=header)
            print("https code: " + str(response.status_code))
            print(f"Temp: {temp_str}")
            print(f"Humidity: {humidity_str}")
        except socket.timeout:
            client_socket.shutdown(socket.SHUT_RDWR)
            client_socket.close()
            print("close connection because timeout")
            break
