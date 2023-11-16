docker run -d --name mariadb --network=mynet2 -e MARIADB_ROOT_PASSWORD=unsbiz8ppr3BG1XSYV2zNkvB8FmNXMYogWy2Q1F3ptc4ytT9i \
-v maria_db_data2:/var/lib/mysql \
-v maria_db_config2:/etc/mysql \
-v self_ssl:/self_ssl \
mariadb
