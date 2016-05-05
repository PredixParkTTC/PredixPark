from pymodbus.server.async import StartTcpServer
from pymodbus.server.async import StartUdpServer
from pymodbus.server.async import StartSerialServer

from pymodbus.device import ModbusDeviceIdentification
from pymodbus.datastore import ModbusSequentialDataBlock
from pymodbus.datastore import ModbusSlaveContext, ModbusServerContext
from pymodbus.transaction import ModbusRtuFramer, ModbusAsciiFramer

#Logging
import logging
logging.basicConfig()
log = logging.getLogger()
log.setLevel(logging.DEBUG)

#Slave addresses to store data.
store = ModbusSlaveContext(
    di = ModbusSequentialDataBlock(0, [17]*100))
context = ModbusServerContext(slaves=store, single=True)

identity = ModbusDeviceIdentification()
identity.VendorName  = 'TTC'
identity.ProductCode = 'PredixPark'
identity.ProductName = 'PredixPark'
identity.ModelName   = 'PredixPark'
identity.MajorMinorRevision = '1.0'

#Might require sudo.
StartTcpServer(context, identity=identity, address=("localhost", 502))
