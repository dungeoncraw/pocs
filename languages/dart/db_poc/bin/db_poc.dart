import 'package:db_poc/db.dart';

void main() async {
	
	// first query for our user, if not found then insert the record
	// also generate some random numbers for the age to test the update
	
	final mySqlClient = MySqlClient();
	
	
	print('Inserting info into people table');
	var res = await mySqlClient.execute('insert into people(name, age, email) values (:name, :age, :email)', {
		"name" : "Antonio",
		"age": 55,
		"email": "antonio@mail.com"
	});
	print('Inserted ${res.affectedRows}');
	
	var result = await mySqlClient.execute('select * from people');
	for (final row in result.rows) {
		print('Record: ${row.assoc()}');
	}
}


