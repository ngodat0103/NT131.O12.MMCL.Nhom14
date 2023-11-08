import json
import time
import requests


while True:
    url_encoded_data = {
        "type": "update_temp",
        "temp": input("Nhap nhiet do: "),
        "time_primary": int(time.time())
    }
    if url_encoded_data["temp"]=="0":
        break
    request = requests.post("http://server.uitprojects.com/update_temp", data=url_encoded_data)
    print(request.json())
