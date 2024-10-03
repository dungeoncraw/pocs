import 'package:apitesting/fetch_books.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:http/http.dart' as http;
import 'package:mockito/mockito.dart';

import 'fetch_book_test.mocks.dart';

@GenerateMocks([http.Client])
void main() {
  // delay initalization
  late MockClient mockClient;

  setUp(() {
    mockClient = MockClient();
  });
  tearDown(() {
    mockClient.close();
  });
  group('Fetch books API call', () {
    test('should return list of books for http success call', () async {
      when(mockClient.get(Uri.parse(fetchBooksURL))).thenAnswer(
          (realInvocation) async => http.Response(
              '[{"name": "Some name", "author": "Some author", "description": "Some description", "amazon": "Some amazon"}]', 200));
      
      expect(await fetchBooks(mockClient), isA<List<BooksListModel>>());
    });
    test('should throw an exception when http call fails', () async {
      when(mockClient.get(Uri.parse(fetchBooksURL))).thenAnswer(
          (realInvocation) async => http.Response(
              'Not Found', 404));
      
      expect(await fetchBooks(mockClient), throwsA(Exception));
    });
  });
}
