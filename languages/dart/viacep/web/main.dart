import 'dart:html';
import 'dart:convert';
import 'package:http/http.dart' as http;


void main() {
	querySelector('#search')?.onClick.listen((a) async {
		String? cep = (querySelector('#cep') as InputElement).value;	
		Uri url = Uri.https('viacep.com.br', 'ws/${cep}/json');
		dynamic response = await http.get(url);
		var body = json.decode(response.body);
		(querySelector('#state') as InputElement).value = body['uf'];	
		(querySelector('#city') as InputElement).value = body['localidade'];
		(querySelector('#neighborhood') as InputElement).value = body['bairro'];
		(querySelector('#street') as InputElement).value = body['logradouro'];
	});
}
