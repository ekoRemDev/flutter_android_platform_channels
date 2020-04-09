import 'package:flutter/material.dart';

///
/// todo 01 import packages
///
import 'package:flutter/services.dart';
import 'dart:async';

class MainApplication extends StatefulWidget {
  @override
  _MainApplicationState createState() => _MainApplicationState();
}

class _MainApplicationState extends State<MainApplication> {

  ///
  /// EventChannel to listen native side
  ///
  static const EventChannel _channel = const EventChannel('eventChannel');

  StreamSubscription _streamSubscription;
  String _platformMessage ="";

  Stream<void> _enableEventReceiver() {
    _streamSubscription = _channel.receiveBroadcastStream().listen(
            (dynamic event) {
          print('Received event: $event');
          setState(() {
            _platformMessage = event;
          });
        },
        onError: (dynamic error) {
          print('Received error: ${error.message}');
        },
        cancelOnError: true);
  }

  void _disableEventReceiver() {
    if (_streamSubscription != null) {
      _streamSubscription.cancel();
      _streamSubscription = null;
    }
  }

  @override
  initState() {
    super.initState();
    _enableEventReceiver();
  }

  @override
  void dispose() {
    super.dispose();
    _disableEventReceiver();
  }








  ///
  /// todo 02 define the MethodChannel
  static const platformMethodChannel =const MethodChannel('flyingpigstudio.co.uk/native');
  String _chargingStatus = 'Charging status: unknown.';

  ///
  /// todo 03 define the function that will handle communication
  String nativeMessage = '';

  Future<Null> _managePower() async
  {
    String _message;
    try {
      final String result = await platformMethodChannel.invokeMethod('powerManage');
      _message = result;
    } on PlatformException catch (e) {
      _message = "Can’t do native stuff ${e.message}.";
    }
    setState(() {
      nativeMessage = _message;
    });
    print("Pressed");
  }


  Future<Null> _launchCamera() async {
    String _message;
    try {
      final String result = await platformMethodChannel.invokeMethod('takePhoto');
      _message = result;
    } on PlatformException catch (e) {
      _message = "Can't do native stuff ${e.message}.";
    }
    setState(() {
      nativeMessage = _message;
    });
  }


  Future<Null> _getBatteryLevel() async {
    String batteryLevel;
    try {
      final int result = int.parse(await platformMethodChannel.invokeMethod('getBatteryLevel'));
      batteryLevel = 'Battery level: $result%.';
    } on PlatformException {
      batteryLevel = 'Failed to get battery level.';
    }
    setState(() {
      nativeMessage = batteryLevel;
    });
  }

  Future<String> _callNumber(String phoneNumber) async {
    String _message;
    try {
      final String result = await platformMethodChannel.invokeMethod('callNumber', {'phoneNumber': phoneNumber});
      _message = result;
    } on PlatformException catch (e) {
      _message = "Can't do native stuff ${e.message}.";
    }
    setState(() {
      nativeMessage = _message;
    });

    return _message;
  }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Application"),
      ),
      body: Center(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(left: 8.0, right: 8.0, top: 102.0),
              child: Center(
                child: FlatButton.icon(
                  icon: Icon(
                    Icons.power_settings_new,
                    size: 50,
                    color: Colors.amber,
                  ),
                  label: Text(''),
                  textColor: Colors.white,
                  ///
                  /// We’ll run this function when the power icon on the app is pressed
                  ///
                  onPressed: _managePower,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(left: 8.0, right: 8.0, top: 25.0),
              child: Center(
                child: FlatButton.icon(
                  icon: Icon(
                    Icons.camera_alt,
                    size: 50,
                    color: Colors.amber,
                  ),
                  label: Text(''),
                  textColor: Colors.white,
                  ///
                  /// We’ll run this function when the power icon on the app is pressed
                  ///
                  onPressed: _launchCamera,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(left: 8.0, right: 8.0, top: 25.0),
              child: Center(
                child: FlatButton.icon(
                  icon: Icon(
                    Icons.battery_unknown,
                    size: 50,
                    color: Colors.amber,
                  ),
                  label: Text(''),
                  textColor: Colors.white,
                  ///
                  /// We’ll run this function when the power icon on the app is pressed
                  ///
                  onPressed: _getBatteryLevel,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(left: 8.0, right: 8.0, top: 25.0),
              child: Center(
                child: FlatButton.icon(
                  icon: Icon(
                    Icons.call,
                    size: 50,
                    color: Colors.amber,
                  ),
                  label: Text(''),
                  textColor: Colors.white,
                  ///
                  /// We’ll run this function when the power icon on the app is pressed
                  ///
                  onPressed: (){
                    _callNumber("555865454");
                  },
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(left: 8.0, right: 8.0, top: 25.0),
              child: Center(
                ///
                /// We’ll then display the response we receive as text:
                ///
                child: Text(
                  _chargingStatus,
                  style: TextStyle(
                      color: Colors.amber[900],
                      fontWeight: FontWeight.w500,
                      fontSize: 23.0),
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(left: 8.0, right: 8.0, top: 25.0),
              child: Center(
                ///
                /// We’ll then display the response we receive as text:
                ///
                child: Text(
                  nativeMessage,
                  style: TextStyle(
                      color: Colors.amber[900],
                      fontWeight: FontWeight.w500,
                      fontSize: 23.0),
                ),
              ),
            ),
            Row(
              children: <Widget>[
                Padding(
                  padding: const EdgeInsets.only(left: 8.0, right: 8.0, top: 25.0),
                  child: Center(
                    child: FlatButton.icon(
                      icon: Icon(
                        Icons.play_arrow,
                        size: 50,
                        color: Colors.green,
                      ),
                      label: Text(''),
                      textColor: Colors.white,
                      ///
                      /// We’ll run this function when the power icon on the app is pressed
                      ///
                      onPressed: (){
                        _enableEventReceiver();
                      },
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.only(left: 8.0, right: 8.0, top: 25.0),
                  child: Center(
                    ///
                    /// We’ll then display the response we receive as text:
                    ///
                    child: Text(
                      _platformMessage,
                      style: TextStyle(
                          color: Colors.amber[900],
                          fontWeight: FontWeight.w500,
                          fontSize: 23.0),
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.only(left: 8.0, right: 8.0, top: 25.0),
                  child: Center(
                    child: FlatButton.icon(
                      icon: Icon(
                        Icons.stop,
                        size: 50,
                        color: Colors.amber,
                      ),
                      label: Text(''),
                      textColor: Colors.white,
                      ///
                      /// We’ll run this function when the power icon on the app is pressed
                      ///
                      onPressed: (){
                        _disableEventReceiver();
                      },
                    ),
                  ),
                ),
              ],
            ),

          ],
        ),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
