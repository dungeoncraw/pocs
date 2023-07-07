import 'dart:math';
import 'package:flutter/material.dart';

void main() {
  runApp(MaterialApp(
    home: BallPage(),
  ));
}

class BallPage extends StatelessWidget {
  const BallPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.blue,
      appBar: AppBar(
        title: const Text('Ask me anything'),
        centerTitle: true,
        backgroundColor: Colors.blue[700],
      ),
      body: const Ball(),
    );
  }
}

class Ball extends StatefulWidget {
  const Ball({super.key});

  @override
  State<Ball> createState() => _BallState();
}

class _BallState extends State<Ball> {
  int ballAnswer = 1;

  void randomAnswer() {
    ballAnswer = Random().nextInt(4) + 1;
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: MaterialButton(
        child: Image.asset('images/ball$ballAnswer.png'),
        onPressed: () {
          setState(() {
            randomAnswer();
          });
        },
      ),
    );
  }
}
