import json

from cipher_module import Cipher_module
import socket
client_socket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
client_socket.connect(('server.uitprojects.com',2590))

while True:
    message_dict = {
        'type':'update_temp',
        'temp':input("nhap nhiet do: ")
    }
    if message_dict['temp'] == '0':
        client_socket.close()
        break
    message_json_str = json.dumps(message_dict)
    encrypted_message_bytes = Cipher_module.encrypt(message_json_str)
    header_length_bytes = len(encrypted_message_bytes).to_bytes(4,byteorder='big')
    client_socket.send(header_length_bytes)
    client_socket.send(encrypted_message_bytes)
