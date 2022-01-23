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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

        
import com.espressif.esptouch.android.EspTouchActivityAbs;
import com.espressif.esptouch.android.EspTouchApp;
import com.espressif.esptouch.android.v1.EspTouchActivity;
import com.example.native_test_2.R;
// import com.espressif.esptouch.android.databinding.ActivityEsptouchBinding;
import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
//import com.espressif.iot.esptouch.util.TouchNetUtil ;
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
  private EsptouchAsyncTask4 mTask;


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
                result.error("Error",e.getMessage(),"asd");
                }
               result.success(manJson.toString());
            }
            
            else if(call.method.equals("transmit")){
              try{
                System.out.println(call.argument("bssid").toString());
                executeEsptouch(call.argument("ssid"),call.argument("bssid"),call.argument("password"),call.argument("isBroadcast"));
              }catch( Exception e){
                 System.out.println(e.getMessage());
                result.error("Error",e.getMessage(),"asd");
                }
            }
            
            
            
            else {
              result.notImplemented();
            }
          }
        );
  }

  public void executeEsptouch(java.lang.String mSsid, java.lang.String mBssid, java.lang.String Password, java.lang.Boolean isBroadCast ) {
        byte[] ssid = ByteUtil.getBytesByString(mSsid);
        byte[] password = Password == null ? null : ByteUtil.getBytesByString(Password);
        byte[] bssid = com.espressif.iot.esptouch.util.TouchNetUtil.parseBssid2bytes(mBssid);
         byte[] deviceCount =  new byte[0] ;
        byte[] broadcast = {(byte) (isBroadCast? 1 : 0)};

        if (mTask != null) {
            mTask.cancelEsptouch();
        }
        mTask = new EsptouchAsyncTask4(this);
        mTask.execute(ssid, bssid, password, deviceCount, broadcast);
    }

        private static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
        private final WeakReference<MainActivity> mActivity;

        private final Object mLock = new Object();
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;

        EsptouchAsyncTask4(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        void cancelEsptouch() {
            cancel(true);
           
        }

        @Override
        protected void onPreExecute() {
            // MainActivity activity = mActivity.get();
            // activity.showProgress(true);
        }

        @Override
        protected void onProgressUpdate(IEsptouchResult... values) {
            // MainActivity activity = mActivity.get();
            // if (activity != null) {
            //     IEsptouchResult result = values[0];
            //     Log.i(TAG, "EspTouchResult: " + result);
            //     String text = result.getBssid() + " is connected to the wifi";
            //     Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();

            // }
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
          MainActivity activity = mActivity.get();
            int taskResultCount;
            synchronized (mLock) {
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                byte[] deviceCountData = params[3];
                byte[] broadcastData = params[4];
                taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
               Context context = activity.getApplicationContext();
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
                mEsptouchTask.setEsptouchListener(this::publishProgress);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
         //   MainActivity activity = mActivity.get();
         //   activity.mTask = null;
         //   activity.showProgress(false);
            // if (result == null) {
            //     mResultDialog = new AlertDialog.Builder(activity)
            //             .setMessage(R.string.esptouch1_configure_result_failed_port)
            //             .setPositiveButton(android.R.string.ok, null)
            //             .show();
            //     mResultDialog.setCanceledOnTouchOutside(false);
            //     return;
            // }

            // check whether the task is cancelled and no results received
            IEsptouchResult firstResult = result.get(0);
            if (firstResult.isCancelled()) {
                return;
            }
            // the task received some results including cancelled while
            // executing before receiving enough results

            // if (!firstResult.isSuc()) {
            //     mResultDialog = new AlertDialog.Builder(activity)
            //             .setMessage(R.string.esptouch1_configure_result_failed)
            //             .setPositiveButton(android.R.string.ok, null)
            //             .show();
            //     mResultDialog.setCanceledOnTouchOutside(false);
            //     return;
            // }

            ArrayList<CharSequence> resultMsgList = new ArrayList<>(result.size());
            for (IEsptouchResult touchResult : result) {
                // String message = activity.getString(R.string.esptouch1_configure_result_success_item,
                //         touchResult.getBssid(), touchResult.getInetAddress().getHostAddress());
                // resultMsgList.add(message);
            }
            CharSequence[] items = new CharSequence[resultMsgList.size()];
            // mResultDialog = new AlertDialog.Builder(activity)
            //         .setTitle(R.string.esptouch1_configure_result_success)
            //         .setItems(resultMsgList.toArray(items), null)
            //         .setPositiveButton(android.R.string.ok, null)
            //         .show();
            mResultDialog.setCanceledOnTouchOutside(false);
        }
    }



  


  //===========================================================================
  //===========================================================================
  //===========================================================================
  //===========================================================================
  //===========================================================================


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