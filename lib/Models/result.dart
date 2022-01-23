// To parse this JSON data, do
//
//     final integerNullString = integerNullStringFromJson(jsonString);

import 'dart:convert';

Result integerNullStringFromJson(String str) =>
    Result.fromJson(json.decode(str));

String integerNullStringToJson(Result data) =>
    json.encode(data.toJson());

class Result {
  Result({
    this.message = "",
    this.permissionGranted = false,
    this.locationRequirement = false,
    this.wifiConnected= false,
    this.ssid= "",
    this.bssid = "",
  });

  String? message;
  bool? permissionGranted;
  bool? locationRequirement;
  bool? wifiConnected;
  String? ssid;
  String? bssid;

  factory Result.fromJson(Map<String, dynamic> json) =>
      Result(
        message: json["message"] == null ? null : json["message"],
        permissionGranted: json["permissionGranted"] == null
            ? null
            : json["permissionGranted"],
        locationRequirement: json["locationRequirement"] == null
            ? null
            : json["locationRequirement"],
        wifiConnected:
            json["wifiConnected"] == null ? null : json["wifiConnected"],
        ssid: json["ssid"] == null ? null : json["ssid"],
        bssid: json["bssid"] == null ? null : json["bssid"],
      );

  Map<String, dynamic> toJson() => {
        "message": message == null ? null : message,
        "permissionGranted":
            permissionGranted == null ? null : permissionGranted,
        "locationRequirement":
            locationRequirement == null ? null : locationRequirement,
        "wifiConnected": wifiConnected == null ? null : wifiConnected,
        "ssid": ssid == null ? null : ssid,
        "bssid": bssid == null ? null : bssid,
      };
}
