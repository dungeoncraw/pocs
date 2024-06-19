import 'package:flutter/material.dart';

// main is the entrypoint for flutter apps
// try to avoid using const MaterialApp as it can cause lint issues
void main() {
  runApp(MaterialApp(
    home: Scaffold(
      backgroundColor: Colors.blueGrey,
      appBar: AppBar(
        title: const Text(
          'I Am Rich!',
        ),
        backgroundColor: Colors.blueGrey[900],
      ),
      body: const Center(
          child: Image(
        image: AssetImage(
          'images/diamond.png',
        ),
      )),
    ),
  ));
}
