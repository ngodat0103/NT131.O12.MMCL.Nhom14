import socket
from threading import Lock
import platform

OS = platform.platform()
share_lock = Lock()
is_make_change = False
device_alive = False
delay = 1000
reset = False
REQUEST_STATUS = 100
MAKE_CHANGES = 200
KEEP_CONFIG = 304
REBOOT_ESP = 0
REBOOT_RAS = -1


def receive(length: int, current_socket: socket.socket) -> bytes:
    byte_read = 0
    actual_data = b""
    while byte_read < length:
        try:
            actual_data += current_socket.recv(length - byte_read)
        except ConnectionError:
            return b""
        if actual_data == b"":
            return b""
        byte_read = len(actual_data)
    return actual_data
