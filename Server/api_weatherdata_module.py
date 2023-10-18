import os
from datetime import datetime
import database_module
import smtp


class WeatherData:
    def __init__(self, coord, weather, base, main, visibility, wind, clouds, dt):
        self.coord = coord
        self.weather = weather
        self.base = base
        self.main = main
        self.visibility = visibility
        self.wind = wind
        self.clouds = clouds
        self.dt = dt


class Coord:
    def __init__(self, lon, lat):
        self.lon = lon
        self.lat = lat


class Weather:
    def __init__(self, id, main, description, icon):
        self.id = id
        self.main = main
        self.description = description
        self.icon = icon


class Main:
    def __init__(self, temp, feels_like, temp_min, temp_max, pressure, humidity):
        self.temp = temp
        self.feels_like = feels_like
        self.temp_min = temp_min
        self.temp_max = temp_max
        self.pressure = pressure
        self.humidity = humidity


class Wind:
    def __init__(self, speed, deg):
        self.speed = speed
        self.deg = deg


class Clouds:
    def __init__(self, all):
        self.all = all


import requests

import datetime


def update_history_weather_data(start_unix_time_int: int, end_unix_time_int:int):
    timestamp_interval_int = 518400

    fullstatement = (
        "INSERT INTO weather_data (time_primarykey,dt, temp, feels_like, pressure, humidity, temp_min, temp_max, "
        "wind_speed"
        ", wind_deg, wind_gust, clouds_all, weather_id, weather_main, weather_description, weather_icon)"
        "VALUES (%s,%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s);")
    base_url = "https://history.openweathermap.org/data/2.5/history/city"
    headers = {
        "accept": "application/json"
    }
    current_unix_time_int = start_unix_time_int
    while current_unix_time_int < end_unix_time_int:
        params = {
            "appid": os.getenv("appid"),
            "lon": "106.8031",
            "units": "metric",
            "lat": "10.8698",
            "start": str(current_unix_time_int),
            "end": str(current_unix_time_int + timestamp_interval_int)
        }

        response = requests.get(base_url, params=params, headers=headers)
        json_data = response.json()
        print(json_data)
        weather_data_list = json_data["list"]
        for weather_data_list_row in weather_data_list:
            timestamp = datetime.datetime.fromtimestamp(weather_data_list_row['dt'])
            time_str = timestamp.strftime("%d-%m-%Y: %H:%M:%S")
            gust_check = weather_data_list_row['wind'].get("gust",None)
            weather_statistic_tuple = (
                time_str,
                weather_data_list_row['dt'],
                weather_data_list_row['main']['temp'],
                weather_data_list_row['main']['feels_like'],
                weather_data_list_row['main']['pressure'],
                weather_data_list_row['main']['humidity'],
                weather_data_list_row['main']['temp_min'],
                weather_data_list_row['main']['temp_max'],
                weather_data_list_row['wind']['speed'],
                weather_data_list_row['wind']['deg'],

                gust_check,
                weather_data_list_row['clouds']['all'],
                weather_data_list_row['weather'][0]['id'],
                weather_data_list_row['weather'][0]['main'],
                weather_data_list_row['weather'][0]['description'],
                weather_data_list_row['weather'][0]['icon'],
            )
            database_module.access_database(fullstatement, weather_statistic_tuple)
        print(datetime.datetime.fromtimestamp(current_unix_time_int).strftime("%d-%m-%Y: %H:%M:%S"))
        current_unix_time_int += timestamp_interval_int


# def notify_rain_to_user():
#     response_from_mysql = database_module.access_database("SELECT email  FROM mobile_project.account;")
#     email_users_dict:list = [item[0] for item in response_from_mysql]
#     if weather_data.weather[0].id < 600:
#         for email in email_users_dict:
#             smtp.send_email_notify_rain(email)
