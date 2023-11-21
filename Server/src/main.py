from api import *
import uvicorn
import threading
from database_module import lock
from time import sleep


def tmp_clean_up(lock: threading.Lock):
    while True:
        with lock:
            tmp_path = os.getcwd() + "/tmp/"
            for current_file in os.listdir(tmp_path):
                os.remove(tmp_path + current_file)
        sleep(10)


clean_up_thread = threading.Thread(target=tmp_clean_up, args=(lock,))
clean_up_thread.start()
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=80)
