package be.heh.juliendhyne.projetandroid;

import androidx.appcompat.app.AppCompatActivity;
import be.heh.juliendhyne.projetandroid.Liquids.LiquidActivity;
import be.heh.juliendhyne.projetandroid.Pills.PillsActivity;
import be.heh.juliendhyne.projetandroid.utils.PasswordSecurity;
import be.heh.juliendhyne.projetandroid.utils.SessionManager;
import be.heh.juliendhyne.projetandroid.utils.TextValidator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity {

    private SessionManager session;

    TextView tv_connectedas;
    ImageButton btn_comp;
    ImageButton btn_liquid;
    ImageButton btn_profile;
    ImageButton btn_web;
    EditText ET_ip;
    EditText ET_rack;
    EditText ET_slot;
    Button btn_saveconf;
    HashMap<String, String> config;
    HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new SessionManager(getApplicationContext());

        tv_connectedas = findViewById(R.id.tv_connectedas);
        btn_comp = findViewById(R.id.btn_pill_user);
        btn_liquid = findViewById(R.id.btn_liquid_user);
        btn_profile = findViewById(R.id.btn_profile_user);
        btn_web = findViewById(R.id.btn_web);
        ET_ip = findViewById(R.id.ET_ip);
        ET_rack = findViewById(R.id.ET_rack);
        ET_slot = findViewById(R.id.ET_slot);
        btn_saveconf = findViewById(R.id.btn_saveconf);

        user = session.getUserDetails();

        String st1 = tv_connectedas.getText().toString();
        String st2 = user.get("sessionFirstName") + user.get("sessionLastName");



        tv_connectedas.setText(st1 + " " + st2);

        try {
            config = session.getConfigDetails();
            ET_ip.setHint(config.get("configIP") != null ? config.get("configIP") : "Ip");
            ET_rack.setHint(config.get("configRACK") != null ? config.get("configRACK") : "Rack");
            ET_slot.setHint(config.get("configSLOT") != null ? config.get("configSLOT") : "Slot");
        }
        catch(Exception e){

        }

        if(Integer.parseInt(user.get("sessionLevel")) == 2) {
            btn_profile.setImageResource(R.drawable.user_list_icon);
        }


    }

    public void onHomeClickManager(View v) {
        switch (v.getId()){
            case R.id.btn_logout:
                session.deleteSession();
                Intent intentGoToMain = new Intent(this, MainActivity.class);
                startActivity(intentGoToMain);
                break;
            case R.id.btn_saveconf:
                session.createConfig(ET_ip.getText().toString(), ET_rack.getText().toString(), ET_slot.getText().toString());
                Toast.makeText(this,"Configuration enregistrée", Toast.LENGTH_SHORT).show();
                //TODO VERIFIER LA VALEUR DES CHAMPS
                break;
            case R.id.btn_pill_user:
                Intent intentGoToPills = new Intent(this, PillsActivity.class);
                startActivity(intentGoToPills);
                break;
            case R.id.btn_liquid_user:
                Intent intentGoToLiquids = new Intent(this, LiquidActivity.class);
                startActivity(intentGoToLiquids);
                break;
            case R.id.btn_profile_user:
                Intent intentGoToProfile = new Intent(this, ProfileActivity.class);
                Intent intentGoToList = new Intent(this, UserListActivity.class);
                startActivity(Integer.parseInt(user.get("sessionLevel")) == 2 ? intentGoToList : intentGoToProfile);
                break;
            case R.id.btn_web:
                String url = "http://";
                url = url + config.get("configIP");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }

}
