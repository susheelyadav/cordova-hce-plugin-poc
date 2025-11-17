package com.example.hceplugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class HcePlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("setMessage")) {
            String message = args.getString(0);
            setMessage(message);
            callbackContext.success();
            return true;
        }
        return false;
    }

    private void setMessage(String message) {
        HCEServiceSingleton.setMessage(message);
    }
}
