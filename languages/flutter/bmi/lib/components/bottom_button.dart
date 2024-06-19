import 'package:flutter/material.dart';
import '../constants.dart';

class BottomButton extends StatelessWidget {

  final VoidCallback onTap;
  final String buttonTitle;

  const BottomButton({super.key, required this.onTap, required this.buttonTitle});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        color: kBottomContainerColor,
        margin: const EdgeInsets.only(top: 10.0),
        // this makes the container always get the full size of screen
        width: double.infinity,
        height: kBottomContainerHeight,
        padding: EdgeInsets.only(
          bottom: 20.0,
        ),
        child: Center(
            child: Text(
              buttonTitle,
              style: klargeButtonTextStyle,
            )),
      ),
    );
  }
}
