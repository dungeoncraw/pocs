import 'dart:async';
import 'package:mysql_client/mysql_client.dart';

class MySqlClient {
	// creates singleton for the DB
	factory MySqlClient() {
		return _inst;
	}
	MySqlClient._internal() {
		_connect();
	}
	
	static final MySqlClient _inst = MySqlClient._internal();

	MySQLConnection? _connection;

	// init the connection for database
	Future<void> _connect() async {
		_connection = await MySQLConnection.createConnection(
			host: '127.0.0.1',
			port: 3306,
			userName: 'nonroot',
			password: 'passworD_01',
			databaseName: 'dart'
		);
		try {
			await _connection?.connect();
		} catch (e) {
			print('Connection error:\n $e');
		}
	}

	Future<IResultSet> execute(
		String query,
		[Map<String, dynamic>? params]
	) async {
		print('check for conection');
		// check for connection before execute query
		if (_connection == null || _connection?.connected == false) {
			await _connect();
		}
		
		if (_connection?.connected == false) {
			throw Exception('Could not connec to DB');
		}
		
		print('Connection stablished');
		return   _connection!.execute(query, params, false);
	}
}
