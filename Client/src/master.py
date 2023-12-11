import socket

from share import *
from time import sleep
from threading import Thread
import share
from share import share_lock
from cipher_module import *
import os

def listen_master():
    master_socket = socket.socket(socket.AF_INET, type=socket.SOCK_STREAM)
    master_socket.connect(("servernhung", 3389))

    master_socket.settimeout(120)

    while True:

        command_code_bytes = receive(16, master_socket)
        if command_code_bytes == b"":
            break
        command_code_bytes = decrypt(command_code_bytes)

        command_code_int = int.from_bytes(command_code_bytes, byteorder="little", signed=True)

        if command_code_int == share.KEEP_CONFIG:
            continue

        if command_code_int == share.REQUEST_STATUS:
            with share_lock:
                master_socket.send(encrypt(share.device_alive.to_bytes(byteorder="little", signed=True, length=1)))

        elif command_code_int == share.MAKE_CHANGES:
            new_time_delay_bytes = receive(16, master_socket)
            new_time_delay_bytes = decrypt(new_time_delay_bytes)
            new_time_delay_int = int.from_bytes(new_time_delay_bytes, byteorder="little", signed=True)
            print(f"set esp8266 new time delay: {new_time_delay_int}ms")
            with share_lock:
                share.is_make_change = True
                share.delay = new_time_delay_int
        elif command_code_int == share.REBOOT_ESP:
            print("Reset esp")
            with share_lock:
                share.is_make_change = True
                share.reset = True
        elif command_code_int == share.REBOOT_RAS:
            print("Ras reboot")
            if share.OS.__contains__("Linux"):
                os.system("systemctl reboot")

        sleep(2)


def changes(new_delay):
    with share_lock:
        share.delay = new_delay
        share.is_make_change = True


def create_master_thread():
    return Thread(target=listen_master)
