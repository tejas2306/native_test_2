import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

import 'Models/result.dart';

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
      home: const MyHomePage(title: 'Flutter EspTouch Test App'),
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
  Result wifiResult = Result();
  final TextEditingController _passwordController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _getWifiDetails();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 15.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            const SizedBox(height: 20),
            Text(
              "SSID    : ${wifiResult.ssid}",
              style: Theme.of(context)
                  .textTheme
                  .headline6!
                  .copyWith(fontWeight: FontWeight.normal),
            ),
            const SizedBox(height: 10),
            Text(
              "BSSID  : ${wifiResult.bssid}",
              style: Theme.of(context)
                  .textTheme
                  .headline6!
                  .copyWith(fontWeight: FontWeight.normal),
            ),
            const SizedBox(height: 20),
            TextField(
              controller: _passwordController,
              decoration: const InputDecoration(
                label: Text("Password"),
                border: OutlineInputBorder(
                    borderRadius: BorderRadius.all(Radius.circular(10))),
              ),
            )
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _transmit,
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

  void _getWifiDetails() async {
    bool status = await askPermissions();
    if (status) {
      try {
        final result = await platform.invokeMethod(
            'getWifiDetails', <String, dynamic>{"Password": 123123});

        if (result != null) {
          wifiResult = Result.fromJson(json.decode(result));
        }
      } on PlatformException catch (e) {
        debugPrint(e.message);
      }
      setState(() {});
    }
  }

  _transmit()async {
      try {
        final result = await platform.invokeMethod(
            'transmit', <String, dynamic>{
            "Password": "123123",
            "ssid":wifiResult.ssid,
            "bssid":wifiResult.bssid,
            "isBroadcast": true,
            });

        if (result != null) {
          wifiResult = Result.fromJson(json.decode(result));
        }
      } on PlatformException catch (e) {
        debugPrint(e.message);
      }
      setState(() {});
  }
}
