from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
import base64
from Crypto.Util.Padding import pad
from Crypto.Util.Padding import unpad
import os
class AES_module:
    key_bytes = base64.b64decode(os.getenv("symmetric_key"))
    cipher_ecbmode = AES.new(key_bytes,AES.MODE_ECB)
    @staticmethod
    def generating_key():
        key_bytes = get_random_bytes(32)
        print(base64.b64encode(key_bytes).decode())
    @staticmethod
    def encrypt(plaintext_string:str) ->str:
        plaintext_bytes = plaintext_string.encode()
        plaintext_padding_bytes = pad(plaintext_bytes,16)
        encrypted_plaintext_bytes = AES_module.cipher_ecbmode.encrypt(plaintext_padding_bytes)
        encrypted_plaintext_base64_string = base64.b64encode(encrypted_plaintext_bytes).decode()
        return encrypted_plaintext_base64_string
    @staticmethod
    def decryt(ciphertext_string:str)->str:
        cipher_text_bytes = base64.b64decode(ciphertext_string)
        plaintext_bytes = AES_module.cipher_ecbmode.decrypt(cipher_text_bytes)
        plaintext_unpadding_bytes = unpad(plaintext_bytes,16)
        plaintext_string = plaintext_unpadding_bytes.decode()
        return plaintext_string

    def test_method(self):
        pass


