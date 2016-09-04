package com.jellygom.miband_sdk.MiBandIO.Model;

import java.util.UUID;

/**
 * Create  : Iyeonghun
 * Date    : 2016. 9. 1.
 * Content : Mi 밴드 사용 UUID
 */
public class MibandUUID {

  // Mac 주소 필터, 미밴드2는 실물 기기 구할 시 업데이트 예정
  public static final String MAC_FILTER_MI1S = "C8:0F:10";
  public static final String MAC_FILTER_MI2 = "C8:0F:10";

  // UUID 기본 형식
  public static final String BASIC_UUID = "0000%s-0000-1000-8000-00805f9b34fb";

  // 기본 UUID
  public static final UUID UUID_SERVICE_MIBAND = UUID.fromString(String.format(BASIC_UUID, "FEE0"));
  public static final UUID UUID_CHARACTERISTIC_USER_INFO = UUID.fromString(String.format(BASIC_UUID, "FF04"));
  public static final UUID UUID_DESCRIPTOR_UPDATE_NOTIFICATION = UUID.fromString(String.format(BASIC_UUID, "2902"));

  // 알람 서비스 UUID 및 특성 UUID
  public static final UUID UUID_SERVICE_ALERT = UUID.fromString(String.format(BASIC_UUID, "1802"));
  public static final UUID UUID_CHARACTERISTIC_ALERT_LEVEL = UUID.fromString(String.format(BASIC_UUID, "2A06"));

  // 심박센서 서비스 UUID 및 특성 UUID
  public static final UUID UUID_SERVICE_HEART_RATE = UUID.fromString(String.format(BASIC_UUID, "180D"));
  public static final UUID UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT = UUID.fromString(String.format(BASIC_UUID, "2A39"));
  public static final UUID UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT = UUID.fromString(String.format(BASIC_UUID, "2A37"));

  // 걸음수 특성 UUID
  public static final UUID UUID_CHARACTERISTIC_REALTIME_STEPS = UUID.fromString(String.format(BASIC_UUID, "FF06"));


}
