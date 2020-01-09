package be.heh.juliendhyne.projetandroid.Liquids;

import androidx.appcompat.app.AppCompatActivity;
import be.heh.juliendhyne.projetandroid.HelpActivity;
import be.heh.juliendhyne.projetandroid.Liquids.ReadTaskS7;
import be.heh.juliendhyne.projetandroid.Liquids.WriteTaskS7;
import be.heh.juliendhyne.projetandroid.R;
import be.heh.juliendhyne.projetandroid.utils.SessionManager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import java.util.HashMap;

public class LiquidActivity extends AppCompatActivity {

    TextView tv_liquid_plc;
    TextView tv_liquid_lvl;
    TextView tv_liquid_mode;
    ToggleButton tb_liquid_connexion;

    TextView tv_liquid_v1;
    TextView tv_liquid_v2;
    TextView tv_liquid_v3;
    TextView tv_liquid_v4;

    LinearLayout ll_liquid;


    ImageButton btn_help;

    EditText et_automaton_binary;
    EditText et_automaton_DBbinary;
    ImageButton btn_confirm_binary;
    EditText et_automaton_integer;
    EditText et_automaton_DBinteger;
    ImageButton btn_confirm_integer;

    SessionManager session;
    private ReadTaskS7 readS7;
    private WriteTaskS7 writeS7;
    private NetworkInfo network;
    private ConnectivityManager connexStatus;

    public String ip, rack, slot;
    String userLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquid);

        tv_liquid_plc = (TextView) findViewById(R.id.tv_liquid_plc);
        tv_liquid_lvl = (TextView) findViewById(R.id.tv_liquid_lvl);
        tv_liquid_mode = (TextView) findViewById(R.id.tv_liquid_mode);
        tb_liquid_connexion = (ToggleButton) findViewById(R.id.tb_liquid_connexion);

        tv_liquid_v1 = (TextView) findViewById(R.id.tv_liquid_v1);
        tv_liquid_v2 = (TextView) findViewById(R.id.tv_liquid_v2);
        tv_liquid_v3 = (TextView) findViewById(R.id.tv_liquid_v3);
        tv_liquid_v4 = (TextView) findViewById(R.id.tv_liquid_v4);
        btn_help = findViewById(R.id.btn_liquids_help);

        et_automaton_binary = findViewById(R.id.et_automaton_binary);
        et_automaton_DBbinary = findViewById(R.id.et_automaton_DBbinary);
        btn_confirm_binary = findViewById(R.id.btn_confirm_binary);
        et_automaton_integer = findViewById(R.id.et_automaton_integer);
        et_automaton_DBinteger = findViewById(R.id.et_automaton_DBinteger);
        btn_confirm_integer = findViewById(R.id.btn_confirm_integer);


        ll_liquid = (LinearLayout) findViewById(R.id.ll_liquid);


        session = new SessionManager(getApplicationContext());
        connexStatus = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        HashMap<String, String> user = session.getUserDetails();
        HashMap<String, String> config = session.getConfigDetails();
        userLevel = user.get("sessionLevel");
        ip = config.get("configIP");
        rack = config.get("configRACK");
        slot = config.get("configSLOT");



        LinearLayout automaton_write = (LinearLayout) findViewById(R.id.ll_automaton_management);

        if(Integer.parseInt(userLevel) != 2)
        {
            automaton_write.setVisibility(View.GONE);
            btn_help.setVisibility(View.GONE);
        }

    }

    public void onLiquidClickManager(View V) {
        switch (V.getId()) {
            case R.id.tb_liquid_connexion:
                Log.i("LEVEL","" + V.getBackground().getLevel());

                connexStatus = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connexStatus != null) {
                    network = connexStatus.getActiveNetworkInfo();
                }
                if(network != null && network.isConnectedOrConnecting())
                {
                    if(!tb_liquid_connexion.isChecked())//Si on appuie sur le bouton connexion, on lance le readtask
                    {
                        Toast.makeText(this, network.getTypeName(),Toast.LENGTH_SHORT).show();

                        readS7 = new ReadTaskS7(ll_liquid, tv_liquid_mode, tv_liquid_plc, tv_liquid_lvl, tv_liquid_v1, tv_liquid_v2, tv_liquid_v3, tv_liquid_v4);
                        readS7.Start(ip, rack, slot);

                        if(Integer.parseInt(userLevel) == 2)//Si on est un super utilisateur qui voit le bouton écriture
                        {
                            writeS7 = new WriteTaskS7();
                            writeS7.Start(ip, rack, slot);
                        }
                    }
                    else//Si on appuie sur le bouton déconnexion on le stoppe
                    {
                        readS7.Stop();
                        ll_liquid.getBackground().setLevel(0);
                        if(Integer.parseInt(userLevel) == 2)//Si on est un super utilisateur qui voit le bouton écriture
                        {
                            writeS7.Stop();
                        }
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_liquids_help:
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
                    writeS7.setWriteInt(Integer.parseInt(et_automaton_DBinteger.getText().toString()), et_automaton_integer.getText().toString());
                }
                break;
        }
    }
}
