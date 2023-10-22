FROM ubuntu
RUN apt-get update && apt-get install -y python3 python3-pip dos2unix git
RUN pip install --upgrade pip
WORKDIR /app
RUN git clone https://github.com/UITProjects/NT131.O12.MMCL.Nhom14.git
RUN dos2unix NT131.O12.MMCL.Nhom14/Server/*
RUN dos2unix NT131.O12.MMCL.Nhom14/Client/*
ARG appid
ARG symmetric_key
ARG password_mysql
ARG username_mysql
ENV ssl_client_key="/ssl/client-key.pem"
ENV ssl_client_cert="/ssl/client-cert.pem"
COPY ssl /ssl 
ENV symmetric_key=$symmetric_key
ENV password_mysql=$password_mysql
ENV username_mysql=$username_mysql
ENV appid=$appid
WORKDIR /app/NT131.O12.MMCL.Nhom14/Server
RUN pip install -r requirement.txt
EXPOSE 2509
EXPOSE 2590
CMD ["python3","main.py"]
