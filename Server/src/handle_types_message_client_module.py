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


def authentication_credential(username_primary: str, password: str, device_name: str, refresh_token: str) -> dict[
    str, str]:
    if refresh_token == "None":
        response = database_module.access_database(
            general_statements["authentication_credential"], (username_primary, hash_password(password))
        )
        if len(response) == 0:
            return {"type": "login", "status": "failed", "reason": "username or password invalid"}
        else:
            refresh_token_str = cipher_module.generate_random_token(32)
            uuid_str = cipher_module.generate_random_token(10)
            database_module.access_database(general_statements["update_token"], (
                uuid_str,
                device_name,
                username_primary,
                refresh_token_str
            )
                                            )
            return {"type": "login",
                    "status": "success",
                    "refresh_token": refresh_token_str,
                    "image_profile": base64.b64encode(response[0][3]).decode()
                    }


def create_account(username_primary: str, password: str, email: str) -> dict[str, str]:
    try:
        database_module.access_database(general_statements["create_account"],
                                        (
                                            username_primary,
                                            hash_password(password),
                                            email
                                        )
                                        )
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
            return {"type": "reset password", "status": "failed", "reason": "Account didn't exist"}
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


def update_temp(time_primary: int, temperature: float, humidity: float):
    time_readable_str = datetime.datetime.fromtimestamp(time_primary).strftime("%d-%m-%Y: %H:%M:%S ")
    try:
        database_module.access_database(general_statements["update_temp"], (
            time_primary,
            time_readable_str,
            temperature,
            humidity
        )
                                        )
    except mysql.connector.errors.IntegrityError:
        return {"type": "update_temp", "status": "can't update", "reason": "duplicate primary key"}
    return {"type": "update_temp", "status": "Ok"}


def get_temp():
    response_from_mysql = database_module.access_database(general_statements["get_temp"])
    return {
        "type": "get_temp",
        "time": response_from_mysql[0][2],
        "temperature": response_from_mysql[0][0],
        "humidity": response_from_mysql[0][1]
    }


def change_password(argument: dict):
    try:
        response = database_module.access_database(
            general_statements["change_password"],
            (hash_password(argument["new_password"]), argument["username_primary"]))
    except:
        return {"status": "Something is not right"}
    return {"status": "success", "type": "reset password"}
