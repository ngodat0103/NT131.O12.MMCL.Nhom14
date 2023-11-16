docker network create -d bridge mynet2
source mariadb/start.sh
source thingsboard/start.sh
source ../Server/start.sh
source nginx/start.sh
