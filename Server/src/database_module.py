import json

import mysql.connector
import os

USERNAME = os.getenv("username_mysql")
PASSWORD = os.getenv("password_mysql")
HOST = "mariadb"
PORT = 3306
DATABASE = "mobile_project"
SSL_CERT = os.getenv("ssl_client_cert")
SSL_key = os.getenv("ssl_client_key")
from threading import Lock

mysql_Lock = Lock()
mysql_connection = mysql.connector.connect(user=USERNAME, password=PASSWORD,
                                           host=HOST,
                                           database='mobile_project',
                                           ssl_cert=SSL_CERT,

                                           ssl_key=SSL_key, )


def access_database(statement: str, param_any=None):
    with mysql_Lock:
        while not mysql_connection.is_connected():
            print("Lost connection to mysql, reconnect in 3 seconds")
            mysql_connection.reconnect(delay=3)
        execute_command_interpreter = mysql_connection.cursor()
        execute_command_interpreter.execute(statement, param_any)
        response_tuple = execute_command_interpreter.fetchall()
        mysql_connection.commit()
    return response_tuple
