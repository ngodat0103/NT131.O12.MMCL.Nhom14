import json
import socket
from cipher_module import AES_module
import threading
import handle_type_client_message_module

threading.active_count()


class Server_module:
    def __init__(self):
        self.SERVER_ADDRESS_TUPLE = ('localhost', 2509)
        self.server_socket = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
        self.server_socket.bind(self.SERVER_ADDRESS_TUPLE)
        self.thread_list = []

    def handle_client_connection(self, client_socket: socket):
        while True:
            header_length_bytearray = bytearray(4)
            try:
                client_socket.recv_into(header_length_bytearray, 4)
            except ConnectionError:
                print("client force close connection")
                client_socket.close()
                break

            header_length_int = int.from_bytes(header_length_bytearray, "big")
            buffer_data_bytearray = bytearray(header_length_int)
            bytes_received_int = 0
            while bytes_received_int < header_length_int:
                bytes_received_int = client_socket.recv_into(buffer_data_bytearray,
                                                             header_length_int - bytes_received_int)
            message_encrypted_str = buffer_data_bytearray.decode()
            message_plaintext_str = AES_module.decryt(message_encrypted_str)
            print(message_plaintext_str)
            client_message: dict = json.loads(message_plaintext_str)
            process_client_message_dict = handle_type_client_message_module.process(client_message)
            print(process_client_message_dict)
            response_to_client_message_json_string = json.dumps(process_client_message_dict)
            response_to_client_message_json_bytes = response_to_client_message_json_string.encode()
            response_to_client_message_header_int = len(response_to_client_message_json_bytes)
            response_to_client_message_header_bytes = response_to_client_message_header_int.to_bytes(4,"big")
            client_socket.send(response_to_client_message_header_bytes)
            client_socket.send(response_to_client_message_json_bytes)

    def listen(self, listen: bool):
        if listen:
            self.server_socket.listen(5)
            print("Server is listening")
            while True:
                new_client_socket, client_address = self.server_socket.accept()
                new_thread = threading.Thread(target=self.handle_client_connection, args=(new_client_socket,))
                self.thread_list.append(new_thread)
                new_thread.start()
                print(f"connect from {client_address}")
