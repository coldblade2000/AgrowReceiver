import json
import random
import hashlib
import socket
import threading
import time

from datetime import timedelta, datetime

from faker import Faker
from faker.providers import date_time

fake = Faker()
fake.add_provider(date_time)

SENSOR_ID = "5"


def generateJSONPayload() -> str:
    baseDate = fake.date_time_between(datetime.fromisoformat("2022-02-02T11:42:52"),
                                      datetime.fromisoformat("2022-04-20T11:42:52"))
    objList = []
    for i in range(0, random.randint(10, 15)):
        mockObj = {
            "timestamp": str(baseDate + timedelta(minutes=30) * i),
            "humidity": random.random(),
            "temperature": random.random() * 50 + 10,
            "sunpower": random.random() * 200 + 900
        }
        objList.append(mockObj)

    return json.JSONEncoder().encode(objList)


def generateJSONDataPackets():
    objList = []
    for j in range(0, 10):
        for i in range(0, random.randint(20, 30)):
            payloadJSON = str(generateJSONPayload())
            payloadHash = str(hashlib.md5(payloadJSON.encode()).hexdigest())
            mockObj = {
                "deviceID": j,
                "dataHash": payloadHash,
                "payloadJSON": payloadJSON,
            }
            objList.append(mockObj)
    print("Acabo de generar paquetes")
    encoded = json.JSONEncoder().encode(objList)
    print("Termino de encodear paquetes, length ", len(encoded))
    return encoded + "\r\n"


class AnnounceThread(threading.Thread):
    def __init__(self, announce_port):
        threading.Thread.__init__(self)
        self.announce_port = announce_port

    def run(self, *args, **kwargs):
        udp_sock_announce_ip = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        udp_sock_announce_ip.bind(("", self.announce_port))
        udp_sock_announce_ip.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        udp_sock_announce_ip.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)

        while True:
            message = f"AGROW_SENSOR_ID={SENSOR_ID}"
            udp_sock_announce_ip.sendall(message)
            time.sleep(4)


# Get local IP
local_ip = socket.gethostbyname(socket.gethostname())

# Open socket that listens to broadcast from the cellphone
udp_sock_recv_ip = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
udp_sock_recv_ip.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
udp_sock_recv_ip.bind(("0.0.0.0", 12341))
print("UDP server bound")

announce_thread = AnnounceThread(12340)
announce_thread.start()

android_address = None
while android_address is None:
    data, addr = udp_sock_recv_ip.recvfrom(1024)
    datastring = str(data.decode("utf-8"))
    print(datastring)
    if datastring.startswith("AGROW_COLLECTOR_IP"):
        android_address = datastring[datastring.index("=") + 1:]
        print("Found android_address: ", android_address)

port = 12345
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = (android_address, port)
print(f'connecting to {android_address} port {port}', )
sock.connect(server_address)

sock.sendall("Python Mock server\r\n".encode())

send_data_msg = sock.recv(1024).decode("utf-8")
print("send_data_msg: ", send_data_msg)
if "SEND_DATA" not in str(send_data_msg):
    print("ERROR, SEND_DATA INVALID")
else:
    sock.sendall(generateJSONDataPackets().encode())
    print("Finished sending")
    receive_data_msg = sock.recv(1024).decode("utf-8")
    print("receive_data_msg: ", receive_data_msg)
    if "DATA_RECEIVED" not in str(receive_data_msg):
        print("Unsuccessful sending")
    else:
        print("Successful sending !!")
    sock.close()
