import 'package:firebase_core/firebase_core.dart' show FirebaseOptions;
import 'package:flutter/foundation.dart'
    show defaultTargetPlatform, kIsWeb, TargetPlatform;

class DefaultFirebaseOptions {
  static FirebaseOptions get currentPlatform {
    if (kIsWeb) {
      return const FirebaseOptions(
        apiKey: 'AlzaSyDxMZWzb_09QvAiveD_rFrbBxywEMOsiW0',
        appId: '1:713899451934:web:YOUR_WEB_APP_ID', // 웹 앱 ID 필요시 수정
        messagingSenderId: '713899451934',
        projectId: 'diku-7a467',
      );
    }

    switch (defaultTargetPlatform) {
      case TargetPlatform.android:
        return const FirebaseOptions(
          apiKey: 'AlzaSyDxMZWzb_09QvAiveD_rFrbBxywEMOsiW0',
          appId: '1:713899451934:android:3bdaf5fd989aaf1e4ff00f', // ✅ Android 앱 ID
          messagingSenderId: '713899451934',
          projectId: 'diku-7a467',
        );
      default:
        throw UnsupportedError(
          'DefaultFirebaseOptions are not supported for this platform.',
        );
    }
  }
}
