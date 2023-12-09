from threading import Lock

command_code = 304
iot_delay = 0
share_lock = Lock()
device = "null"

REQUEST_STATUS = 100
MAKE_CHANGES = 200
REBOOT_ESP = 0
KEEP_CONFIG = 304
REBOOT_RAS = -1

