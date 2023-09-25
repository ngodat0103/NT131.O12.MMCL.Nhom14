general_statements: dict[str, str] = {
    'authentication': "SELECT * FROM mobile_project.account "
                      "where username_primary='{username_primary}' and "
                      "hashed_password ='{hashed_password}' ",
    'create_account': "INSERT INTO `mobile_project`.`account`(`username_primary`,`hashed_password`,`email`)"
                      "VALUES('{username_primary}','{hashed_password}','{email}');"}
