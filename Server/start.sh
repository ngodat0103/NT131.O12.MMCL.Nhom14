docker run -d --name servernhung --network=mynet2 \
-e symmetric_key=$symmetric_key -e \
username_mysql=$username_mysql -e password_mysql=$password_mysql -e SMTP_PASSWORD=$SMTP_PASSWORD -e SMTP_USERNAME=$SMTP_USERNAME \
ngodat0103/nhungproject:server
