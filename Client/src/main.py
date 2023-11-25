from slave import listen_from_slave_Thread
from master import listen_master_thread
from time import sleep
listen_from_slave_Thread.start()
sleep(5)
listen_master_thread.start()