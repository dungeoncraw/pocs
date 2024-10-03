import 'package:flutter/material.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';

import 'package:weather/screens/location_screen.dart';
import 'package:weather/services/weather.dart';

class LoadingScreen extends StatefulWidget {
  @override
  _LoadingScreenState createState() => _LoadingScreenState();
}

class _LoadingScreenState extends State<LoadingScreen> {
  @override
  void initState() {
    super.initState();
    // get location when add the widget into tree, so once completes navigate to new screen
    getLocation();
  }

  void getLocation() async {
    WeatherModel weatherModel = WeatherModel();
    dynamic weather = await weatherModel.getLocationWeather();
    Navigator.push(
      context,
      MaterialPageRoute(
          builder: (context) => LocationScreen(
                locationWeather: weather,
              )),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: SpinKitPulsingGrid(
          color: Colors.white,
          size: 100.0,
        ),
      ),
    );
  }
}
