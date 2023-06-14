import 'dart:io';
import 'dart:convert';
import 'dart:async';

int counter = 0;

void main() async{
	// Linux implementation, missing windows version
	if (Platform.isWindows) {
		print('Just works in linux based system, sorry.');
		return;
	}
	// first parameter is the program and the second is arguments
	Process.start('cat', []).then((Process process) {
		// convert output to utf8
		process.stdout.transform(utf8.decoder).listen((data) {
			print(data);
		});	
		process.stdin.writeln('Nice and simple input');
		// now just kill the process as it already printed the value
		Process.killPid(process.pid);
	});

	// async features
	Duration duration = Duration(seconds: 2);
	Timer.periodic(duration, timeout);

	print('##### start reading file to use future ####');
	appendFile();
	print(await readFile());
	print('#### end reading file ####');

	// streaming
	var eventList = List<dynamic>.empty(growable: true);
	
	// need to import dart:async
	var controller = StreamController<dynamic>();
	var stream = controller.stream;

	stream.listen((onData) {
		print('event triggered: $onData');
		eventList.add(onData);
	}, onDone: (){
		print('stream finished');
		print(eventList);
	});

	controller.sink.add('Data 1');
	controller.sink.add('Second message');
	controller.sink.add('This could be a string');
	controller.sink.add('Or could be anything?');
	final person = <String, int>{'age': 10};
	controller.sink.add(person);
	controller.close();

}

void timeout(Timer timer) {
	print('Waiting: ${getTime()}')	;
	
	counter++;
	if (counter >=5)
		timer.cancel();	

}

String getTime() {
	DateTime dt = DateTime.now();
	return dt.toString();
}

void appendFile() {
	File file = File(Directory.current.path+'/test.txt');
	DateTime dt = DateTime.now();
	file.writeAsString(dt.toString()+'\r\n', mode: FileMode.append);
}

Future<String> readFile() {
	File file = File(Directory.current.path+'/test.txt');
	return file.readAsString();
}
