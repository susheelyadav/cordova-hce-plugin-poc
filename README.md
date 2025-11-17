# cordova-plugin-hce

Cordova plugin to emulate an NFC tag via Android HCE and serve a text message.

## Install locally
From your Cordova project root:
```
cordova plugin add /path/to/cordova-hce-plugin
```

## API
- `window.hce.setMessage(message, success, error)` — sets the text that the HCE service will serve as an NDEF Text record.

## Notes
- This is a simplified example. For compatibility with many readers you may need to implement the full Type-4 file system (CAP + NDEF files, READ BINARY, correct SWs).
- Change the AID in `res/xml/apduservice.xml` to match your reader or other phone's reader-side SELECT AID.
