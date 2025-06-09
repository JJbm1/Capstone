#include <ControlMotor.h> // 라이브러리를 사용합니다.

// 아두이노 보드에서 사용할 핀을 설정합니다.
ControlMotor control(2, 3, 7, 4, 5, 6); // 우모1, 우모2, 좌모1, 좌모2, 우PWM, 좌PWM

String command = "";
int speed = 150;

void setup() {
  Serial.begin(9600);
}

void loop() {
  if (Serial.available()) {
    command = Serial.readStringUntil('\n');
    command.trim(); // 앞뒤 공백 제거

    if (command == "forward") {
      control.Motor(speed, 1); // 전진
    } 
    else if (command == "backward") {
      control.Motor(-speed, 1); // 후진
    } 
    else if (command == "left") {
      control.Motor(speed, 100); // 왼쪽 회전
      delay(400);
      control.Motor(0, 1);       // 정지
    } 
    else if (command == "right") {
      control.Motor(speed, -100); // 오른쪽 회전
      delay(400);
      control.Motor(0, 1);        // 정지
    } 
    else if (command == "stop") {
      control.Motor(0, 1); // 정지
    }

    Serial.print("✅ 받은 명령: ");
    Serial.println(command);
  }
}
