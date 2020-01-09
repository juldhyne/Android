package be.heh.juliendhyne.projetandroid;

import androidx.appcompat.app.AppCompatActivity;
import be.heh.juliendhyne.projetandroid.DB.User;
import be.heh.juliendhyne.projetandroid.DB.UserAccessBDD;
import be.heh.juliendhyne.projetandroid.utils.PasswordSecurity;
import be.heh.juliendhyne.projetandroid.utils.SessionManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;

    EditText ET_mail;
    EditText ET_password;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.btn_Login);
        ET_mail = findViewById(R.id.ET_mail);
        ET_password = findViewById(R.id.ET_password);
        session = new SessionManager(this.getApplicationContext());
    }

    public void onLoginClickManager(View v) {
        switch (v.getId()){

            case R.id.btn_Login:
                boolean x = false;
                Log.i("Fonction Login : ", "Entrée dans la fonction");
                UserAccessBDD userDB = new UserAccessBDD(this);
                userDB.openForRead();
                ArrayList<User> tabUser = userDB.getAllUser();
                userDB.Close();
                Log.i("Fonction Login : ", "DB Close réussi");
                PasswordSecurity password = new PasswordSecurity(ET_password.getText().toString());
                Log.i("Password entre : ", ET_password.getText().toString());
                Log.i("Login entre : ", ET_mail.getText().toString());
                for(User user : tabUser) {
                    Log.i("Password db : ", user.getPassword());
                    Log.i("Login db : ", user.getEmail());
                    if(user.getEmail().equals(ET_mail.getText().toString().trim())
                            && user.getPassword().equals(password.getPasswordSecured())) {
                        x = true;
                        Log.i("Fonction Login : ", "Entre dans le if");
                        session.createSession(ET_mail.getText().toString().trim(),user.getFirstname(), user.getLastname(), user.getLevel());

                        Log.i("Fonction Login : ", "Crée la session");

                        Intent intentHome =
                                new Intent(this, HomeActivity.class);

                        startActivity(intentHome);
                        break;
                    }
                }
                if(!x)Toast.makeText(this,"Cet utilisateur n'existe pas", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
