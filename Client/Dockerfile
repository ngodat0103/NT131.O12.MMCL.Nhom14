FROM ubuntu
RUN apt-get update && apt-get install -y python3 python3-pip dos2unix git
RUN pip install --upgrade pip
WORKDIR /app
RUN git clone --branch test-deploy https://github.com/UITProjects/NT131.O12.MMCL.Nhom14.git
RUN dos2unix NT131.O12.MMCL.Nhom14/Client/src/*
ENV symmetric_key=symmetric_key
WORKDIR /app/NT131.O12.MMCL.Nhom14/Client/src
RUN pip install -r requirement.txt
EXPOSE 80
CMD ["python3","-u","main.py"]

