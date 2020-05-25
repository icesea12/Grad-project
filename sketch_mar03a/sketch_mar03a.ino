/*
  Example Bluetooth Serial Passthrough Sketch
 by: Jim Lindblom
 SparkFun Electronics
 date: February 26, 2013
 license: Public domain
 This example sketch converts an RN-42 bluetooth module to
 communicate at 9600 bps (from 115200), and passes any serial
 data between Serial Monitor and bluetooth module.
 */
#include <SoftwareSerial.h>  

int SIMPOWER = 5;
int bluetoothTx = 2;  // TX-O pin of bluetooth mate, Arduino D2
int bluetoothRx = 3;  // RX-I pin of bluetooth mate, Arduino D3
int simTx = 6;
int simRx = 7;

SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);
SoftwareSerial simolonibaloni(simTx, simRx);

void setup()
{
  Serial.begin(9600);  // Begin the serial monitor at 9600bps
  simolonibaloni.begin(9600);
  bluetooth.begin(9600);
  pinMode(SIMPOWER, OUTPUT);
  delay(1000);
  pinMode(SIMPOWER, INPUT_PULLUP);

 /* 
  bluetooth.begin(115200);  // The Bluetooth Mate defaults to 115200bps
  bluetooth.print("$");  // Print three times individually
  bluetooth.print("$");
  bluetooth.print("$");  // Enter command mode
  delay(100);  // Short delay, wait for the Mate to send back CMD
  bluetooth.println("U,9600,N");  // Temporarily Change the baudrate to 9600, no parity
  // 115200 can be too fast at times for NewSoftSerial to relay the data reliably
  bluetooth.begin(9600);  // Start bluetooth serial at 9600
  Serial.println("Sending AT commands....");
  
 */
  //Debug message
  Serial.println("Module turning on... ");

  //Loop that ensures the module is enabled so we can send further AT commands

   String resp;

  simolonibaloni.listen();
  do
  {
    //Send the command AT
    simolonibaloni.println("AT");
    //Wait 3 seconds to see if the module has turned on if not then repeat this step
    delay(3000);
  
    resp = simolonibaloni.readString();
    Serial.println("Response: ");
    Serial.println(resp);
  }while(!(resp.indexOf("OK") > 0));

   //Debug message
   Serial.println("Module Ready!");

   //Setup the auto cqs
   simolonibaloni.println("AT+AUTOCSQ=1,1");

   delay(200);

   simolonibaloni.println("AT+CSQDELTA=1");

}

void loop()
{
  //Send Debug status

  if(simolonibaloni.available())
  {
    String data = simolonibaloni.readString();
    bluetooth.print(data);
  }
  
  ///---------------ENABLE FOR DEBUGGING PURPOSES ---------------///
/*if(simolonibaloni.available())  // If the bluetooth sent any characters
  {
    // Send any characters the bluetooth prints to the serial monitor
    Serial.print((char)simolonibaloni.read());  
  }
  if(Serial.available())  // If stuff was typed in the serial monitor
  {
    // Send any characters the Serial monitor prints to the bluetooth
    simolonibaloni.print((char)Serial.read());
  }*/
  ///---------------ENABLE FOR DEBUGGING PURPOSES ---------------///
 /*
  if(bluetooth.available())  // If the bluetooth sent any characters
  {
    // Send any characters the bluetooth prints to the serial monitor
    Serial.print((char)bluetooth.read());  
  }
  if(Serial.available())  // If stuff was typed in the serial monitor
  {
    // Send any characters the Serial monitor prints to the bluetooth
    bluetooth.print((char)Serial.read());
  }
  // and loop forever and ever!
  */
}
