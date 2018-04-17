package com.apppubs.util;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class DeviceUuidFactory {

	protected static UUID uuid;

	public static String DeviceUuidFactory(Context context) {

		if (uuid == null) {
			synchronized (DeviceUuidFactory.class) {
				if (uuid == null) {

						final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

						// Use the Android ID unless it's broken, in which case
						// fallback on deviceId,
						// unless it's not available, then fallback on a random
						// number which we store
						// to a prefs file
						try {
							if (!"9774d56d682e549c".equals(androidId)) {
								uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
							} else {
								final String deviceId = ((TelephonyManager) context
										.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
								uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID
										.randomUUID();
								return uuid.toString();
							}
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException(e);
						}

						// Write the value out to the prefs file

					}

				}
		}
		return uuid.toString();

	}
}
