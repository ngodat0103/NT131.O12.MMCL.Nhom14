import base64
import threading

import mysql.connector.errors
import pandas
from random_otp.generator import generate_numeric_otp
import cipher_module
from cipher_module import hash_password
import smtp
from database_statements_module import general_statements
import database_module
from sqlalchemy import create_engine
from time import time
import datetime
import pandas as pd
from smtp import send_email_warning


def authentication_credential(username: str, password: str, device_name: str, refresh_token: str) -> dict[
                                                                                                         str, str] | None:
    if refresh_token == "None":
        response = database_module.access_database(
            general_statements["authentication_credential"], (username, hash_password(password))
        )
        if len(response) == 0:
            return None
        else:
            refresh_token_str = cipher_module.generate_random_token(32)
            uuid_str = cipher_module.generate_random_token(10)
            database_module.access_database(general_statements["update_token"], (
                uuid_str,
                device_name,
                username,
                refresh_token_str
            )
                                            )
            return {"type": "login",
                    "status": "success",
                    "refresh_token": refresh_token_str,
                    }


def create_account(username: str, password: str, email: str) -> bool:
    try:
        database_module.access_database(general_statements["create_account"],
                                        (
                                            username,
                                            hash_password(password),
                                            email
                                        )
                                        )
    except mysql.connector.errors.IntegrityError as error:
        return False

    database_module.access_database("insert into account_setting(`username`) values(%s)", (username,))
    return True


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


def load_profile_image(refresh_token: str) -> None | bytes:
    response = database_module.access_database(general_statements["load_profile_image"],
                                               (refresh_token,))
    if len(response) == 0:
        return None
    load_image_bytes: bytes = response[0][0]

    return load_image_bytes


def get_weather_data(argument: dict):
    fullstatement: str = general_statements["get_weather"]
    engine = create_engine('mysql+mysqlconnector://', creator=lambda: database_module.mysql_connection)
    response_from_mysql_dataframe = pd.read_sql(fullstatement, engine)
    weather_data_firstrow_dataframe: pandas.DataFrame = response_from_mysql_dataframe.iloc[0]
    return {"type": "get_weather", "status": "success", "weather_data": weather_data_firstrow_dataframe.to_json()}


def update_temp(time_primary: int, temperature: float, humidity: float) -> bool:
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
        return False

    response = database_module.access_database(general_statements["check_limit"], (temperature,))
    if len(response) == 1:
        response = database_module.access_database(general_statements["get_emails"])
        emails = [email[0] for email in response]
        send_email_Thread = threading.Thread(target=send_email_warning, args=(emails, temperature, humidity))
        send_email_Thread.start()

    return True


def current():
    response_from_mysql = database_module.access_database(general_statements["get_temp"])
    return {
        "type": "current time",
        "time": response_from_mysql[0][2],
        "temperature": response_from_mysql[0][0],
        "humidity": response_from_mysql[0][1]
    }


def history(left, right, order, limit, download=False):
    if limit == 0:
        response = database_module.access_database(general_statements["history"].format(limit="", order=order),
                                                   (left, right))
    else:
        response = database_module.access_database(
            general_statements["history"].format(limit=f"limit {limit}", order=order), (left, right))

    if download is True:
        return response
    body = {
        "type": "history",
        "from": left,
        "to": right,
        "limit": limit,
        "length elements": None,
        "order": order,
        "data": []
    }
    for current in response:
        body["data"].append({
            "time": current[2],
            "temperature": current[0],
            "humidity": current[1]
        })
    body["length elements"] = len(response)

    return body


def device_setting(device_name: str):
    response = database_module.access_database(general_statements["get_device_info"], (device_name,))
    return {
        "Device_name": device_name,
        "Delay time": str(response[0][1]) + "ms",
        "Warning limit temperature": response[0][2],
        "Warning limit humidity": response[0][3]
    }


def change_password(argument: dict):
    try:
        response = database_module.access_database(
            general_statements["change_password"],
            (hash_password(argument["new_password"]), argument["username_primary"]))
    except:
        return {"status": "Something is not right"}
    return {"status": "success", "type": "reset password"}
