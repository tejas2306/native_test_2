import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel('samples.flutter.dev/battery');
  String _batteryLevel = 'Unknown battery level.';

  void _incrementCounter() async {
    String batteryLevel;

    bool status = await askPermissions();
    if (status) {
      try {
        final result = await platform.invokeMethod(
            'getWifiDetails', <String, dynamic>{"Password": 123123});
        debugPrint(json.decode(result)["bssid"]);
        batteryLevel = 'SSID : ${json.decode(result)["bssid"]}';
      } on PlatformException catch (e) {
        batteryLevel = "Failed to get Wifi Details: '${e.message}'.";
      }
      setState(() {
        _batteryLevel = batteryLevel;
      });
    }
  }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              _batteryLevel,
              style: Theme.of(context).textTheme.headline4,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
    );
  }

  Future<bool> askPermissions() async {
    if (await Permission.location.request().isGranted) {
      return true;
    }
    return false;
  }
}
