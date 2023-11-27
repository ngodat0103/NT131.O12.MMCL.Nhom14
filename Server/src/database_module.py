import json

import mysql.connector
import os

USERNAME = os.getenv("username_mysql")
PASSWORD = os.getenv("password_mysql")
HOST = "app.mariadb.uitprojects.com"
PORT = 3306
DATABASE = "mobile_project"
SSL_CERT = os.getenv("ssl_client_cert")
SSL_key = os.getenv("ssl_client_key")
from threading import Lock

mysql_Lock = Lock()



def access_database(statement: str, param_any=None):
    with mysql_Lock:
        mysql_connection = mysql.connector.connect(user=USERNAME, password=PASSWORD,
                                                   host=HOST,
                                                   database='mobile_project',
                                                   ssl_cert=SSL_CERT,

                                                   ssl_key=SSL_key, )
        execute_command_interpreter = mysql_connection.cursor()
        execute_command_interpreter.execute(statement, param_any)
        response_tuple = execute_command_interpreter.fetchall()
        mysql_connection.commit()
        mysql_connection.disconnect()
        mysql_connection.close()
    return response_tuple


def test():
    response = access_database("select time_primary,temperature,humidity from mobile_project.raspberry")
    body = {
        "type": "history",
        "from": 123,
        "to": 1234,
        "data": []
    }
    for current in response:
        body["data"].append({
            "time": current[2],
            "temperature": current[0],
            "humidity": current[1]
        })

    body_str = json.dumps(body)
    return body_str

