void setup()
{
  Serial.begin(9600);
  Serial.println("Arduino Start");
}
 
void loop() 
{
  if (Serial.available()) {
    byte data = Serial.read();
    Serial.write(data);
  }
}
