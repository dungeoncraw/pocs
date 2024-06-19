import 'package:flutter_test/flutter_test.dart';
import 'package:unittest/validator.dart';

void main() {
  group('Email validator', () { 
    test('check for empyt email', () {
      // arrange
      String email = '';
      // act
      String? result = Validator.validateEmail(email);
      // assert
      expect(result, 'Required Field');
    });

    test('check invalid email', () {
      // arrange
      String email = 'somerandonstring';
      // act
      String? result = Validator.validateEmail(email);
      // assert
      expect(result, 'Please enter a valid email');
    });
    test('check valid email', () {
      // arrange
      String email = 'someemail@mail.com';
      // act
      String? result = Validator.validateEmail(email);
      // assert
      expect(result, null);
    });
    group('Password validator', () { 
      test('check empty password', () {
        // arrange
        String password = '';
        // act
        String? result = Validator.validatePassword(password);
        // assert
        expect(result, 'Required Field');
      });
      test('check empty password', () {
        // arrange
        String password = '';
        // act
        String? result = Validator.validatePassword(password);
        // assert
        expect(result, 'Required Field');
      });
      test('check invalid password', () {
        // arrange
        String password = 'pass';
        // act
        String? result = Validator.validatePassword(password);
        // assert
        expect(result, 'Please enter at least 8 character for password');
      });
      test('check valid password', () {
        // arrange
        String password = 'somegoodpasswordlength';
        // act
        String? result = Validator.validatePassword(password);
        // assert
        expect(result, null);
      });
    });
  });
} 