import time  
import RPi.GPIO as GPIO
from pymodbus3.client.sync import ModbusTcpClient

#set up GPIO using BCM numbering
GPIO.setmode(GPIO.BCM)

#setup GPIO using Board numbering
#GPIO.setmode(GPIO.BOARD)
GPIO.cleanup()  
GPIO.setup(22, GPIO.OUT)
GPIO.setup(5, GPIO.IN)
  
client = ModbusTcpClient('127.0.0.1')

while True:

    uzaklik=GPIO.input(5)
    print(uzaklik)

    if(uzaklik == 1.0):
	# Uncomment if connected to led
        # GPIO.output(22, True)
        client.write_register(1, 1)
    if(uzaklik == 0.0):
	# Uncomment if connected to led
        # GPIO.output(22, False)
        client.write_register(1, 1)

   time.sleep(1.0)


GPIO.cleanup()
