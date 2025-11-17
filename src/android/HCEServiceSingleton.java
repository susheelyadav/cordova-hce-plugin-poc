package com.example.hceplugin;

public class HCEServiceSingleton {
    private static volatile String message = "Hello from HCE";

    public static void setMessage(String m) {
        message = m;
    }

    public static String getMessage() {
        return message;
    }
}
