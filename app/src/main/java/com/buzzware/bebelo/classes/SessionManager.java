package com.buzzware.bebelo.classes;



import static com.buzzware.bebelo.classes.Constant.SessionManagerConstants.FILTER;
import static com.buzzware.bebelo.classes.Constant.SessionManagerConstants.LATANDLNG;
import static com.buzzware.bebelo.classes.Constant.SessionManagerConstants.LOGGED_IN;
import static com.buzzware.bebelo.classes.Constant.SessionManagerConstants.USER_INFO_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.buzzware.bebelo.retrofit.Login.LoginResponse;
import com.google.gson.Gson;

public class SessionManager {

    private static SharedPreferences.Editor prefsEditor;
    private static SharedPreferences prefrences;
    private static SessionManager sessionManager;

    public static SessionManager getInstance() {
        if (sessionManager == null)
            sessionManager = new SessionManager();
        return sessionManager;
    }


    public void setUser(Context c, LoginResponse loginResponse) {
        prefrences = PreferenceManager
                .getDefaultSharedPreferences(c);
        prefsEditor = prefrences.edit();
        prefsEditor.putString(USER_INFO_KEY, new Gson().toJson(loginResponse));
        prefsEditor.commit();
    }

    public void setFilter(Context c, String filter) {
        prefrences = PreferenceManager
                .getDefaultSharedPreferences(c);
        prefsEditor = prefrences.edit();
        prefsEditor.putString(FILTER, filter);
        prefsEditor.commit();
    }

    public void setLastLoc(Context c, String latAndLng) {
        prefrences = PreferenceManager
                .getDefaultSharedPreferences(c);
        prefsEditor = prefrences.edit();
        prefsEditor.putString(LATANDLNG, latAndLng);
        prefsEditor.commit();
    }

    public String getLastLoc(Context c) {
        prefrences = PreferenceManager
                .getDefaultSharedPreferences(c);
        String s = prefrences.getString(LATANDLNG, null);
        return s;
    }

    public String getFilter(Context c) {
        prefrences = PreferenceManager
                .getDefaultSharedPreferences(c);
        String s = prefrences.getString(FILTER, null);
        return s;
    }

    public LoginResponse getUser(Context c) {
        prefrences = PreferenceManager
                .getDefaultSharedPreferences(c);
        String s = prefrences.getString(USER_INFO_KEY, null);
        return new Gson().fromJson(s, LoginResponse.class);
    }



}
