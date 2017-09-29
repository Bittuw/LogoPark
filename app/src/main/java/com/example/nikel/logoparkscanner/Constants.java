package com.example.nikel.logoparkscanner;


/**
 * Created by nikel on 21.09.2017.
 */

public class Constants {

    public static final boolean DebugMode = true;
    public static final String MainFileInfo = "MainFileInfo"; // TODO файл хранения параметров приложения
    public static final String IS_FIRST_LAUNCH = "IsFirstLaunch"; // TODO название поля информации о первом запуске
    public static final String IS_AUTHARIZED = "IsAutharized"; // TODO название поля  информации о состоянии авторизации
    public static final String YES = "Y";
    public static final String NO = "N";


    public static final String ConfirmAuth = "ConfirmAuth"; // TODO строка интента для подтверждения авторизации
    public  static final String ConAuth = "ConAuth";

    public static final String GetData = "Map"; // TODO строка интента для получения массива распарсенного JSON

    public static final String TypeOfDialog = "TypeOfDialog"; // TODO  строка типа диалога
    public static final int ManualDialog = 1; //
    public static final int ConfirmDialog = 2;

    public static final String isRead = "isReadInstruct";
    public static final String isAuth = "isAuthorized";

    public static class IntentParams {
        public static final int Authorizate = 1;
        public static final String Auth = "Authorizate";
        public static final int ReceiveData = 2;
        public static final String RecD = "ReceiveData";
        public static final int StartReceiveCasts = 3;
        public static final String StartRecCas = "StartReceiveCasts";
        public static final String URL = "URL";
        public static final String SendData = "SendData";
        public static final String Data = "Data";
    }

}
