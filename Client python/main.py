import socket
from cipher_module import Cipher_module
import json
server_address = ("localhost", 2590)
client_socket = socket.create_connection(server_address)
while True:
    test_message = {
        "message":input("message: ")
    }
    test_message=json.dumps(test_message)
    encrypt_message_bytes = Cipher_module.encrypt(test_message)
    header_length_byte = len(encrypt_message_bytes).to_bytes(4,"big")
    client_socket.send(header_length_byte)
    client_socket.sendall(encrypt_message_bytes)
