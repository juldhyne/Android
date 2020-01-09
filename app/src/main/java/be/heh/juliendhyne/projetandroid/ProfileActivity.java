package be.heh.juliendhyne.projetandroid;

import androidx.appcompat.app.AppCompatActivity;
import be.heh.juliendhyne.projetandroid.DB.User;
import be.heh.juliendhyne.projetandroid.DB.UserAccessBDD;
import be.heh.juliendhyne.projetandroid.utils.PasswordSecurity;
import be.heh.juliendhyne.projetandroid.utils.SessionManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    ImageView profile_pic;
    TextView tv_username;
    TextView tv_usermail;
    Button btn_modifiy_passwd;
    Button btn_delete_account;

    private SessionManager session;
    String mail;

    UserAccessBDD userDB = new UserAccessBDD(this);
    User user;
    boolean x = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        profile_pic = findViewById(R.id.iv_profile_pic);
        tv_username = findViewById(R.id.tv_username);
        tv_usermail = findViewById(R.id.tv_usermail);
        btn_modifiy_passwd = findViewById(R.id.btn_modify_passwd);
        btn_delete_account = findViewById(R.id.btn_delete_account);

        tv_username.setText(user.get("sessionFirstName") + " " + user.get("sessionLastName"));
        tv_usermail.setText("Adresse Mail: " + user.get("sessionMail"));
        mail = user.get("sessionMail");

        userDB.openForRead();
        Cursor c = userDB.myCursor();
    }

    public void onProfileClickManager(View V) {
        switch (V.getId()) {
            case R.id.btn_modify_passwd:
                user = userDB.getUserByMail(mail);
                createPasswordUserDialog();
                break;
            case R.id.btn_delete_account:
                user = userDB.getUserByMail(mail);
                createDeleteUserDialog();
                Toast.makeText(this, "Votre compte a été supprimé", Toast.LENGTH_SHORT).show();
                Intent intentMain = new Intent(this, MainActivity.class);
                if(x) {startActivity(intentMain);}
                break;

        }
    }

    public void createPasswordUserDialog() {

        final EditText input = new EditText(ProfileActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Change my Password - " + user.getEmail());
        builder.setView(input);

        builder.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PasswordSecurity password = new PasswordSecurity(input.getText().toString().trim());
                userDB.openForWrite();
                user.setPassword(password.getPasswordSecured());
                userDB.updateUser(user.getId(), user);
                userDB.Close();

            }
        })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();

    }

    public void createDeleteUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(user.getFirstname() + " " + user.getLastname() + " - Delete User");
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userDB.openForWrite();
                userDB.removeUser(user.getEmail());
                userDB.Close();
                x = true;
            }
        })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
    }
}
