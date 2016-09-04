package com.jellygom.miband_sdk.MiBandIO.Listener;

/**
 * Create  : Iyeonghun
 * Date    : 2016. 9. 1.
 * Content : Notify 리스너 인터페이스
 */
public interface NotifyListener {
  void onNotify(byte[] data);
}
