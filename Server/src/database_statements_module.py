general_statements: dict[str, str] = {
    'authentication_token': "select * from devices "
                            "join account on devices.username_foreignkey = account.username_primary "
                            "where refresh_token='{refresh_token}'",
    'authentication_credential': "select * from mobile_project.account "
                                 "where username_primary=%s and hashed_password=%s",
    'forgot_password': "SELECT username_primary,email FROM mobile_project.account "
                       "where username_primary = %s",
    'change_new_password': "UPDATE `mobile_project`.`account` "
                           "SET `hashed_password` = '{new_hashed_password}' "
                           "WHERE (`username_primary` = '%s' and `email` = '%s');",
    'upload_image_profile': "UPDATE `mobile_project`.`account`"
                            " SET `image_profile` = %s "
                            "WHERE (`username_primary` = '{username_primary}');",
    'load_profile_image': "SELECT image_profile FROM mobile_project.account "
                          "join devices on username_foreignkey = username_primary "
                          "where refresh_token = %s",
    'get_weather': "SELECT * FROM mobile_project.weather_api order by date_primarykey desc",
    'update_token': "INSERT INTO `mobile_project`.`devices`(`uuid`,`device_name`,`username_foreignkey`,`refresh_token`)"
                    "VALUES(%s,%s,%s,%s);",
    'update_temp': "insert into `mobile_project`.`raspberry`(`time_primary`,`time_readable`,`temperature`,`humidity`)VALUES(%s,%s,%s,%s)",
    'get_temp': "select temperature,humidity,time_primary from mobile_project.raspberry order by time_primary desc limit 1",
    "history":"select temperature,humidity,time_primary from mobile_project.raspberry where time_primary>%s and time_primary <%s "
              "order by time_primary {order} {limit} ",
    'create_account': "insert into mobile_project.account(`username_primary`,`hashed_password`,`email`) values(%s,%s,"
                      "%s)",
    "update_otp": "UPDATE `mobile_project`.`account` SET `reset_password` = '1',otp_code=%s,expire=%s WHERE (`username_primary` = %s);",
    "check_valid_otp": "select * from account where "
                       "reset_password = true and "
                       "otp_code =%s and "
                       "expire>%s and "
                       "username_primary=%s",
    "update_otp_android_project": "insert into otp_email(`UUID`,`email`,`otp_code`,`expire`) values (%s,%s,%s,%s);",
    "check_otp_android_project": "select * from otp_email where email=%s and otp_code=%s and expire > %s",
    "change_password": "update account set "
                       "hashed_password=%s,"
                       "reset_password=false,"
                       "expire=null,otp_code=null "
                       "where username_primary=%s"

}
