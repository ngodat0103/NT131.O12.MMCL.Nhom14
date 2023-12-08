import json
import socket
import threading
import time
from datetime import datetime

import requests
import numpy

from share import *
import share

ras_socket = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
ras_socket.bind(("0.0.0.0", 80))
ras_socket.listen(5)
delay = 1000
import pytz

tz = pytz.timezone('Asia/Ho_Chi_Minh')
berlin_now = datetime.now(tz)


def vn_time() -> float:
    return datetime.now(tz).timestamp()


def handle_socket(current_socket: socket.socket, current_lock: threading.Lock):
    current_socket.settimeout(120)

    def make_changes(interval: int):
        current_socket.send(int(interval).to_bytes(length=2, byteorder="little", signed=False))
        status_code_bytes = receive(2, current_socket)
        status_code_int = int.from_bytes(status_code_bytes, byteorder="little")
        print("Status code:" + str(status_code_int))
        if status_code_int != 200:
            current_socket.shutdown(socket.SHUT_RDWR)
            print("Some thing is not right from esp8266, drop connection")
            current_socket.close()
            return
        share.is_make_change = False

    while True:
        try:
            temp_bytes = receive(4, current_socket)
            if temp_bytes == b"":
                current_socket.shutdown(socket.SHUT_RDWR)
                with current_lock:
                    share.device_alive = False
                current_socket.close()
                break

            humidity_bytes = receive(4, current_socket)
            temp_float = numpy.frombuffer(temp_bytes, dtype=numpy.float32)
            humidity_float = numpy.frombuffer(humidity_bytes, dtype=numpy.float32)

            temp_str = str(temp_float[0])
            humidity_str = str(humidity_float[0])
            with current_lock:
                current_socket.send(share.is_make_change.to_bytes(length=1, byteorder="little"))
                if share.is_make_change:
                    make_changes(share.delay)

            headers = {
                "Content-type": "application/x-www-form-urlencoded"
            }

            json_dict = {
                "humidity": humidity_str,
                "temperature": temp_str,
                "time_primary": int(vn_time())
            }
            print(datetime.now(tz).strftime("%d/%m/%Y, %H:%M:%S"))
            try:
                response = requests.post("http://servernhung/update_temp",
                                         data=json_dict,
                                         headers=headers, timeout=3)
            except requests.exceptions.ConnectionError:
                print("The server is down")

            print("Temperature: " + temp_str)
            print("Humidity: " + humidity_str + "\n")


        except current_socket.timeout:
            current_socket.shutdown(socket.SHUT_RDWR)
            current_socket.close()
            print("close connection because timeout")
            with current_lock:
                share.is_make_change = False
            break


def listen_from_slave():
    print("Server is listening from Arduino")

    while True:
        slave_socket, ip_address = ras_socket.accept()

        with lock:
            share.is_make_change = True
            share.device_alive = True

        handle_thread = threading.Thread(target=handle_socket, args=(slave_socket, lock))
        handle_thread.start()


listen_from_slave_Thread = threading.Thread(target=listen_from_slave)
