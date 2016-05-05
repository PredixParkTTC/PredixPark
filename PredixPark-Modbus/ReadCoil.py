from pymodbus.client.sync import ModbusTcpClient


client = ModbusTcpClient('127.0.0.1')
result = client.read_coils(0,1)
print result.bits[0]
client.close()