import 'package:http/http.dart' as http;
import 'dart:convert';

class NetworkHelper {

  NetworkHelper({required this.uri});

  final Uri uri;

  Future getData() async {
    http.Response response = await http.get(uri);
    if (response.statusCode == 200) {
      dynamic data = jsonDecode(response.body);
      return data;
    } else {
      //TODO return something to better to user
      throw Exception('Something went wrong getting data.');
    }
  }
}