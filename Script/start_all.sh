docker network create -d bridge mynet2
source mariadb/start.sh
tar -xzvf thingsboard/data.tar -C $(realpath thingsboard)
source ../Server/start.sh
source nginx/start.sh
cd thingsboard/
./start.sh
