import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:unittest/login_screen.dart';

void main() {
  group('Login screen', () {
    const String validEmail = 'email@mail.com';
    const String invalidEmail = 'email';
    const String validPass = 'somelongpass';
    const String invalidPass = 'short';
    testWidgets('should have a title', (WidgetTester tester) async {
      // Arrange
      // tester is Future
      await tester.pumpWidget(const MaterialApp(home: LoginScreen()));
      // Act
      Finder title = find.text('Login');
      // Assert
      expect(title, findsOneWidget);
    });
    testWidgets('should have one text field form to collect email',
        (WidgetTester tester) async {
      // Arrange
      await tester.pumpWidget(const MaterialApp(
        home: LoginScreen(),
      ));
      // Act
      Finder usernameTextField = find.byKey(const ValueKey('email'));
      // Assert
      expect(usernameTextField, findsOneWidget);
    });
    testWidgets('should have one text field form to collect password',
        (WidgetTester tester) async {
      // Arrange
      await tester.pumpWidget(const MaterialApp(
        home: LoginScreen(),
      ));
      // Act
      Finder passwordTextField = find.byKey(const ValueKey('password'));
      // Assert
      expect(passwordTextField, findsOneWidget);
    });
    testWidgets('should have one login button', (WidgetTester tester) async {
      // Arrange
      await tester.pumpWidget(const MaterialApp(
        home: LoginScreen(),
      ));
      // Act
      Finder loginButton = find.byType(ElevatedButton);
      // Assert
      expect(loginButton, findsOneWidget);
    });
    testWidgets('should show error message for required fields',
        (WidgetTester tester) async {
      // Arrange
      await tester.pumpWidget(const MaterialApp(
        home: LoginScreen(),
      ));
      // Act
      Finder loginButton = find.byType(ElevatedButton);
      // simulate touch screen
      await tester.tap(loginButton);
      // await animations to finish
      await tester.pumpAndSettle();
      Finder errorTexts = find.text('Required Field');
      // Assert
      expect(errorTexts, findsNWidgets(2));
    });
    testWidgets('should show error message for invalid email',
        (WidgetTester tester) async {
      // Arrange
      await tester.pumpWidget(const MaterialApp(
        home: LoginScreen(),
      ));
      // Act
      Finder emailTextField = find.byKey(const ValueKey('email'));
      Finder passwordTextField = find.byKey(const ValueKey('password'));
      await tester.enterText(emailTextField, invalidEmail);
      await tester.enterText(passwordTextField, validPass);
      
      Finder loginButton = find.byType(ElevatedButton);
      // simulate touch screen
      await tester.tap(loginButton);
      // await animations to finish
      await tester.pumpAndSettle();
      Finder requiredError = find.text('Required Field');
      Finder invalidEmailError = find.text('Please enter a valid email');
      
      // Assert
      expect(requiredError, findsNothing);
      expect(invalidEmailError, findsOneWidget);
    });
    testWidgets('should show error message for invalid password',
        (WidgetTester tester) async {
      // Arrange
      await tester.pumpWidget(const MaterialApp(
        home: LoginScreen(),
      ));
      // Act
      Finder emailTextField = find.byKey(const ValueKey('email'));
      Finder passwordTextField = find.byKey(const ValueKey('password'));
      await tester.enterText(emailTextField, validEmail);
      await tester.enterText(passwordTextField, invalidPass);
      
      Finder loginButton = find.byType(ElevatedButton);
      // simulate touch screen
      await tester.tap(loginButton);
      // await animations to finish
      await tester.pumpAndSettle();
      Finder requiredError = find.text('Required Field');
      Finder invalidEmailError = find.text('Please enter a valid email');
      Finder invalidPassword = find.text('Please enter at least 8 character for password');
      
      // Assert
      expect(requiredError, findsNothing);
      expect(invalidEmailError, findsNothing);
      expect(invalidPassword, findsOneWidget);
    });
    testWidgets('should submit form when email and password is valid',
        (WidgetTester tester) async {
      // Arrange
      await tester.pumpWidget(const MaterialApp(
        home: LoginScreen(),
      ));
      // Act
      Finder emailTextField = find.byKey(const ValueKey('email'));
      Finder passwordTextField = find.byKey(const ValueKey('password'));
      await tester.enterText(emailTextField, validEmail);
      await tester.enterText(passwordTextField, validPass);
      
      Finder loginButton = find.byType(ElevatedButton);
      // simulate touch screen
      await tester.tap(loginButton);
      // await animations to finish
      await tester.pumpAndSettle();
      Finder requiredError = find.text('Required Field');
      Finder invalidEmailError = find.text('Please enter a valid email');
      Finder invalidPassword = find.text('Please enter at least 8 character for password');
      
      // Assert
      expect(requiredError, findsNothing);
      expect(invalidEmailError, findsNothing);
      expect(invalidPassword, findsNothing);
    });
  });
}
