docker network create -d bridge mynet2
source mariadb/start.sh
tar -xzvf thingsboard/data.tar -C $(realpath thingsboard)
source ../Server/start.sh
source nginx/start.sh
cd thingsboard/
sudo chown -R 799 .mytb-logs
sudo chown -R 799 .mytb-data
./start.sh
