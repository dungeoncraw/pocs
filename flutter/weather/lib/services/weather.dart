import 'package:weather/services/location.dart';
import 'package:weather/services/networking.dart';

import '../utilities/constants.dart';

class WeatherModel {
  // this is to match different api
  // String getWeatherIcon(int condition) {
  //   if (condition < 300) {
  //     return '🌩';
  //   } else if (condition < 400) {
  //     return '🌧';
  //   } else if (condition < 600) {
  //     return '☔️';
  //   } else if (condition < 700) {
  //     return '☃️';
  //   } else if (condition < 800) {
  //     return '🌫';
  //   } else if (condition == 800) {
  //     return '☀️';
  //   } else if (condition <= 804) {
  //     return '☁️';
  //   } else {
  //     return '🤷‍';
  //   }
  // }
  String getWeatherIcon(int condition) {
    if (condition == 40) {
      return '🌩';
    } else if (condition == 45) {
      return '🌧';
    } else if (condition == 35) {
      return '☔️';
    } else if (condition == 16) {
      return '☃️';
    } else if (condition == 14) {
      return '🌫';
    } else if (condition == 32) {
      return '☀️';
    } else if (condition == 44) {
      return '☁️';
    } else {
      return '🤷‍';
    }
  }

  String getMessage(int temp) {
    if (temp > 25) {
      return 'It\'s 🍦 time';
    } else if (temp > 20) {
      return 'Time for shorts and 👕';
    } else if (temp < 10) {
      return 'You\'ll need 🧣 and 🧤';
    } else {
      return 'Bring a 🧥 just in case';
    }
  }

  Future<dynamic> getLocationWeatherByCityName(String cityName) async {
    Location location = Location();
    await location.getCurrentLocation();
    if (location.latitude != null && location.longitude != null) {
      dynamic params = {
        'format': 'json-cors',
        'locale': 'en',
        'city_name': cityName,
        'key': weatherKey
      };
      Uri uri = Uri.https('api.hgbrasil.com', 'weather', params);
      NetworkHelper networkHelper = NetworkHelper(uri: uri);

      dynamic weather = await networkHelper.getData();
      return weather;
    }
  }

  Future<dynamic> getLocationWeather() async {
    Location location = Location();
    await location.getCurrentLocation();
    if (location.latitude != null && location.longitude != null) {
      dynamic params = {
        'format': 'json-cors',
        'locale': 'en',
        'latitude': location.latitude.toString(),
        'longitude': location.longitude.toString(),
        'key': weatherKey
      };
      Uri uri = Uri.https('api.hgbrasil.com', 'weather', params);
      NetworkHelper networkHelper = NetworkHelper(uri: uri);

      dynamic weather = await networkHelper.getData();
      return weather;
    }
  }
}
