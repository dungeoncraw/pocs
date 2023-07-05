import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:unittest/login_screen.dart';

void main() {
  group('Login flow Test', () {
    IntegrationTestWidgetsFlutterBinding.ensureInitialized();
    testWidgets(
        'Should show required fields error message when user taps submit without entering email and password',
        (WidgetTester tester) async {
      // Arrange
      await tester.pumpWidget(const MaterialApp(
        home: LoginScreen(),
      ));
      // Act
      Finder emailTextField = find.byKey(const ValueKey('email'));
      Finder passwordTextField = find.byKey(const ValueKey('password'));
      await tester.enterText(emailTextField, '');
      await tester.enterText(passwordTextField, '');

      Finder loginButton = find.byType(ElevatedButton);
      // simulate touch screen
      await tester.tap(loginButton);
      // await animations to finish
      await tester.pumpAndSettle();

      Finder errorText = find.text('Required Field');

      // Assert
      expect(errorText, findsNWidgets(2));
    });
    testWidgets(
        'Should show home screen if user taps on login button and have valid email and password in login screen ',
        (WidgetTester tester) async {
      // Arrange
      await tester.pumpWidget(const MaterialApp(
        home: LoginScreen(),
      ));
      // Act
      Finder emailTextField = find.byKey(const ValueKey('email'));
      Finder passwordTextField = find.byKey(const ValueKey('password'));
      await tester.enterText(emailTextField, 'email@mail.com');
      await tester.enterText(passwordTextField, 'somelongpass');

      Finder loginButton = find.byType(ElevatedButton);
      // simulate touch screen
      await tester.tap(loginButton);
      // await animations to finish
      await tester.pumpAndSettle();

      Finder welcomeWidget = find.byType(Text);
      Finder welcomeText = find.text('Welcome Home!');

      // Assert
      expect(welcomeWidget, findsOneWidget);
      expect(welcomeText, findsOneWidget);
    });
  });
}
