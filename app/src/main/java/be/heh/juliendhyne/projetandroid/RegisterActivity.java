package be.heh.juliendhyne.projetandroid;

import androidx.appcompat.app.AppCompatActivity;
import be.heh.juliendhyne.projetandroid.DB.User;
import be.heh.juliendhyne.projetandroid.DB.UserAccessBDD;
import be.heh.juliendhyne.projetandroid.utils.PasswordSecurity;
import be.heh.juliendhyne.projetandroid.utils.TextValidator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    Button registerButton;

    EditText ET_firstname;
    EditText ET_lastname;
    EditText ET_mail;
    EditText ET_password;
    boolean validFirstName;
    boolean validLastName;
    boolean validMail;
    boolean validPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.btn_Register);

        ET_firstname = findViewById(R.id.ET_firstname);
        ET_lastname = findViewById(R.id.ET_lastname);
        ET_mail = findViewById(R.id.ET_mail);
        ET_password = findViewById(R.id.ET_password);


        ET_firstname.addTextChangedListener(new TextValidator(ET_firstname) {
            @Override public void validate(TextView textView, String text) {
                checkIfTextFirstname(ET_firstname.getText().toString().trim());
            }
        });

        ET_lastname.addTextChangedListener(new TextValidator(ET_lastname) {
            @Override public void validate(TextView textView, String text) {
                checkIfTextLastname(ET_lastname.getText().toString().trim());
            }
        });

        ET_mail.addTextChangedListener(new TextValidator(ET_mail) {
            @Override public void validate(TextView textView, String text) {
                checkIfMail(ET_mail.getText().toString().trim());
            }
        });

        ET_password.addTextChangedListener(new TextValidator(ET_password) {
            @Override public void validate(TextView textView, String text) {
                checkIfSpecialChar(ET_password.getText().toString().trim());
            }
        });

    }

    public void onRegisterClickManager(View v) {
        switch (v.getId()){

            case R.id.btn_Register:
                PasswordSecurity password = new PasswordSecurity(ET_password.getText().toString());
                if(isTheFormValid()) {
                    UserAccessBDD userDB = new UserAccessBDD(this);
                    userDB.openForRead();
                    Log.i("BOO", ET_mail.getText().toString());
                    if(!userDB.IsMyMailUsed(ET_mail.getText().toString())) {
                        Log.i("Caput Draconis", ET_password.getText().toString());
                        User user1 = new User(ET_firstname.getText().toString().trim(),
                                ET_lastname.getText().toString().trim(),
                                ET_mail.getText().toString().trim(),
                                password.getPasswordSecured(),
                                1);
                        userDB.openForWrite();
                        userDB.insertUser(user1);
                        userDB.Close();
                        Toast.makeText(this, "Inscription réussie!", Toast.LENGTH_SHORT).show();
                        Intent intentMain = new Intent(this, MainActivity.class);
                        startActivity(intentMain);
                    }
                    else {
                        Toast.makeText(this, "L'adresse mail est déja utilisée", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(this, "Les champs d'inscription contiennent des erreurs", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean isTheFormValid() {
        return validFirstName && validLastName && validMail && validPassword;
    }

    private void checkIfTextFirstname(String text) {
        char[] chars = text.toCharArray();
        validFirstName = (ET_firstname.getText().toString().trim().length() < 15) ? true : false;
        if (text == null)
            validFirstName =  false;
        for (char c : chars) {
            if(!Character.isLetter(c)) {
                validFirstName =  false;
            }
        }
        if(!validFirstName){ET_firstname.setError("Ce champ ne doit contenir que des lettres et faire 15 caractères maximum");}
    }

    private void checkIfTextLastname(String text) {
        char[] chars = text.toCharArray();
        if (text == null)
            validLastName =  false;
        validLastName = (ET_lastname.getText().toString().trim().length() < 15) ? true : false;
        for (char c : chars) {
            if(!Character.isLetter(c)) {
                validLastName =  false;
            }
        }
        if(!validLastName){ET_lastname.setError("Ce champ ne doit contenir que des lettres et faire 15 caractères maximum");}
    }

    private void checkIfMail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            validMail =  false;
        validMail =  pat.matcher(email).matches();
        if(!validMail){ET_mail.setError("Ce champ doit contenir une adresse mail");}
    }

    private void checkIfSpecialChar(String password) {
        Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
        validPassword = regex.matcher(password).find() && (password.length() > 4);
        if(!validPassword){ET_password.setError("Ce champ doit contenir minimum 5 caractères dont un spécial");}
    }
}
