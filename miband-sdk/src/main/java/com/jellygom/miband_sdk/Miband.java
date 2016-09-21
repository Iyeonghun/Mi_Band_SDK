package com.jellygom.miband_sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.util.Log;
import com.jellygom.miband_sdk.MiBandIO.Listener.HeartrateListener;
import com.jellygom.miband_sdk.MiBandIO.Listener.NotifyListener;
import com.jellygom.miband_sdk.MiBandIO.Listener.RealtimeStepListener;
import com.jellygom.miband_sdk.MiBandIO.MibandCallback;
import com.jellygom.miband_sdk.MiBandIO.MibandIO;
import com.jellygom.miband_sdk.MiBandIO.Model.MibandUUID;
import com.jellygom.miband_sdk.MiBandIO.Model.UserInfo;

import java.util.Arrays;
import java.util.Set;

public class Miband {

  private static final String TAG = Miband.class.getSimpleName();

  private Context context;
  private MibandIO mibandIO;

  public Miband(Context context) {
    this.context = context;
    this.mibandIO = new MibandIO();
  }

  /**
   * Miband 디바이스 찾기, Mi fit 앱에서 먼저 bonded 된 Device를 찾는다.
   *
   * @param mBluetoothAdapter
   * @param callback
   */
  public void searchDevice(final BluetoothAdapter mBluetoothAdapter, final MibandCallback callback) {
    this.mibandIO.setStatus(MibandCallback.STATUS_SEARCH_DEVICE);
    Set<BluetoothDevice> pairDevices = mBluetoothAdapter.getBondedDevices();
    BluetoothDevice mibandDevice = null;
    if (pairDevices.size() > 0) {
      for (BluetoothDevice device : pairDevices) {
        String deviceMac = device.getAddress().substring(0, 8);
        if (MibandUUID.MAC_FILTER_MI1S.equals(deviceMac)) {
          mibandDevice = device;
          break;
        }
      }
    }
    if (mibandDevice == null)
      callback.onFail(-1, "실패", MibandCallback.STATUS_SEARCH_DEVICE);
    else
      callback.onSuccess(mibandDevice, MibandCallback.STATUS_SEARCH_DEVICE);
  }

  /**
   * Miband 연결
   *
   * @param device
   * @param callback
   */

  public void connect(final BluetoothDevice device, final MibandCallback callback) {
    this.mibandIO.setStatus(MibandCallback.STATUS_CONNECT);
    this.mibandIO.connect(context, device, callback);
  }

  /**
   * 미밴드 연결 해제 확인리스너.
   *
   * @param disconnectedListener
   */
  public void setDisconnectedListener(NotifyListener disconnectedListener) {
    this.mibandIO.setDisconnectedListener(disconnectedListener);
  }

  /**
   * LED와 함께 진동알람을 2번 발생한다.
   *
   * @param callback
   */
  public void sendAlert(final MibandCallback callback) {
    byte[] protocol = {1};
    this.mibandIO.setStatus(MibandCallback.STATUS_SEND_ALERT);
    this.mibandIO.writeCharacteristic(MibandUUID.UUID_SERVICE_ALERT, MibandUUID.UUID_CHARACTERISTIC_ALERT_LEVEL, protocol, callback);
  }

  /**
   * 저장된 UserInfo를 가져온다, 심박수 측정전 반드시 해야한다.
   *
   * @param callback
   */
  public void getUserInfo(final MibandCallback callback) {
    this.mibandIO.getDevice();
    this.mibandIO.setStatus(MibandCallback.STATUS_GET_USERINFO);
    this.mibandIO.readCharacteristic(MibandUUID.UUID_CHARACTERISTIC_USER_INFO, callback);
  }

  /**
   * UserInfo를 저장한다, 심박수 측정 전 반드시 해야한다.
   *
   * @param userInfo
   * @param callback
   */
  public void setUserInfo(final UserInfo userInfo, final MibandCallback callback) {

    BluetoothDevice device = this.mibandIO.getDevice();
    byte[] data = userInfo.getBytes(device.getAddress());
    this.mibandIO.setStatus(MibandCallback.STATUS_SET_USERINFO);
    this.mibandIO.writeCharacteristic(MibandUUID.UUID_SERVICE_MIBAND, MibandUUID.UUID_CHARACTERISTIC_USER_INFO, data, callback);
  }


  /**
   * 심박수를 측정 시작한다.
   * 단, setUserInfo가 완전하게 성공하였을 때만 정상적으로 작동한다
   *
   * @param mode     : 1 한번, 0 약 10번
   * @param callback
   */
  public void startHeartRateScan(final int mode, final MibandCallback callback) {
    byte[] protocol = {21, 1, 1};
    if (1 == mode) {
      protocol = new byte[]{21, 2, 1};
    }
    this.mibandIO.setStatus(MibandCallback.STATUS_START_HEARTRATE_SCAN);
    this.mibandIO.writeCharacteristic(MibandUUID.UUID_SERVICE_HEART_RATE, MibandUUID.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT, protocol, callback);
  }

  /**
   * 실시간으로 심박수 값을 받아온다.
   *
   * @param listener
   */
  public void setHeartRateScanListener(final HeartrateListener listener) {
    this.mibandIO.setNotifyListener(MibandUUID.UUID_SERVICE_HEART_RATE, MibandUUID.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT, new NotifyListener() {
      @Override
      public void onNotify(byte[] data) {
//        Log.d(TAG, Arrays.toString(data));
        if (data.length == 2 && data[0] == 6) {
          int heartRate = data[1] & 0xFF;
          listener.onNotify(heartRate);
        }
      }
    });
  }

  /**
   * 현재 걸음수를 가져온다
   *
   * @param callback
   */
  public void getCurrentSteps(final MibandCallback callback) {
    MibandCallback cb = new MibandCallback() {
      @Override
      public void onSuccess(Object data, int status) {
        byte[] ab = ((BluetoothGattCharacteristic)data).getValue();
        if (ab.length == 4) {
          int steps = ab[3] << 24 | (ab[2] & 0xFF) << 16 | (ab[1] & 0xFF) << 8 | (ab[0] & 0xFF);
          callback.onSuccess((int) steps, MibandCallback.STATUS_GET_ACTIVITY_DATA);
        }
      }

      @Override
      public void onFail(int errorCode, String msg, int status) {
        callback.onFail(errorCode, msg, status);
      }
    };
    this.mibandIO.readCharacteristic(MibandUUID.UUID_CHARACTERISTIC_REALTIME_STEPS, cb);
  }

  /**
   * 실시간으로 걸음수 값을 받아온다.
   *
   * @param listener
   */
  public void setRealtimeStepListener(final RealtimeStepListener listener) {
    this.mibandIO.setNotifyListener(MibandUUID.UUID_SERVICE_MIBAND, MibandUUID.UUID_CHARACTERISTIC_REALTIME_STEPS, new NotifyListener() {
      @Override
      public void onNotify(byte[] data) {
//        Log.d(TAG, Arrays.toString(data));
        if (data.length == 4) {
          int steps = data[3] << 24 | (data[2] & 0xFF) << 16 | (data[1] & 0xFF) << 8 | (data[0] & 0xFF);
          listener.onNotify(steps);
        }
      }
    });
  }

  /**
   * 현재 배터리 잔량을 가져온다.
   *
   * @param callback
   */
  public void getBatteryLevel(final MibandCallback callback) {
    MibandCallback cb = new MibandCallback() {
      @Override
      public void onSuccess(Object data, int status) {
        int level = ((BluetoothGattCharacteristic) data).getValue()[0];
        callback.onSuccess(level, MibandCallback.STATUS_GET_BATTERY);
      }

      @Override
      public void onFail(int errorCode, String msg, int status) {
        callback.onFail(errorCode, msg, status);
      }
    };
    this.mibandIO.readCharacteristic(MibandUUID.UUID_CHARACTERISTIC_BATTERY, cb);
  }
}
