package com.example.hceplugin;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

public class HCEService extends HostApduService {
    private static final String TAG = "HCEService";

    // Status words
    private static final byte[] STATUS_SUCCESS = {(byte)0x90, (byte)0x00};
    private static final byte[] STATUS_NOT_SUPPORTED = {(byte)0x6A, (byte)0x82};

    // SELECT AID APDU header: CLA INS P1 P2
    private static final byte[] SELECT_APDU_PREFIX = {(byte)0x00, (byte)0xA4, (byte)0x04, (byte)0x00};

    // Example NDEF text (will be constructed as bytes when needed)
    private byte[] ndefMessageBytes = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "HCEService created");
        String msg = HCEServiceSingleton.getMessage();
        if (msg == null) msg = "Hello from HCE";
        ndefMessageBytes = buildNdefText(msg);
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        if (commandApdu == null) return STATUS_NOT_SUPPORTED;

        Log.i(TAG, "Received APDU: " + bytesToHex(commandApdu));

        // If this is a SELECT AID command, reply with success.
        if (isSelectAidApdu(commandApdu)) {
            Log.i(TAG, "SELECT AID received");
            return STATUS_SUCCESS;
        }

        // Simplified: respond to any other APDU by returning the complete NDEF message + success
        byte[] response = new byte[ndefMessageBytes.length + STATUS_SUCCESS.length];
        System.arraycopy(ndefMessageBytes, 0, response, 0, ndefMessageBytes.length);
        System.arraycopy(STATUS_SUCCESS, 0, response, ndefMessageBytes.length, STATUS_SUCCESS.length);
        Log.i(TAG, "Returning NDEF (" + ndefMessageBytes.length + " bytes) + SW");
        return response;
    }

    @Override
    public void onDeactivated(int reason) {
        Log.i(TAG, "Deactivated: " + reason);
    }

    private boolean isSelectAidApdu(byte[] apdu) {
        if (apdu.length < 4) return false;
        for (int i = 0; i < 4; i++) {
            if (apdu[i] != SELECT_APDU_PREFIX[i]) return false;
        }
        return true;
    }

    private byte[] buildNdefText(String text) {
        try {
            byte[] textBytes = text.getBytes("UTF-8");
            byte[] ndefHeader = new byte[] {
                (byte)0xD1,
                0x01,
                (byte)(textBytes.length + 3),
                0x54
            };

            byte status = 0x02;
            byte[] lang = new byte[] { 'e', 'n' };

            byte[] payload = new byte[1 + lang.length + textBytes.length];
            payload[0] = status;
            System.arraycopy(lang, 0, payload, 1, lang.length);
            System.arraycopy(textBytes, 0, payload, 1 + lang.length, textBytes.length);

            byte[] ndef = new byte[ndefHeader.length + payload.length];
            System.arraycopy(ndefHeader, 0, ndef, 0, ndefHeader.length);
            System.arraycopy(payload, 0, ndef, ndefHeader.length, payload.length);

            return ndef;
        } catch (Exception e) {
            Log.e(TAG, "Error building NDEF", e);
            return new byte[0];
        }
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "";
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
