package be.heh.juliendhyne.projetandroid;

import androidx.appcompat.app.AppCompatActivity;
import be.heh.juliendhyne.projetandroid.utils.SessionManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    Button registerButton;
    private SessionManager session;
    HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.btn_GoToLogin);
        registerButton = findViewById(R.id.btn_GoToRegister);

        session = new SessionManager(getApplicationContext());
        try {
            user = session.getUserDetails();
        }
        catch(Exception e){

        }
        if(user.get("sessionFirstName") != null){
            //An account is registered in session
            Intent intentHome =
                    new Intent(this, HomeActivity.class);

            startActivity(intentHome);
        }
    }

    public void onMainClickManager(View v) {
        switch (v.getId()){

            case R.id.btn_GoToLogin:
                Intent intentLogin = new Intent(this, LoginActivity.class);
                startActivity(intentLogin);
                break;

            case R.id.btn_GoToRegister:
                Intent intentRegister = new Intent(this, RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }

}
