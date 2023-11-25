FROM ubuntu
RUN apt-get update && apt-get install -y python3 python3-pip dos2unix git
RUN pip install --upgrade pip
WORKDIR /app
RUN git clone --branch test-deploy https://github.com/UITProjects/NT131.O12.MMCL.Nhom14.git
RUN dos2unix NT131.O12.MMCL.Nhom14/Server/src/*
ENV ssl_client_key="/ssl/client-key.pem"
ENV ssl_client_cert="/ssl/client-cert.pem"
COPY ssl /ssl 
ENV password_mysql=$password_mysql
ENV username_mysql=$username_mysql
ENV symmetric_key=none
WORKDIR /app/NT131.O12.MMCL.Nhom14/Server/src
RUN pip install -r requirement.txt
RUN mkdir tmp
EXPOSE 80
EXPOSE 2509
CMD ["python3","-u","main.py"]
