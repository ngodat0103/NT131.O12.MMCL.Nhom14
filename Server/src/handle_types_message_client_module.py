import base64
import mysql.connector.errors
import pandas
from random_otp.generator import generate_numeric_otp
import cipher_module
import smtp
from database_statements_module import general_statements
import database_module
from cipher_module import hash_password
from sqlalchemy import create_engine
from time import time
import datetime
import pandas as pd


def authentication(argument: dict) -> dict[str, str]:
    refresh_token_str = argument.get("refresh_token")
    if refresh_token_str is None:
        full_statement_str = general_statements["authentication_credential"].format(
            username_primary=argument["username_primary"],
            hashed_password=hash_password(
                argument["password"]))
    else:
        full_statement_str = general_statements["authentication_token"].format(refresh_token=argument["refresh_token"])

    response_from_mysql = database_module.access_database(full_statement_str)
    if response_from_mysql:
        if refresh_token_str is None:
            refresh_token_str = cipher_module.generate_random_token(32)
            uuid_str = cipher_module.generate_random_token(10)
            update_token_fullstatement_str = general_statements["update_token"].format(
                username_foreignkey=argument["username_primary"], device_name=argument["device_name"],
                refresh_token=refresh_token_str, uuid=uuid_str)
            database_module.access_database(update_token_fullstatement_str)
            return {
                "type": "login",
                "status": "success",
                "refresh_token": refresh_token_str,
                "image_profile": base64.b64encode(response_from_mysql[0][3]).decode()
            }

        else:
            print(response_from_mysql)
            return {"type": "login",
                    "status": "success",
                    "refresh_token": refresh_token_str,
                    "image_profile": base64.b64encode(response_from_mysql[0][3]).decode()
                    }
    else:
        return {"type": "login", "status": "failed", "reason": "username or password invalid"}


def create_account(argument: dict) -> dict[str, str]:
    full_statement = general_statements["create_account"]
    params = (
        argument["username_primary"],
        argument["password"],
        argument["email"]
    )
    try:
        database_module.access_database(full_statement, params)
    except mysql.connector.errors.IntegrityError as error:
        print(str(error))
        return {"status": "can't create account", "reason": str(error)}
    return {"status": "successful"}


def forgot_password(argument: dict = None, check_otp=False):
    if check_otp is False:
        response = database_module.access_database(
            general_statements["forgot_password"],
            (argument["username_primary"],)
        )
        if len(response) == 1:
            random_otp = generate_numeric_otp(6)
            database_module.access_database(
                general_statements["update_otp"],
                (hash_password(random_otp), int(time() + 120), argument["username_primary"])
            )
            smtp.send_email_otp(random_otp, response[0][1])
        return {"type": "reset password", "status": "otp_sent", "expire": "in 2 minutes", "email": response[0][1]}
    else:
        response = database_module.access_database(
            general_statements["check_valid_otp"],
            (
                hash_password(str(argument["otp_code"])),
                int(time()),
                argument["username_primary"],
            )
        )
        if len(response) == 1:
            return {
                "type": "check valid otp",
                "status": "valid"
            }
        else:
            return {
                "type": "check valid otp",
                "status": "failed",
                "reason": "invalid or expired OTP"
            }


def forgot_password_mobile(email: str, check_otp=False, otp_code: str = None):
    if check_otp is False:
        random_otp = generate_numeric_otp(6)
        database_module.access_database(
            general_statements["update_otp_android_project"],
            (generate_numeric_otp(32), email, hash_password(random_otp),
             int(time()) + 120))
        smtp.send_email_otp(random_otp, email)
        return {"type": "reset password", "status": "otp_sent", "expire": "in 2 minutes"}
    else:
        response = database_module.access_database(
            general_statements["check_otp_android_project"],
            (email, hash_password(otp_code), int(time()))
        )
        if len(response) == 1:
            database_module.access_database("delete from otp_email where email = %s", (email,))
            return {
                "type": "check valid otp",
                "status": "valid"
            }
        else:
            return {
                "type": "check valid otp",
                "status": "failed",
                "reason": "invalid otp or expired"
            }


def upload_image_profile(argument: dict):
    image_encoded_base64_string: str = argument["image_encoded_base64_string"]
    image_bytes = base64.b64decode(image_encoded_base64_string)
    fullstatement = general_statements["upload_image_profile"].format(username_primary=argument["username_primary"])
    database_module.access_database(fullstatement, (image_bytes,))
    return {"type": "upload_image_profile", "status": "success"}


def load_profile_image(argument: dict):
    fullstatement: str = general_statements["load_profile_image"].format(refresh_token=argument["refresh_token"])

    response_from_mysql_list = database_module.access_database(fullstatement)
    load_image_bytes: bytes = response_from_mysql_list[0][0]

    return {"type": "load_profile_image", "status": "pending_download", "large_file_size": str(len(load_image_bytes)),
            "large_data": "true"}


def get_weather_data(argument: dict):
    fullstatement: str = general_statements["get_weather"]
    engine = create_engine('mysql+mysqlconnector://', creator=lambda: database_module.mysql_connection)
    response_from_mysql_dataframe = pd.read_sql(fullstatement, engine)
    weather_data_firstrow_dataframe: pandas.DataFrame = response_from_mysql_dataframe.iloc[0]
    return {"type": "get_weather", "status": "success", "weather_data": weather_data_firstrow_dataframe.to_json()}


def update_temp(argument: dict):
    fullstatement: str = general_statements["update_temp"]
    time_primary_int: int = int(argument['time_primary'])
    time_readable_str = datetime.datetime.fromtimestamp(time_primary_int).strftime("%d-%m-%Y: %H:%M:%S ")
    params = (time_primary_int, time_readable_str, argument['temp'])
    print(params)
    try:
        database_module.access_database(fullstatement, params)
    except mysql.connector.errors.IntegrityError:
        return {"type": "update_temp", "status": "can't update", "reason": "duplicate primary key"}
    return {"type": "update_temp", "status": "Ok"}


def get_temp(argument: dict = None):
    fullstatement: str = general_statements['get_temp']
    response_from_mysql = database_module.access_database(fullstatement)
    return {"type": "get_temp", "temp": response_from_mysql[0][0]}


def change_password(argument: dict):
    response = database_module.access_database(
        general_statements["change_password"],
        (hash_password(argument["new_password"]), argument["username_primary"])
    )
    print(response)


type_client_message = {
    "authentication": authentication,
    "create_account": create_account,
    "forgot_password": forgot_password,
    "upload_image_profile": upload_image_profile,
    "load_profile_image": load_profile_image,
    "get_weather_data": get_weather_data,
    "update_temp": update_temp,
    "get_temp": get_temp
}

test_forgot_password = {
    "username_primary": "test_account1",
    "email": "ngovuminhdat@gmail.com"
}
