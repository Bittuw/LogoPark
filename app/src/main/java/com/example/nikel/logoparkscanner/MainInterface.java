package com.example.nikel.logoparkscanner;


/**
 * Created by nikel on 21.09.2017.
 */

public interface MainInterface {

    static final boolean DebugMode = true;
    static final String MainFileInfo = "MainFileInfo"; // TODO файл хранения параметров приложения
    static final String IS_FIRST_LAUNCH = "IsFirstLaunch"; // TODO название поля информации о первом запуске
    static final String IS_AUTHARIZED = "IsAutharized"; // TODO название поля  информации о состоянии авторизации
    static final String YES = "Y";
    static final String NO = "N";


    static final String ConfirmAuth = "ConfirmAuth"; // TODO строка интента для подтверждения авторизации
    static final String ConAuth = "ConAuth";

    static final String GetData = "Map"; // TODO строка интента для получения массива распарсенного JSON

    static final String TypeOfDialog = "TypeOfDialog"; // TODO  строка типа диалога
    static final int ManualDialog = 1; //
    static final int ConfirmDialog = 2;

    static final String isRead = "isReadInstruct";
    static final String isAuth = "isAuthorized";

     static class IntentParams {
        static final int Authorizate = 1;
        static final String Auth = "Authorizate";
        static final int ReceiveData = 2;
        static final String RecD = "ReceiveData";
        static final int StartReceiveCasts = 3;
        static final String StartRecCas = "StartReceiveCasts";
    }

}
