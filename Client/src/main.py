import json
import time
import requests

url_encoded_data = {
    "type": "update_temp",
    "temp": "100",
    "time_primary": int(time.time())
}
request = requests.post("http://server.uitprojects.com/update_temp", data=url_encoded_data)
print(request.json())
