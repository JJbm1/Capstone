import 'package:firebase_messaging/firebase_messaging.dart';

class PushService {
  static Future<void> initFCM() async {
    FirebaseMessaging messaging = FirebaseMessaging.instance;

    // 디바이스 토큰 발급
    String? token = await messaging.getToken();
    print('디바이스 토큰: $token');

    // TODO: 이 토큰을 백엔드에 전송 (예: HTTP POST 등)
  }
}