import asyncio
import os
from typing import Annotated
import numpy
from fastapi import FastAPI, Form, Request, HTTPException, Response, status
from starlette.responses import StreamingResponse, FileResponse

import share
from handle_types_message_client_module import *
from threading import Lock

MESSAGE_STREAM_DELAY = 1  # second
MESSAGE_STREAM_RETRY_TIMEOUT = 15000
from share import share_lock

TEMP_PATH = os.getcwd() + "/tmp/"
app = FastAPI()

import csv

file_Lock = Lock()


@app.get('/live-data')
async def message_stream(request: Request):
    async def generator():
        latest: (float, float) = None
        while True:
            response = database_module.access_database(general_statements["get_temp"])
            response = response[0]
            if response != latest:
                temp_float32 = numpy.float32(response[0])
                humidity_float32 = numpy.float32(response[1])
                time_int = response[2]
                data_bytes = b""
                data_bytes += temp_float32.tobytes()
                data_bytes += humidity_float32.tobytes()
                data_bytes += time_int.to_bytes(byteorder="little", signed=True, length=4)
                latest = response
                yield data_bytes
            await asyncio.sleep(1)

    return StreamingResponse(generator())


@app.post("/user/authentication", tags=['User'])
async def login(response: Response, username: Annotated[str, Form()],
                password: Annotated[str, Form()],
                device_name: Annotated[str, Form()] = "test device",
                refresh_token: Annotated[str, Form()] = "None",
                ):
    result = authentication_credential(username, password, device_name, refresh_token)
    if type(result) == dict:
        return result
    response.status_code = status.HTTP_401_UNAUTHORIZED
    return HTTPException(status_code=401, detail="invalid username or password")


@app.post("/registration", tags=['User'])
async def registration(response: Response,
                       username: Annotated[str, Form()],
                       password: Annotated[str, Form()],
                       email: Annotated[str, Form()]
                       ):
    result = create_account(username, password, email)
    if result is True:
        response.status_code = status.HTTP_204_NO_CONTENT
        return
    response.status_code = status.HTTP_422_UNPROCESSABLE_ENTITY
    return HTTPException(detail="username existed", status_code=422)


@app.get("/image", tags=['User'])
async def get_image(refresh_token: str):
    image_bytes = load_profile_image(refresh_token)
    if image_bytes is None:
        return {
            "error": "account didn't have image"
        }
    return Response(image_bytes, media_type="image/png")


@app.get("/image/download", tags=['User'])
async def get_image(refresh_token: str):
    image_bytes = load_profile_image(refresh_token)
    if image_bytes is None:
        return {
            "error": "account didn't have image or invalid token"
        }
    with file_Lock:
        with open(TEMP_PATH + "tmp.png", mode="wb") as temp_file:
            temp_file.write(image_bytes)
        return FileResponse(temp_file.name, media_type="image/png", filename="userprofile.png")


@app.get("/user/notification", tags=['User'])
async def user_setting(response: Response, refresh_token: str):
    response_mysql = database_module.access_database(general_statements["user_notification"], (refresh_token,))
    if len(response_mysql) == 0:
        response.status_code = status.HTTP_403_FORBIDDEN
        return HTTPException(status_code=403, detail="token invalid")
    return {
        "notification": True if response_mysql[0][0] == 1 else False
    }


@app.post("/user/set-notification", tags=['User'])
async def user_setting(response: Response,
                       refresh_token: Annotated[str, Form()],
                       notification: Annotated[bool, Form()]
                       ):
    response_mysql = database_module.access_database(general_statements["authentication_token"], (refresh_token,))
    if len(response_mysql) == 0:
        response.status_code = status.HTTP_403_FORBIDDEN
        return HTTPException(status_code=403, detail="invalid token")

    response_mysql = database_module.access_database(general_statements["set_user_notification"],
                                                     (notification, refresh_token))
    response.status_code = status.HTTP_204_NO_CONTENT
    return None


@app.get("/current_temp", tags=['Sensors'])
async def get_current_temp():
    return current()


@app.get("/history", tags=['Sensors'])
async def get_history(left: int = 0, right: int = 2147483647, order: str = "desc", limit: int = 1000):
    if order == "desc" or order == "asc":
        return history(left, right, order, limit)
    else:
        raise HTTPException(status_code=400, detail="order is invalid")


@app.get("/history/download", tags=['Sensors'])
async def get_history(left: int = 0, right: int = 2147483647, order: str = "desc", limit: int = 1000):
    if order == "desc" or order == "asc":
        with file_Lock:
            with open(TEMP_PATH + "temp.csv", mode="w", newline='') as temp_csv:
                writer = csv.writer(temp_csv)
                writer.writerow(("Temperature", "Humidity", "Time"))
                writer.writerows(history(left, right, order, limit, True))
            return FileResponse(TEMP_PATH + "temp.csv", filename="history data.csv", media_type="text/csv")
    else:
        raise HTTPException(status_code=400, detail="order is invalid")


@app.get("/device/setting", tags=['Devices'])
async def get_device_setting(response: Response, device_name: str = "esp8266"):
    if len(database_module.access_database(general_statements["get_device_info"], (device_name,))) == 0:
        response.status_code = status.HTTP_422_UNPROCESSABLE_ENTITY
        return HTTPException(status_code=422, detail="device not found")
    return device_setting(device_name)


@app.post("/update_temp", tags=['Sensors'])
async def temp_update(response: Response, time_primary: Annotated[int, Form()],
                      temperature: Annotated[float, Form()],
                      humidity: Annotated[float, Form()]
                      ):
    result = update_temp(time_primary, temperature, humidity)
    if result is True:
        response.status_code = status.HTTP_204_NO_CONTENT
        return
    response.status_code = status.HTTP_422_UNPROCESSABLE_ENTITY
    return HTTPException(status_code=422, detail="duplicate primary key")


@app.post("/device/set-setting", tags=['Devices'])
async def set_setting(response: Response,
                      refresh_token: Annotated[str, Form()],
                      time_delay: Annotated[int, Form()] = None,
                      war_temp: Annotated[float, Form()] = None,
                      war_humidity: Annotated[float, Form()] = None,
                      reboot: Annotated[bool, Form()] = False,
                      device_name: Annotated[str, Form()] = "esp8266"):
    response_mysql = database_module.access_database(general_statements["authentication_token"],
                                                     (refresh_token,))
    if len(response_mysql) == 0:
        response.status_code = status.HTTP_403_FORBIDDEN
        return HTTPException(status_code=403, detail="invalid token")
    is_admin_account = bool(response_mysql[0][0])

    if is_admin_account is False:
        response.status_code = status.HTTP_401_UNAUTHORIZED
        return HTTPException(status_code=401, detail="Permission deny")
    if len(database_module.access_database(general_statements["get_device_info"], (device_name,))) == 0:
        response.status_code = status.HTTP_404_NOT_FOUND
        return HTTPException(status_code=404, detail="device not found")

    with share_lock:
        if reboot:
            if device_name == "esp8266":
                share.command_code = share.REBOOT_ESP
            elif device_name == "ras":
                share.command_code = share.REBOOT_RAS

            response.status_code = status.HTTP_200_OK
            return HTTPException(status_code=200,detail=f"Because Reboot is True , All the remaining fields are ignored.")


    response.status_code = status.HTTP_204_NO_CONTENT

    if time_delay is not None:
        if time_delay == 0:
            response.status_code = status.HTTP_400_BAD_REQUEST
            return HTTPException(status_code=400, detail="Time delay can't not equal 0")
        share.command_code = share.MAKE_CHANGES
        share.device = device_name
        share.iot_delay = time_delay
        database_module.access_database("update iot_setting set delay = %s where device_name = %s",
                                        (time_delay, device_name)
                                        )

    if war_temp is not None and war_humidity is None:
        database_module.access_database(
            "update iot_setting set war_temp = %s where device_name = %s", (war_temp, device_name)
        )
        return
    elif war_humidity is not None and war_temp is None:
        database_module.access_database(
            "update iot_setting set war_humidity = %s where device_name = %s", (war_humidity, device_name)
        )
        return
