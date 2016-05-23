#include "HX711.h"

#define calibration_factor -7050.0 //This value is obtained using the SparkFun_HX711_Calibration sketch
#define MAX_WEIGHT_lb 44
#define lb2kg 0.45359237
#define error_rate 0.001

#define DOUT  3
#define CLK  2

HX711 scale(DOUT, CLK);
void setup()
{
  Serial.begin(9600);
  scale.set_scale(calibration_factor); //This value is obtained by using the SparkFun_HX711_Calibration sketch

  scale.tare();  //Assuming there is no weight on the scale at start up, reset the scale to 0
}
void loop()
{
  double weight = 0;
  double W = 0;

  if (scale.get_units() >= MAX_WEIGHT_lb * error_rate){
      weight = scale.get_units();
      while(1){
        W = scale.get_units();
        if (W <= MAX_WEIGHT_lb * error_rate) break;
        weight = weight * 0.80 + W * 0.20;
      }
      Serial.println(weight * lb2kg, 4);
  }
}

