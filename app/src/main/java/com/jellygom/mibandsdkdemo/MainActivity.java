package com.jellygom.mibandsdkdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.jellygom.miband_sdk.MiBandIO.Listener.HeartrateListener;
import com.jellygom.miband_sdk.MiBandIO.MibandCallback;
import com.jellygom.miband_sdk.MiBandIO.Model.UserInfo;
import com.jellygom.miband_sdk.Miband;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  private Miband miband;
  private BluetoothAdapter mBluetoothAdapter;

  private TextView heart;
  private TextView text;

  private HeartrateListener heartrateNotifyListener = new HeartrateListener() {
    @Override
    public void onNotify(final int heartRate) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          heart.setText(heartRate + " bpm");
          text.append(heartRate + " bpm\n");
        }
      });
    }
  };

  private final MibandCallback mibandCallback = new MibandCallback() {
    @Override
    public void onSuccess(Object data, int status) {
      switch (status) {
        case MibandCallback.STATUS_SEARCH_DEVICE:
          Log.e(TAG, "성공: STATUS_SEARCH_DEVICE");
          miband.connect((BluetoothDevice) data, this);
          break;
        case MibandCallback.STATUS_CONNECT:
          Log.e(TAG, "성공: STATUS_CONNECT");
          miband.getUserInfo(this);
          break;
        case MibandCallback.STATUS_SEND_ALERT:
          Log.e(TAG, "성공: STATUS_SEND_ALERT");
          break;
        case MibandCallback.STATUS_GET_USERINFO:
          Log.e(TAG, "성공: STATUS_GET_USERINFO");
          UserInfo userInfo = new UserInfo().fromByteData(((BluetoothGattCharacteristic) data).getValue());
          miband.setUserInfo(userInfo, this);
          break;
        case MibandCallback.STATUS_SET_USERINFO:
          Log.e(TAG, "성공: STATUS_SET_USERINFO");
          miband.setHeartRateScanListener(heartrateNotifyListener);
          break;
        case MibandCallback.STATUS_START_HEARTRATE_SCAN:
          Log.e(TAG, "성공: STATUS_START_HEARTRATE_SCAN");
          break;
      }
    }

    @Override
    public void onFail(int errorCode, String msg, int status) {
      switch (status) {
        case MibandCallback.STATUS_SEARCH_DEVICE:
          Log.e(TAG, "실패: STATUS_SEARCH_DEVICE");
          break;
        case MibandCallback.STATUS_CONNECT:
          Log.e(TAG, "실패: STATUS_CONNECT");
          break;
        case MibandCallback.STATUS_SEND_ALERT:
          Log.e(TAG, "실패: STATUS_SEND_ALERT");
          break;
        case MibandCallback.STATUS_GET_USERINFO:
          Log.e(TAG, "실패: STATUS_GET_USERINFO");
          break;
        case MibandCallback.STATUS_SET_USERINFO:
          Log.e(TAG, "실패: STATUS_SET_USERINFO");
          break;
        case MibandCallback.STATUS_START_HEARTRATE_SCAN:
          Log.e(TAG, "실패: STATUS_START_HEARTRATE_SCAN");
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.button_vive).setOnClickListener(this);
    findViewById(R.id.button_heart_start_one).setOnClickListener(this);
    findViewById(R.id.button_heart_start_many).setOnClickListener(this);

    heart = (TextView) findViewById(R.id.heart);
    text = (TextView) findViewById(R.id.text);

    final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    mBluetoothAdapter = bluetoothManager.getAdapter();

    miband = new Miband(getApplicationContext());

    // Miband 디바이스 검색
    miband.searchDevice(mBluetoothAdapter, this.mibandCallback);
  }

  @Override
  public void onClick(View view) {
    int i = view.getId();
    if (i == R.id.button_vive) {
      miband.sendAlert(this.mibandCallback);
    } else if (i == R.id.button_heart_start_one) {
      miband.startHeartRateScan(1, this.mibandCallback);
    } else if (i == R.id.button_heart_start_many) {
      miband.startHeartRateScan(0, this.mibandCallback);
    }
  }

}
