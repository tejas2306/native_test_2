package com.example.native_test_2;

import io.flutter.embedding.android.FlutterActivity;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
        
import com.espressif.esptouch.android.EspTouchActivityAbs;
import com.espressif.esptouch.android.EspTouchApp;
import com.espressif.esptouch.android.v1.EspTouchActivity;
import com.example.native_test_2.R;
// import com.espressif.esptouch.android.databinding.ActivityEsptouchBinding;
import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
//import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.espressif.iot.esptouch2.provision.TouchNetUtil;
import android.util.Log;



import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.location.LocationManagerCompat;

import org.json.JSONObject;
import org.json.JSONArray;



import java.net.InetAddress;


public class MainActivity extends FlutterActivity {
  private static final String CHANNEL = "samples.flutter.dev/battery";
  private WifiManager mWifiManager;

  @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
             mWifiManager = (WifiManager) getApplicationContext().getSystemService("wifi");
    }

  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
  super.configureFlutterEngine(flutterEngine);
    new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
        .setMethodCallHandler(
          (call, result) -> {
            // Note: this method is invoked on the main thread.
            // TODO
            if (call.method.equals("getWifiDetails")) {
               StateResult  res = checkWifi();
               JSONObject manJson = new JSONObject();
               try{
                manJson.put("ssid", res.ssid);
                manJson.put("locationRequirement", res.locationRequirement);
                manJson.put("wifiConnected", res.wifiConnected);
                manJson.put("address", res.address);
                manJson.put("ssidBytes", res.ssidBytes);
                manJson.put("bssid", res.bssid);
                }catch( Exception e){
                 System.out.println(e.getMessage());
                }
               result.success(manJson.toString());
            } else {
              result.notImplemented();
            }
          }
        );
  }
  

  public static class StateResult {
        public CharSequence message = null;

        public boolean permissionGranted = false;

        public boolean locationRequirement = false;

        public boolean wifiConnected = false;
        public boolean is5G = false;
        public InetAddress address = null;
        public String ssid = null;
        public byte[] ssidBytes = null;
        public String bssid = null;
    }

    public StateResult checkWifi() {
        StateResult result = new StateResult();
        result.wifiConnected = false;
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        boolean connected = TouchNetUtil.isWifiConnected(mWifiManager);
        if (!connected) {
            result.message ="Wifi is not connected";
            return result;
        }

        String ssid = TouchNetUtil.getSsidString(wifiInfo);
        int ipValue = wifiInfo.getIpAddress();
        if (ipValue != 0) {
            result.address = TouchNetUtil.getAddress(wifiInfo.getIpAddress());
        } else {  
            result.address = TouchNetUtil.getIPv4Address();
            if (result.address == null) {
                result.address = TouchNetUtil.getIPv6Address();
            }
        }

        result.wifiConnected = true;
        result.message = ssid;
        result.ssid = ssid;
        result.ssidBytes = TouchNetUtil.getRawSsidBytesOrElse(wifiInfo, ssid.getBytes());
        result.bssid = wifiInfo.getBSSID();

        return result;
    }
}