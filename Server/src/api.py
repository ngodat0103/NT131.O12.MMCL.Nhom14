from typing import Union, Annotated
import uvicorn
from fastapi import FastAPI, Form, Body
from pydantic import BaseModel

app = FastAPI()
from handle_types_message_client_module import *


@app.get("/")
def read_root():
    return {"Hello": "World"}


@app.post("/authentication")
async def login(username_primary: Annotated[str, Form()],
                password: Annotated[str, Form()],
                device_name: Annotated[str, Form()],
                refresh_token: Annotated[str, Form()]
                ):
    return authentication_credential(username_primary, password, device_name, refresh_token)


@app.post("/registration")
async def registration(
        username_primary: Annotated[str, Form()],
        password: Annotated[str, Form()],
        email: Annotated[str, Form()]
):
    return create_account(username_primary, password, email)


@app.get("/current_temp")
async def current_temp():
    return get_temp()


@app.post("/update_temp")
async def temp_update(temp: Annotated[str, Form()],
                      time_primary: Annotated[int, Form()]
                      ):
    return update_temp(time_primary, temp)


