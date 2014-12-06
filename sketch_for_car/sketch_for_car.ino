char msgs[5][15] = {
	"Up Key OK    ",
	"Left Key OK  ",
	"Down Key OK  ",
	"Right Key OK ",
	"Select Key OK" };
char start_msg[15] = { "Start loop "};
int  adc_key_val[5] ={ 30, 150, 360, 535, 760 };
int NUM_KEYS = 5;
int adc_key_in;
int key = -1;
int oldkey = -1;

//Standard PWM DC control
int E1 = 5;     //M1 Speed Control
int E2 = 6;     //M2 Speed Control
int M1 = 4;    //M1 Direction Control
int M2 = 7;    //M1 Direction Control

void stop(void)                    //Stop
{
  digitalWrite(E1,LOW);
  digitalWrite(E2,LOW);
}
void advance(char a,char b)          //Move forward
{
  
  analogWrite (E1,a);
  digitalWrite(M1,LOW);   
  analogWrite (E2,b);    
  digitalWrite(M2,LOW);
}  
void back_off (char a,char b)          //Move backward
{
  analogWrite (E1,a);      //PWM Speed Control
  digitalWrite(M1,HIGH);    
  analogWrite (E2,b);    
  digitalWrite(M2,HIGH);
}
void turn_L (char a,char b)             //Turn Left
{
  analogWrite (E1,a);
  digitalWrite(M1,LOW);    
  analogWrite (E2,b);    
  digitalWrite(M2,HIGH);
}
void turn_R (char a,char b)             //Turn Right
{
  analogWrite (E1,a);
  digitalWrite(M1,HIGH);    
  analogWrite (E2,b);    
  digitalWrite(M2,LOW);
}

void setup(void)
{
	int i;
	pinMode(13, OUTPUT);  //we’ll use the debug LED to output a heartbeat
	for(i=4;i<=7;i++)
	pinMode(i, OUTPUT);
	Serial1.begin(115200);
	Serial.println("Run keyboard control");
}

void loop() {
  
  if (Serial1.available() )
  {
    char incomingByte = Serial1.read();
    Serial.write(incomingByte);
    switch(incomingByte) {
      case 'f':
        advance (255,255);
        break;
      case 'b':
        back_off (255,255);
        break;
      case 'l':
        turn_L (200,200);
        break;
      case 'r':
        turn_R (200,200);
        break;
      case 's':
        stop();
        break;
    }
  }
}
