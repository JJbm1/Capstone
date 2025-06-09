import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'dashboard_screen.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final emailController = TextEditingController();
  final passwordController = TextEditingController();
  final nameController = TextEditingController();

  String? selectedGender; // '남' 또는 '여'
  DateTime? birthDate;
  bool isLoading = false;

  Future<void> signUp() async {
    setState(() => isLoading = true);

    try {
      final credential = await FirebaseAuth.instance.createUserWithEmailAndPassword(
        email: emailController.text.trim(),
        password: passwordController.text.trim(),
      );

      final uid = credential.user?.uid;
      if (uid == null) throw Exception("Firebase UID 없음");

      final userName = nameController.text.trim();

      // ✅ 회원가입 성공 시 이름 전달하며 이동
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (context) => DashboardScreen(userName: userName),
        ),
      );
    } on FirebaseAuthException catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Firebase 오류: ${e.message ?? '회원가입 실패'}")),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("오류 발생: $e")),
      );
    } finally {
      setState(() => isLoading = false);
    }
  }

  Future<void> pickBirthDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: DateTime(now.year - 20),
      firstDate: DateTime(1900),
      lastDate: now,
    );
    if (picked != null) {
      setState(() => birthDate = picked);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("회원가입")),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            TextField(
              controller: nameController,
              decoration: const InputDecoration(labelText: "이름", border: OutlineInputBorder()),
            ),
            const SizedBox(height: 15),
            Row(
              children: [
                const Text("성별:"),
                const SizedBox(width: 20),
                Expanded(
                  child: RadioListTile<String>(
                    title: const Text("남"),
                    value: "남",
                    groupValue: selectedGender,
                    onChanged: (value) => setState(() => selectedGender = value),
                  ),
                ),
                Expanded(
                  child: RadioListTile<String>(
                    title: const Text("여"),
                    value: "여",
                    groupValue: selectedGender,
                    onChanged: (value) => setState(() => selectedGender = value),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 15),
            Row(
              children: [
                const Text("생년월일:"),
                const SizedBox(width: 20),
                Expanded(
                  child: InkWell(
                    onTap: pickBirthDate,
                    child: Container(
                      padding: const EdgeInsets.symmetric(vertical: 15, horizontal: 10),
                      decoration: BoxDecoration(
                        border: Border.all(color: Colors.grey),
                        borderRadius: BorderRadius.circular(5),
                      ),
                      child: Text(
                        birthDate == null
                            ? "날짜 선택"
                            : "${birthDate!.year}-${birthDate!.month.toString().padLeft(2, '0')}-${birthDate!.day.toString().padLeft(2, '0')}",
                        style: const TextStyle(fontSize: 16),
                      ),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 15),
            TextField(
              controller: emailController,
              decoration: const InputDecoration(labelText: "이메일", border: OutlineInputBorder()),
            ),
            const SizedBox(height: 15),
            TextField(
              controller: passwordController,
              obscureText: true,
              decoration: const InputDecoration(labelText: "비밀번호", border: OutlineInputBorder()),
            ),
            const SizedBox(height: 30),
            SizedBox(
              width: double.infinity,
              height: 50,
              child: ElevatedButton(
                onPressed: isLoading ? null : signUp,
                style: ElevatedButton.styleFrom(backgroundColor: Colors.deepPurple),
                child: isLoading
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text("회원가입", style: TextStyle(color: Colors.white)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
