import mysql.connector.errors

from database_statements_module import general_statements
import database_module
from hash_password_module import hash_password


def authentication(argument: dict) -> dict[str, str]:
    full_statement = general_statements["authentication"].format(username_primary=argument["username_primary"],
                                                                 hashed_password=hash_password(argument["password"]))
    response_from_mysql = database_module.access_database(full_statement)
    if len(response_from_mysql) != 0:
        return {"login": "success"}
    else:
        return {"login": "failed"}


def create_account(argument: dict) -> dict[str, str]:
    full_statement = general_statements["create_account"].format(username_primary=argument["username_primary"],
                                                                 hashed_password=hash_password(argument["password"]),
                                                                 email=argument["email"]
                                                                 )
    response_from_mysql = None
    try:
        response_from_mysql = database_module.access_database(full_statement)
    except mysql.connector.errors.IntegrityError:
        return {"create_account": "failed_because_username_exist"}
    return {"create_account": "successful"}


type_client_message = {
    "authentication": authentication,
    "create_account": create_account
}


def process(client_message: dict):
    print(client_message["type"])
    return type_client_message[client_message["type"]](client_message)
