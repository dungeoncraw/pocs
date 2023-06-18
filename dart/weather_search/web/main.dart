import 'dart:html';
import 'dart:convert';
import 'package:dialog/dialog.dart';
import 'package:http/http.dart' as http;


String weather_key = '64cc8231';
void main() {
	List cities = List.empty(growable:true);
	cities.add('Florianópolis');
	cities.add('São Paulo');
	cities.add('London');

	loadData(cities);

	querySelector('#search-city')?.onClick.listen((a) async {
		var cityPrompt = await prompt('Which city do you want to search?');
			if ((cityPrompt?.toString()?.length ?? 0) > 0) {
			loadData([cityPrompt.toString()]);
		} else {
			alert('Please type one city');
		}
	});
}

Future getWeather(String city) {
	dynamic params = {
		'format': 'json-cors',
		'locale': 'en',
		'city_name': city,
		'key': weather_key
	};
	Uri url = Uri.https('api.hgbrasil.com', 'weather', params);
	return http.get(url);
}

void loadData(List cities) {
	var empty = querySelector('#empty');
	
	if (empty != null) {
		empty.remove();
	}
	cities.forEach((city) {
		insertData(getWeather(city));
	});
}

void insertData(Future data) async{
	dynamic insertData = await data;
	dynamic body = json.decode(insertData.body);	
	
	if(body['results']['forecast'].length > 0) {
		String html = '<div class="row">';
		html +=  createDivCell(body['results']['city_name']);
		html +=  createDivCell(body['results']['temp']);
		html +=  createDivCell(body['results']['description']);
		html +=  createDivCell(body['results']['wind_speedy']);
		html += '</div>';
		Element? table = querySelector('.table');
		if(table?.innerHtml != null) {
			table?.innerHtml = (table.innerHtml ?? '') + html;
		}
	}
}

String createDivCell(dynamic data) {
	return '<div class="cell">$data</div>';
}
