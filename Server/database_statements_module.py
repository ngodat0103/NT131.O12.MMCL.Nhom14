general_statements: dict[str, str] = {
    'authentication_token': "select * from devices "
                            "join account on devices.username_foreignkey = account.username_primary "
                            "where refresh_token='{refresh_token}'",
    'authentication_credential': "select username_primary from mobile_project.account "
                                 "where username_primary='{username_primary}' and hashed_password='{hashed_password}'",
    'forgot_password': "SELECT * FROM mobile_project.account "
                       "where email = '{email}' and username_primary = '{username_primary}'",
    'change_new_password': "UPDATE `mobile_project`.`account` "
                           "SET `hashed_password` = '{new_hashed_password}' "
                           "WHERE (`username_primary` = '{username_primary}' and `email` = '{email}');",
    'upload_image_profile': "UPDATE `mobile_project`.`account`"
                            " SET `image_profile` = %s "
                            "WHERE (`username_primary` = '{username_primary}');",
    'load_profile_image': "SELECT image_profile FROM mobile_project.account "
                          "join devices on username_foreignkey = username_primary "
                          "where refresh_token = '{refresh_token}'",
    'get_weather': "SELECT * FROM mobile_project.weather_api order by date_primarykey desc",
    'update_token': "INSERT INTO `mobile_project`.`devices`(`uuid`,`device_name`,`username_foreignkey`,`refresh_token`)"
                    "VALUES('{uuid}','{device_name}','{username_foreignkey}','{refresh_token}');"}
