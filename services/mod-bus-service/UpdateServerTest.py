import time  
from pymodbus3.client.sync import ModbusTcpClient



client = ModbusTcpClient('127.0.0.1')


while True:
	client.write_coil(0, True)
	time.sleep(1.0)
	client.write_coil(0, False)
	time.sleep(1.0)
