import base64
import io

import mysql.connector
import os

USERNAME = os.getenv("username_mysql")
PASSWORD = os.getenv("password_mysql")
HOST = "app.mariadb.uitprojects.com"
PORT = 3306
DATABASE = "mobile_project"
SSL_CERT = os.getenv("ssl_client_cert")
SSL_key = os.getenv("ssl_client_key")

mysql_connection = mysql.connector.connect(user=USERNAME, password=PASSWORD,
                                           host=HOST,
                                           database='mobile_project',
                                           ssl_cert=SSL_CERT,
                                           ssl_key=SSL_key, )


def access_database(statement: str, param_any=None):
    execute_command_interpreter = mysql_connection.cursor()
    print(statement)
    execute_command_interpreter.execute(statement, param_any)
    print(execute_command_interpreter.statement)
    response_tuple = execute_command_interpreter.fetchall()
    mysql_connection.commit()
    return response_tuple


def test():
    response_mysql = access_database("SELECT dt,temp,humidity FROM mobile_project.weather_data;")
    import requests

    for element in response_mysql:
        header = {
            "Content-type": "application/json"
        }
        body = {
            "ts": str(element[0]),
            "temperature": str(element[1]),
            "humidity": str(element[2])
        }
        response_http = requests.post("https://thingsboard.uitprojects.com/api/v1/xNX9FiLyWenmKNaj2pXV/telemetry",
                                      headers=header, json=body)
        print(element)
        print(response_http.status_code)
