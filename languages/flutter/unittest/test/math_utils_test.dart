import 'package:flutter_test/flutter_test.dart';
import 'package:unittest/math_utils.dart';

void main() {
  group('Math utils test', () { 
    test('check sum of two int values', (){
      // arrange
      var a = 10;
      var b = 10;

      // act
      var sum = add(a, b);

      // assert
      expect(sum, 20);		
    });
    test('check subtract of two int values', () {
      // arrange
      int a = 10;
      int b = 15; 
      // act
      int sub = subtract(a, b);
      // assert
      expect(sub, -5);
    });
  });
}
