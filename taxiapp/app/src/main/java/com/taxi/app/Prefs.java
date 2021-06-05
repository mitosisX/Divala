package com.taxi.app;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private String fileName = "myPrefs";

    private Context context;

    public Prefs(Context context){this.context=context;}

    public void writeFile(String key, String value){
        SharedPreferences pref = this.context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String readFile(String key){
        SharedPreferences pref = this.context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String def = "DefaultName";
        String name = pref.getString(key, def);
        return name;
    }
}
