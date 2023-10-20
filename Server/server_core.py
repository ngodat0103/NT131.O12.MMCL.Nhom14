import json
import socket
from cipher_module import Cipher_module
import threading
import handle_types_message_client_module
from random import randint


class Handle_raspberry_app_socket:
    def __init__(self, raspberry_app_socket: socket):
        self.raspberry_app_socket = raspberry_app_socket
        new_thread = threading.Thread(target=self.listen_mode)
        new_thread.start()

    def listen_mode(self):
        while True:
            raspberry_app_message = self.listen()
            if raspberry_app_message==None:
                print("Raspberry close connection")
                break
            if raspberry_app_message['type'] == 'update_temp':
                self.response_to_client(handle_types_message_client_module.process(raspberry_app_message))

    def listen(self) :
        try:
            header_length_bytes = self.raspberry_app_socket.recv(4)
        except ConnectionError:
            return None
        header_length_int = int.from_bytes(header_length_bytes, "big")
        buffer_data_byte = b''
        while len(buffer_data_byte) < header_length_int:
            chunk = self.raspberry_app_socket.recv(header_length_int)
            buffer_data_byte += chunk
        # decrypt_mode
        message_encrypted_bytes = buffer_data_byte
        message_plaintext_str = Cipher_module.decrypt(message_encrypted_bytes)
        raspberry_app_message: dict = json.loads(message_plaintext_str)
        # not decrypt
        # message_plaintext_str = buffer_data_byte.decode()
        # raspberry_app_message: dict = json.loads(message_plaintext_str)
        return raspberry_app_message

    def response_to_client(self, message: dict, large_data: bool = False):
        if large_data is False:
            response_to_client_message_json_string = json.dumps(message)
            response_to_client_message_json_encrypted_bytes = Cipher_module.encrypt(
                response_to_client_message_json_string)
            response_to_client_message_header_int = len(response_to_client_message_json_encrypted_bytes)
            response_to_client_message_header_bytes = response_to_client_message_header_int.to_bytes(4, "big")
            self.raspberry_app_socket.send(response_to_client_message_header_bytes)
            self.raspberry_app_socket.send(response_to_client_message_json_encrypted_bytes)
        else:
            self.raspberry_app_socket.send(Handle_android_app_socket.large_data)
            Handle_android_app_socket.large_data = None


class Handle_android_app_socket:
    large_data: bytes = b''

    def __init__(self, android_app_socket: socket):
        self.server_handle_client_socket: socket = android_app_socket
        new_thread = threading.Thread(target=self.listen_mode)
        new_thread.start()

    def listen_mode(self):
        while True:
            client_message_dict = self.listen()
            if client_message_dict is None:
                break
            if client_message_dict["type"] != "get_temp":
                print(client_message_dict)
            if client_message_dict["type"] == "forgot_password":
                random_otp = str(randint(a=100000, b=999999))
                client_message_dict["random_otp"] = random_otp
                valid_user_email_dict = handle_types_message_client_module.process(client_message_dict)
                if valid_user_email_dict["status"] == "incorrect_username_email":
                    break
                else:
                    self.response_to_client({"type": "forgot_password", "status": "otp_sent"})
                otp_response_from_client_dict = self.listen()
                if otp_response_from_client_dict["otp"] == random_otp:
                    self.response_to_client({"type": "forgot_password", "otp_valid": "valid"})
                    new_password_from_client_dict = self.listen()
                    self.response_to_client(handle_types_message_client_module.process(new_password_from_client_dict))

                else:
                    self.response_to_client({"type": "forgot_password", "otp_valid": "invalid"})
            elif client_message_dict["type"] == "load_profile_image":
                self.response_to_client(handle_types_message_client_module.process(client_message_dict))
                self.response_to_client(handle_types_message_client_module.process(client_message_dict),
                                        large_data=True)
            else:
                self.response_to_client(handle_types_message_client_module.process(client_message_dict))

    def response_to_client(self, message: dict, large_data: bool = False):
        if large_data is False:
            response_to_client_message_json_string = json.dumps(message)
            response_to_client_message_json_encrypted_bytes = Cipher_module.encrypt(
                response_to_client_message_json_string)
            response_to_client_message_header_int = len(response_to_client_message_json_encrypted_bytes)
            response_to_client_message_header_bytes = response_to_client_message_header_int.to_bytes(4, "big")
            self.server_handle_client_socket.send(response_to_client_message_header_bytes)
            self.server_handle_client_socket.send(response_to_client_message_json_encrypted_bytes)
        else:
            self.server_handle_client_socket.send(Handle_android_app_socket.large_data)
            Handle_android_app_socket.large_data = None

    def listen(self):
        header_length_bytearray = bytearray(4)
        try:
            self.server_handle_client_socket.recv_into(header_length_bytearray, 4)
        except ConnectionError:
            print("client force close connection")
            self.server_handle_client_socket.close()
            return None

        header_length_int = int.from_bytes(header_length_bytearray, "big")
        buffer_data_byte = b''
        while len(buffer_data_byte) < header_length_int:
            chunk = self.server_handle_client_socket.recv(header_length_int)
            buffer_data_byte += chunk
        message_encrypted_bytes = buffer_data_byte
        try:
            message_plaintext_str = Cipher_module.decrypt(message_encrypted_bytes)
        except ValueError:
            print("Android close conenction")
            return
        client_message: dict = json.loads(message_plaintext_str)
        return client_message


class Server_core:
    handle_client_connection_list = []

    def __init__(self, server_address: str, server_port: int):
        self.SERVER_ADDRESS_TUPLE = (server_address, server_port)
        self.server_socket = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
        self.server_socket.bind(self.SERVER_ADDRESS_TUPLE)

    def listen_establish_from_new_client(self, type_client: str, listen: bool = False):
        if listen is False:
            return

        self.server_socket.listen(5)
        if type_client == "android_app":
            print("Server is listening android app")
            while True:
                new_client_socket, client_address = self.server_socket.accept()
                new_handle_client_connection = Handle_android_app_socket(new_client_socket)
                Server_core.handle_client_connection_list.append(new_handle_client_connection)
                print(f"connect from android app ip: {client_address}")
        elif type_client == "raspberry_app":
            print("Server is listening raspberry app")
            while True:
                new_client_socket, client_address = self.server_socket.accept()
                new_handle_client_connection = Handle_raspberry_app_socket(new_client_socket)
                Server_core.handle_client_connection_list.append(new_handle_client_connection)
                print(f"connect from raspberry app ip: {client_address}")
