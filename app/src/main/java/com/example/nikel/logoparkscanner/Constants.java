package com.example.nikel.logoparkscanner;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nikel on 21.09.2017.
 */

public class Constants {

    public static final boolean DebugMode = false;
    public static final String MainFileInfo = "MainFileInfo"; // TODO файл хранения параметров приложения
    public static final String IS_FIRST_LAUNCH = "IsFirstLaunch"; // TODO название поля информации о первом запуске
    public static final String IS_AUTHARIZED = "IsAutharized"; // TODO название поля  информации о состоянии авторизации
    public static final String YES = "Y";
    public static final String NO = "N";

    public static final String TypeOfDialog = "TypeOfDialog"; // TODO  строка типа диалога
    public static final int ManualDialog = 1; //
    public static final int ConfirmDialog = 2;

    public static final String isRead = "isReadInstruct";
    public static final String isAuth = "isAuthorized";
    public static final String Password = "Password";
    public static final String User = "User";

    public static final String Type = "Type";
    public static final String Code = "Code";

    public static final int delay = 1000;
    public static final int period = 5000;

    public static class IntentParams {
        public static final String Auth = "Authorizate";
        public static final String RecData = "ReceiveData";
        public static final String StartRecCas = "StartReceiveCasts";
        public static final String StopService = "StopService";

        public static final String URL = "URL";
        public static final String SendData = "SendData";
        public static final String GetData = "GetData";
        public static final String QR = "qr";
        public static final String foregroundService = "foregroundService";
        public static final String isOnlineTimer = "isOnlineTimer";
        public static final String Picture = "picture";
    }

    public static final String JSON =
            "{\"items\":[{\"status\":\"blabla\", \"non\":\"non\"}]}";

    public static JSONObject getJSON() {
        try {
            return new JSONObject(JSON);
        } catch (JSONException e) {
            Log.e("STATIC METHOD", e.getMessage());
        }
        return null;
    }
}
