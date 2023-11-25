import json
import threading
import time

import requests
import numpy

from share import *

is_make_change = False
device_alive = False
manager_socket = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
manager_socket.bind(("0.0.0.0", 80))
manager_socket.listen(5)
delay = 1000


def is_device_alive():
    return device_alive


def changes(new_delay):
    global delay
    global is_make_change
    with lock:
        delay = new_delay
        is_make_change = True


def listen_from_slave():
    global is_make_change, device_alive
    print("Server is listening from Arduino")

    while True:
        slave_socket, ip_address = manager_socket.accept()
        slave_socket.settimeout(120)

        with lock:
            device_alive = True

        def make_changes(interval: int):
            global is_make_change
            with lock:
                slave_socket.send(int(interval).to_bytes(length=2, byteorder="little", signed=False))
                status_code_bytes = receive(2, slave_socket)
                status_code_int = int.from_bytes(status_code_bytes, byteorder="little")
                print("Status code:" + str(status_code_int))
                is_make_change = False
        while True:
            try:
                temp_bytes = receive(4, slave_socket)
                if temp_bytes == b"":
                    slave_socket.shutdown(socket.SHUT_RDWR)
                    slave_socket.close()
                    break

                humidity_bytes = receive(4, slave_socket)
                temp_float = numpy.frombuffer(temp_bytes, dtype=numpy.float32)
                humidity_float = numpy.frombuffer(humidity_bytes, dtype=numpy.float32)

                temp_str = str(temp_float[0])
                humidity_str = str(humidity_float[0])
                slave_socket.send(is_make_change.to_bytes(length=1, byteorder="little"))
                if is_make_change:
                    make_changes(delay)

                headers = {
                    "Content-type": "application/json"
                }
                json_dict = {
                    "humidity": humidity_str,
                    "temperature": temp_str,
                }
                json_data = json.dumps(json_dict)
                response1 = requests.post("http://192.168.1.204:9090/api/v1/xNX9FiLyWenmKNaj2pXV/telemetry",
                                          data=json_data,
                                          headers=headers)

                headers["Content-type"] = "application/x-www-form-urlencoded"

                json_dict = {
                    "humidity": humidity_str,
                    "temperature": temp_str,
                    "time_primary": int(time.time())
                }
                response2 = requests.post("http://192.168.1.205/update_temp",
                                          data=json_dict,
                                          headers=headers)

                print(humidity_str)
                print(temp_str)


            except socket.timeout:
                slave_socket.shutdown(socket.SHUT_RDWR)
                slave_socket.close()
                print("close connection because timeout")
                break


listen_from_slave_Thread = threading.Thread(target=listen_from_slave)
