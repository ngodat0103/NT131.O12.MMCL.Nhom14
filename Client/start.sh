docker run -it --rm -p 80:80 -e symmetric_key=$symmetric_key --restart always\
-v /bin/systemctl:/bin/systemctl \
-v /run/systemd/system:/run/systemd/system \
-v /var/run/dbus/system_bus_socket:/var/run/dbus/system_bus_socket \
-v /sys/fs/cgroup:/sys/fs/cgroup \
--priviledge \
ngodat0103/nhungproject:client
