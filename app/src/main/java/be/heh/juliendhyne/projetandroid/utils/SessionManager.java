package be.heh.juliendhyne.projetandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.EditText;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class SessionManager {

    private SharedPreferences SPUser;
    private Editor editUser;

    private SharedPreferences SPConfig;
    private Editor editConfig;

    public SessionManager(Context c) {
        SPUser = c.getSharedPreferences("userdetails", MODE_PRIVATE);
        editUser = SPUser.edit();

        SPConfig = c.getSharedPreferences("configdetails", MODE_PRIVATE);
        editConfig = SPConfig.edit();
    }

    public void createConfig(String ip, String rack, String slot) {
        editConfig.putString("configIP", ip);
        editConfig.putString("configRACK", rack);
        editConfig.putString("configSLOT", slot);
        editConfig.commit();
    }

    public void createSession(String mail, String firstname, String lastname, int level) {
        editUser.putBoolean("isSomeoneLogged", true);
        editUser.putString("sessionMail", mail);
        editUser.putString("sessionFirstName", firstname);
        editUser.putString("sessionLastName", lastname);
        editUser.putString("sessionLevel", Integer.toString(level));
        editUser.commit();
    }

    public String IsUserAdmin() {
        return SPUser.getString("sessionLevel", null);
    }

    public void deleteSession() {
        editUser.clear().commit();
    }

    public HashMap<String, String> getUserDetails() {

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<>();

        user.put("sessionMail", SPUser.getString("sessionMail", null));
        user.put("sessionFirstName", SPUser.getString("sessionFirstName", null));
        user.put("sessionLastName", SPUser.getString("sessionLastName", null));
        user.put("sessionLevel", SPUser.getString("sessionLevel", null));

        return user;
    }

    public HashMap<String, String> getConfigDetails() {
        HashMap<String, String> config = new HashMap<>();
        config.put("configIP", SPConfig.getString("configIP", null));
        config.put("configRACK", SPConfig.getString("configRACK", null));
        config.put("configSLOT", SPConfig.getString("configSLOT", null));
        return config;
    }
}
