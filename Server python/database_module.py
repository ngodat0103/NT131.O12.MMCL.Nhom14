import mysql.connector
import os
from database_statements_module import general_statements
from hash_password_module import hash_password
USERNAME = os.getenv("username_mysql")
PASSWORD = os.getenv("password_mysql")
HOST = "app.mariadb.uitprojects.com"
PORT = 3306
DATABASE = "mobile_project"
SSL_CERT = os.getenv("ssl_client_cert")
SSL_key = os.getenv("ssl_client_key")

def access_database(statement:str):
    mysql_connection = mysql.connector.connect(user=USERNAME, password=PASSWORD,
                                      host=HOST,
                                      database='mobile_project',
                                      ssl_cert=SSL_CERT,
                                      ssl_key=SSL_key,)
    execute_command_interpreter = mysql_connection.cursor()
    execute_command_interpreter.execute(statement)
    response_tuple = execute_command_interpreter.fetchall()
    mysql_connection.commit()
    mysql_connection.close()
    return response_tuple


# execute_command_interpreter.execute(f"SELECT * FROM mobile_project.account WHERE "
#                                     "username_primary='test_username' "
#                                     "and "
#                                     f"hashed_password='{hash_password('123456789')}';")
# hashed_password = hash_password("mypassword")
# insert_command = (f"INSERT INTO `mobile_project`.`account`(`username_primary`,`hashed_password`)VALUES('test_username',"
#                   f"'{hashed_password}');")
# execute_command_interpreter.execute(insert_command)
# print(hash_password('123456789'))
# print(execute_command_interpreter.fetchall())
