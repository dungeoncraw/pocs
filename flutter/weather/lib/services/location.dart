import 'package:geolocator/geolocator.dart';

class Location {
  double? latitude;
  double? longitude;
  bool serviceEnabled = false;
  LocationPermission? permission;

  Future<bool> _isReadyToUseLocation() async {
    bool locationEnabled = await _isLocationEnabled();
    if (!locationEnabled) {
      return Future.error('Location is disabled');
    }
    bool permission =  await _isPermissionGranted();
    if (!permission) {
      return Future.error('Permission not granted');
    }
    return true;
  }

  Future<bool> _isLocationEnabled() async {
    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      // mobile location services are disabled
      // TODO request user to enable the location
      return false;
    }
    return true;
  }

  Future<bool> _isPermissionGranted() async {
    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        // Permissions are denied, next time you could try
        // requesting permissions again (this is also where
        // Android's shouldShowRequestPermissionRationale
        // returned true. According to Android guidelines
        // your App should show an explanatory UI now.
        return false;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      // Permissions are denied forever
      // TODO instead of using location can get city by name
      return Future.error(
          'Location permissions are permanently denied, we cannot request permissions.');
    }
    return true;
  }

  Future<void> getCurrentLocation() async {
    try {
      await _isReadyToUseLocation();
      Position position = await Geolocator.getCurrentPosition(
          desiredAccuracy: LocationAccuracy.low);
      latitude = position.latitude;
      longitude = position.longitude;
    } catch (e) {
      // TODO need to test this scenario, include unti test
      print(e);
      return;
    }
  }

}