import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:io';

void main() {
	menu();
}

void menu() {
	print('########### Init ########');
	print('\nSelect one option:');
	print('1 - Today exchange rate');
	print('2 - Save exchange rate');
	print('3 - Read exchange reate file');

	String? option = stdin.readLineSync();
	
	if (option == null) {
		print('Invalid option');
		return;
	}	

	switch(int.parse(option)){
		case 1: todayExchange(); break;
		case 2: registerData(); break;
		case 3: listData(); break;
		default: print('\n\nInvalid option, select a valid option\n\n'); menu(); break;
	}
}

todayExchange() async {
	var data = await getData();
	print('\\###### HG BRasil ######');
	print('${data['date']} -> ${data['data']}');
}

Future getData() async {
	String url = 'https://api.hgbrasil.com/finance?key=2aa6d902';
	http.Response response = await http.get(Uri.parse(url));
	
	if(response.statusCode == 200) {
		var data = json.decode(response.body)['results']['currencies'];
		var usd = data['USD'];
		var eur = data['EUR'];

		Map formatedMap = Map();
		formatedMap['date'] = now();
		formatedMap['data'] = '${usd['name']}: ${usd['buy']} | ${eur['name']}: ${eur['buy']}';
		return formatedMap;
	}
	else
	{
		throw('Something wrong happened');
	}	
}

String now() {
	var now = DateTime.now();
	return '${now.day.toString().padLeft(2,'0')}-${now.month.toString().padLeft(2, '0')}-${now.year.toString()}';
}

Future readFile() async {
	
	Directory dir = Directory.current;
	String filename = 'exchange.txt';
	String path = dir.path + '/' + filename;

	dynamic fileData = List<Map>.empty(growable: true);

	if (await File(path).existsSync()) {
		File file =  new File(path);
		fileData = json.decode(file.readAsStringSync());	
	}
	return fileData;

}

void registerData() async {
	
	var data = await getData();

	dynamic fileData = await readFile();

	
	bool existDate = false;
	fileData.forEach((data) {
		if(data['date'] == now()) {
			existDate = true;
		}
	});
	if (!existDate) {
		fileData.add({"date": now(), "data": '${data['data']}'});
	
		Directory dir = Directory.current;
		File file = new File(dir.path + '/exchange.txt');
		RandomAccessFile raf = file.openSync(mode: FileMode.write);
		raf.writeStringSync(json.encode(fileData).toString());
		raf.flushSync();
		raf.closeSync();

		print('###### data saved ######');
	}
	else
		print('###### record not saved, already have data for today');

}

void listData () async {
	dynamic fileData = await readFile();
	
	print('\n\n################### Saved exchange info ###################');
	
	fileData.forEach((line) {
		print('${line['date']} --> ${line['data']}');
	});
}
