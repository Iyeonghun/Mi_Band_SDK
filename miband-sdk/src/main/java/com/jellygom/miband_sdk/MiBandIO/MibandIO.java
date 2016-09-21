package com.jellygom.miband_sdk.MiBandIO;

import android.bluetooth.*;
import android.content.Context;
import android.util.Log;
import com.jellygom.miband_sdk.MiBandIO.Listener.NotifyListener;
import com.jellygom.miband_sdk.MiBandIO.Model.MibandUUID;

import java.util.HashMap;
import java.util.UUID;

public class MibandIO extends BluetoothGattCallback {

  private static final String TAG = MibandIO.class.getSimpleName();

  private int mStatus = 0;

  private BluetoothGatt gatt;
  private MibandCallback currentCallback;
  private HashMap<UUID, NotifyListener> notifyListeners = new HashMap<>();
  private NotifyListener disconnectedListener = null;

  public void setStatus(int status) {
    this.mStatus = status;
  }

  public BluetoothDevice getDevice() {
    if (null == gatt) {
      Log.e(TAG, "실패 getDevice");
      return null;
    }
    return gatt.getDevice();
  }

  public void connect(final Context context, BluetoothDevice device, final MibandCallback callback) {
    MibandIO.this.currentCallback = callback;
    device.connectGatt(context, false, MibandIO.this);
  }

  public void setDisconnectedListener(NotifyListener disconnectedListener) {
    this.disconnectedListener = disconnectedListener;
  }

  public void readCharacteristic(UUID characteristicUUID, MibandCallback callback) {
    try {
      if (null == gatt) {
        throw new Exception("실패");
      }
      this.currentCallback = callback;
      BluetoothGattCharacteristic chara = gatt.getService(MibandUUID.UUID_SERVICE_MIBAND).getCharacteristic(characteristicUUID);
      if (null == chara) {
        this.onFail(-1, "실패");
        return;
      }
      if (!this.gatt.readCharacteristic(chara)) {
        this.onFail(-1, "실패");
      }
    } catch (Throwable tr) {
      this.onFail(-1, "실패");
    }
  }

  public void writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value, MibandCallback callback) {
    try {
      if (null == gatt) {
        throw new Exception("실패");
      }
      this.currentCallback = callback;
      BluetoothGattCharacteristic chara = gatt.getService(serviceUUID).getCharacteristic(characteristicUUID);
      if (null == chara) {
        this.onFail(-1, "실패");
        return;
      }
      chara.setValue(value);
      if (!this.gatt.writeCharacteristic(chara)) {
        this.onFail(-1, "실패");
      }
    } catch (Throwable tr) {
      this.onFail(-1, "실패");
    }
  }

  public void setNotifyListener(UUID serviceUUID, UUID characteristicUUID, NotifyListener listener) {
    if (null == gatt) {
      Log.e(TAG, "실패");
      return;
    }
    BluetoothGattCharacteristic chara = gatt.getService(serviceUUID).getCharacteristic(characteristicUUID);
    if (null == chara) {
      Log.e(TAG, "실패");
      return;
    }
    this.gatt.setCharacteristicNotification(chara, true);
    BluetoothGattDescriptor descriptor = chara.getDescriptor(MibandUUID.UUID_DESCRIPTOR_UPDATE_NOTIFICATION);
    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    this.gatt.writeDescriptor(descriptor);
    this.notifyListeners.put(characteristicUUID, listener);
  }

  @Override
  public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    super.onConnectionStateChange(gatt, status, newState);
    if (newState == BluetoothProfile.STATE_CONNECTED) {
      gatt.discoverServices();
    }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
      gatt.close();
      if (this.disconnectedListener != null)
        this.disconnectedListener.onNotify(null);
    }
  }

  @Override
  public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    super.onCharacteristicRead(gatt, characteristic, status);
    if (BluetoothGatt.GATT_SUCCESS == status) {
      this.onSuccess(characteristic);
    } else {
      this.onFail(status, "실패");
    }
  }

  @Override
  public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    super.onCharacteristicWrite(gatt, characteristic, status);
    if (BluetoothGatt.GATT_SUCCESS == status) {
      this.onSuccess(characteristic);
    } else {
      this.onFail(status, "실패");
    }
  }

  @Override
  public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    super.onServicesDiscovered(gatt, status);
    if (status == BluetoothGatt.GATT_SUCCESS) {
      this.gatt = gatt;
      this.onSuccess(null);
    } else {
      this.onFail(status, "실패");
    }
  }

  @Override
  public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
    super.onCharacteristicChanged(gatt, characteristic);
    if (this.notifyListeners.containsKey(characteristic.getUuid())) {
      this.notifyListeners.get(characteristic.getUuid()).onNotify(characteristic.getValue());
    }
  }

  private void onSuccess(Object data) {
    if (this.currentCallback != null) {
      MibandCallback callback = this.currentCallback;
      this.currentCallback = null;
      callback.onSuccess(data, mStatus);
    }
  }

  private void onFail(int errorCode, String msg) {
    if (this.currentCallback != null) {
      MibandCallback callback = this.currentCallback;
      this.currentCallback = null;
      callback.onFail(errorCode, msg, mStatus);
    }
  }
}
