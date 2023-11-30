sudo chown 799:799 -R .mytb-logs/
sudo chown 799:799 -R .mytb-data/
sudo chmod 700 -R .mytb-data/db/
docker run -d --network=mynet2 -v $(realpath .mytb-data):/data \
-v $(realpath .mytb-logs):/var/log/thingsboard --name thingsboard --restart always thingsboard/tb-postgres
