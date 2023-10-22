import threading
import api_weatherdata_module
from server_core import *
server_android = Server_core("0.0.0.0.", 2509)
server_raspberry = Server_core("0.0.0.0", 2590)
thread1 = threading.Thread(target=server_android.listen_establish_from_new_client, args=("android_app", True))
thread2 = threading.Thread(target=server_raspberry.listen_establish_from_new_client, args=("raspberry_app", True))
thread1.start()
thread2.start()
