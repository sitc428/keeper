package com.gooduct.keeper.helpers;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Vincent on 2016/10/26.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_FORCE_WELCOME = "IsForceWelcome";

    public boolean isForceWelcomePage = false;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setForceWelcome(boolean isForceWelcome) {
        editor.putBoolean(IS_FORCE_WELCOME, isForceWelcome);
        editor.commit();
    }

    public boolean isForceWelcome() {
        return pref.getBoolean(IS_FORCE_WELCOME, true);
    }
}
