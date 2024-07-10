import 'package:flutter/material.dart';

class LoaderScreen extends StatelessWidget {
  const LoaderScreen({super.key});

  @override
  Widget build(BuildContext context) => const Scaffold(
      body: Padding(
          padding: EdgeInsets.symmetric(horizontal: 8, vertical: 16),
          child: Column(
              crossAxisAlignment: CrossAxisAlignment.center, children: [])));
}
