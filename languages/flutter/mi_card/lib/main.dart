import 'package:flutter/material.dart';

void main() {
  runApp(
    const MyApp(),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        backgroundColor: Colors.teal,
        body: SafeArea(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const CircleAvatar(
                radius: 50.0,
                backgroundImage: AssetImage(
                  'images/avatar.png',
                ),
              ),
              const Text('Thiago Nascimento',
                style: TextStyle(
                    fontFamily: 'Pacifico',
                    fontSize: 40.0,
                    color: Colors.white,
                    fontWeight: FontWeight.bold),
              ),
              Text('Entrepreneur'.toUpperCase(),
                style: TextStyle(
                  fontFamily: 'Source Sans',
                  fontSize: 20.0,
                  color: Colors.teal[100],
                  fontWeight: FontWeight.bold,
                  letterSpacing: 2.5,
                ),
              ),
              SizedBox(
                height: 20.0,
                  width: 150.0,
                  child: Divider(
                    thickness: 1,
                    color: Colors.white,
                  ),
              ),
              Card(
                margin: EdgeInsets.symmetric(horizontal: 25.0, vertical: 10.0),
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: ListTile(
                    leading: Icon(
                      Icons.phone,
                      color: Colors.teal,
                    ),
                    title: Text(
                      '+99 99999-9999',
                      style: TextStyle(
                          color: Colors.teal[900],
                          fontFamily: 'Source Sans',
                          fontSize: 20.0
                      ),
                    ),
                  ),
                ),
              ),
              Card(
                margin: EdgeInsets.symmetric(horizontal: 25.0, vertical: 10.0),
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: ListTile(
                    leading: Icon(
                      Icons.email,
                      color: Colors.teal,
                    ),
                    title: Text(
                      'somemail@mail.com',
                      style: TextStyle(
                          color: Colors.teal[900],
                          fontFamily: 'Source Sans',
                          fontSize: 20.0
                      ),
                    ),
                  ),
                ),
              )
            ],
          )
        ),
      ),
    );
  }
}

class AlignmentChallenge extends StatelessWidget {
  const AlignmentChallenge({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        backgroundColor: Colors.teal,
        body: SafeArea(
          child: Row(
            //space between items in the row
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Container(
                color: Colors.red,
                width: 100.0,
              ),
              Container(
                child: Column(
                  //align in center the column values
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Container(
                      width: 100.0,
                      height: 100.0,
                      color: Colors.yellow,
                    ),
                    Container(
                      width: 100.0,
                      height: 100.0,
                      color: Colors.yellowAccent,
                    )
                  ],
                ),
              ),
              Container(
                color: Colors.blue,
                width: 100.0,
              )
            ],
          ),
        ),
      ),
    );
  }
}
