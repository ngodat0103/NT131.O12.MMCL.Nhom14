from share import *
from time import sleep
from threading import Thread
import share


def listen_master(current_lock: Lock, func):
    master_socket = socket.socket(socket.AF_INET, type=socket.SOCK_STREAM)
    master_socket.connect(("servernhung", 2509))

    master_socket.settimeout(120)

    def reconnect() -> socket.socket:
        master_socket.shutdown(socket.SHUT_RDWR)
        master_socket.close()
        new_socket = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
        new_socket.settimeout(120)
        new_socket.connect(("servernhung", 2509))
        return new_socket

    while True:
        try:
            with current_lock:
                is_device_alive = share.device_alive
            if is_device_alive is False:
                master_socket.send(False.to_bytes(length=1, byteorder="little", signed=False))
                sleep(2)
                continue
            else:
                master_socket.send(True.to_bytes(length=1, byteorder="little", signed=False))
                command_code_bytes = receive(4, master_socket)
        except:
            print("Some thing is not right from master, try to reconnect in 3 seconds")
            sleep(3)
            master_socket = reconnect()
            continue
        if command_code_bytes == b"":
            try:
                master_socket = reconnect()
                continue
            except socket.error:
                print("Connection reset error, try to reconnect in 3 seconds")
                sleep(3)
                continue

        command_code_int = int.from_bytes(command_code_bytes, byteorder="little", signed=False)
        if command_code_int == 200:
            new_time_delay_bytes = receive(4, master_socket)
            new_time_delay_int = int.from_bytes(new_time_delay_bytes, byteorder="little", signed=False)
            print(f"set new esp8266 new time delay: {new_time_delay_int}")
            func(new_time_delay_int)
            master_socket.send(True.to_bytes(length=1, byteorder="little", signed=False))
        sleep(2)


def changes(new_delay):
    with lock:
        share.delay = new_delay
        share.is_make_change = True


listen_master_thread = Thread(target=listen_master, args=(lock, changes))
