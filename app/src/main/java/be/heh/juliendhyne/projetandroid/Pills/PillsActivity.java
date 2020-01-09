package be.heh.juliendhyne.projetandroid.Pills;

import androidx.appcompat.app.AppCompatActivity;
import be.heh.juliendhyne.projetandroid.HelpActivity;
import be.heh.juliendhyne.projetandroid.HomeActivity;
import be.heh.juliendhyne.projetandroid.R;
import be.heh.juliendhyne.projetandroid.utils.SessionManager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class PillsActivity extends AppCompatActivity {

    LinearLayout ll_pills_management;

    TextView tv_pills_status;
    TextView tv_pills_plc;
    TextView tv_pills_bottles_coming;
    TextView tv_pills_pills_asked;
    TextView tv_pills_stored_pills;
    TextView tv_pills_stored_bottles;



    ImageButton btn_pills_connexion;
    ImageButton btn_help;

    EditText et_automaton_binary;
    EditText et_automaton_DBbinary;
    ImageButton btn_confirm_binary;
    EditText et_automaton_integer;
    EditText et_automaton_DBinteger;
    ImageButton btn_confirm_integer;

    private SessionManager session;

    private ReadTaskS7 readS7;
    private WriteTaskS7 writeS7;
    private NetworkInfo network;
    private ConnectivityManager connexStatus;

    public String ip, rack, slot;
    String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pills);

        ll_pills_management = findViewById(R.id.ll_automaton_management);

        tv_pills_status = findViewById(R.id.tv_pills_status);
        tv_pills_plc = findViewById(R.id.tv_pills_plc);
        tv_pills_bottles_coming = findViewById(R.id.tv_pills_bottles_coming);
        tv_pills_pills_asked = findViewById(R.id.tv_pills_pills_asked);
        tv_pills_stored_pills = findViewById(R.id.tv_pills_stored_pills);
        tv_pills_stored_bottles = findViewById(R.id.tv_pills_stored_bottles);


        btn_pills_connexion = findViewById(R.id.btn_pills_connexion);
        btn_help = findViewById(R.id.btn_pills_help);

        et_automaton_binary = findViewById(R.id.et_automaton_binary);
        et_automaton_DBbinary = findViewById(R.id.et_automaton_DBbinary);
        btn_confirm_binary = findViewById(R.id.btn_confirm_binary);
        et_automaton_integer = findViewById(R.id.et_automaton_integer);
        et_automaton_DBinteger = findViewById(R.id.et_automaton_DBinteger);
        btn_confirm_integer = findViewById(R.id.btn_confirm_integer);

        session = new SessionManager(getApplicationContext());
        connexStatus = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        HashMap<String, String> user = session.getUserDetails();
        HashMap<String, String> config = session.getConfigDetails();
        level = user.get("sessionLevel");
        ip = config.get("configIP");
        rack = config.get("configRACK");
        slot = config.get("configSLOT");

        LinearLayout automaton_write = (LinearLayout) findViewById(R.id.ll_automaton_management);

        if(Integer.parseInt(level) != 2)
        {
            automaton_write.setVisibility(View.GONE);
            btn_help.setVisibility(View.GONE);
        }
    }

    public void onPillsClickManager(View V)
    {
        switch (V.getId())
        {
            case R.id.btn_pills_connexion://BOUTON LECTURE
                connexStatus = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connexStatus != null) {
                    network = connexStatus.getActiveNetworkInfo();
                }
                if(network != null && network.isConnectedOrConnecting())
                {
                    if(tv_pills_status.getText().toString().equals("OFF"))//Si on appuie sur le bouton connexion, on lance le readtask
                    {
                        Toast.makeText(this, network.getTypeName(),Toast.LENGTH_SHORT).show();
                        tv_pills_status.setText("ON");
                        tv_pills_status.setTextColor(getResources().getColor(R.color.GREEN));


                        readS7 = new ReadTaskS7(V, tv_pills_status, tv_pills_plc, tv_pills_bottles_coming, tv_pills_pills_asked, tv_pills_stored_pills, tv_pills_stored_bottles);
                        readS7.Start(ip, rack, slot);

                        if(Integer.parseInt(level) == 2)//Si on est un super utilisateur
                        {
                            writeS7 = new WriteTaskS7();
                            writeS7.Start(ip, rack, slot);
                        }
                    }
                    else//Si on appuie sur le bouton déconnexion on le stoppe
                    {
                        readS7.Stop();
                        tv_pills_status.setText("OFF");
                        tv_pills_status.setTextColor(getResources().getColor(R.color.RED));
                            writeS7.Stop();

                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_pills_help:
                Intent intentHelp = new Intent(this, HelpActivity.class);
                startActivity(intentHelp);
                break;


        }
    }

    public void onAutomatonClickManager(View V) {
        switch (V.getId()) {
            case R.id.btn_confirm_binary:
                if(et_automaton_binary.getText().toString().isEmpty() || et_automaton_DBbinary.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Entrez des valeurs dans les champs adéquats", Toast.LENGTH_SHORT).show();
                }
                else {
                    writeS7.setWriteBool(Integer.parseInt(et_automaton_DBbinary.getText().toString()), et_automaton_integer.getText().toString());
                }
                break;
            case R.id.btn_confirm_integer:
                if(et_automaton_integer.getText().toString().isEmpty() || et_automaton_DBinteger.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Entrez des valeurs dans les champs adéquats", Toast.LENGTH_SHORT).show();
                }
                else {
                    writeS7.setWriteInt(et_automaton_integer.getText().toString());
                }
                break;
        }
    }

}
