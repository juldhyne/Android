package be.heh.juliendhyne.projetandroid.Liquids;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.juliendhyne.projetandroid.R;
import be.heh.juliendhyne.projetandroid.S7.S7;
import be.heh.juliendhyne.projetandroid.S7.S7Client;
import be.heh.juliendhyne.projetandroid.S7.S7OrderCode;

public class ReadTaskS7
{

    // Constantes pour gestion des messages Handler
    private static final int MESSAGE_PRE_EXECUTE = 1;
    public static final int MESSAGE_MODE_UPDATE = 2;
    public static final int MESSAGE_LIQUID_LEVEL_UPDATE = 3;
    public static final int MESSAGE_VALV1_UPDATE = 4;
    public static final int MESSAGE_VALV2_UPDATE = 5;
    public static final int MESSAGE_VALV3_UPDATE = 6;
    public static final int MESSAGE_VALV4_UPDATE = 7;
    public static final int MESSAGE_POST_EXECUTE = 8;

    //Etat de l'automate par défaut inactif
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    //Élément de l'activité Pills permettant la lecture
    private LinearLayout linearLayout;
    private TextView tv_liquid_mode;
    private TextView tv_liquid_plc;
    private TextView tv_liquid_lvl;
    private TextView tv_v1;
    private TextView tv_v2;
    private TextView tv_v3;
    private TextView tv_v4;

    //L'automate
    private AutomateS7 plcS7;
    //Le Thread de lecture
    private Thread readThread;

    //Objet S7 nécessaire à la connexion
    private S7Client comS7;
    //Tableaux contenant les paramètres permettant les échanges avec l'automate
    private String[] param = new String[10];
    private byte[] dataPLC = new byte[512];

    private byte[] modePLC = new byte[2];
    private byte[] LLPLC = new byte[2];
    private byte[] ValvesPLC = new byte[2];
    private byte[] ManuPLC = new byte[2];
    private byte[] AutoPLC = new byte[2];
    //Constructeur de la classe ReadTaskS7
    public ReadTaskS7(LinearLayout ll, TextView tv1, TextView tv2, TextView tv3, TextView tv_valv1, TextView tv_valv2, TextView tv_valv3, TextView tv_valv4)
    {
        linearLayout = ll;
        tv_liquid_mode = tv1;
        tv_liquid_plc = tv2;
        tv_liquid_lvl = tv3;
        tv_v1 = tv_valv1;
        tv_v2 = tv_valv2;
        tv_v3 = tv_valv3;
        tv_v4 = tv_valv4;

        comS7 = new S7Client();
        plcS7 = new AutomateS7();
        readThread = new Thread(plcS7);
    }

    //Méthode permettant de stopper l'automate et le thread
    public void Stop()
    {
        isRunning.set(false);
        comS7.Disconnect();
        readThread.interrupt();
    }

    //Méthode permettant de démarrer l'automate et le thread
    public void Start(String address, String rack, String slot)
    {
        if(!readThread.isAlive())
        {
            param[0] = address;
            param[1] = rack;
            param[2] = slot;

            readThread.start();
            isRunning.set(true);
        }
    }

    //Méthode appelées au démarrage du traitement. Affiche le PLC
    private void downloadOnPreExecute(int t)
    {
        Toast.makeText(tv_liquid_plc.getContext(), "le traitement de la tâche de fond est démarré", Toast.LENGTH_SHORT).show();
        tv_liquid_plc.setText("PLC: " + String.valueOf(t));
    }

    //Affichage de le mode d'utilisation de l'automate
    private void downloadOnModeUpdate(int t)
    {
        String mode = (t == 1) ? "Automatique" : "Manuel";
        tv_liquid_mode.setText(mode);
    }

    //Affiche le niveau de liquide de l'automate
    private void downloadOnLiquidLevelUpdate(int t)
    {
        tv_liquid_lvl.setText(String.valueOf(t));
        linearLayout.getBackground().setLevel(t/2);
        Log.i("LEVEL2", "" + linearLayout.getBackground().getLevel());
    }

    //Affiche l'état de la valve 1
    private void downloadOnValv1StateUpdate(int t)
    {
        tv_v1.setText("État V1: " + String.valueOf(t));
    }

    //Affiche l'état de la valve 2
    private void downloadOnValv2StateUpdate(int t)
    {
        tv_v2.setText("État V2: " + String.valueOf(t));
    }

    //Affiche l'état de la valve 3
    private void downloadOnValv3StateUpdate(int t)
    {
        tv_v3.setText("État V3: " + String.valueOf(t));
    }

    //Affiche l'état de la valve 4
    private void downloadOnValv4StateUpdate(int t)
    {
        tv_v4.setText("État V4: " + String.valueOf(t));
    }

    //Affiche un /!\ au PLC une fois le traitement terminé
    private void downloadOnPostExecute()
    {
        tv_liquid_plc.setText("PLC : /!\\ " );
    }

    //En fonction du message envoyé au Handler, va effectuer l'action requise
    private Handler myhandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case MESSAGE_PRE_EXECUTE:
                    downloadOnPreExecute(msg.arg1);
                    break;
                case MESSAGE_MODE_UPDATE:
                    downloadOnModeUpdate(msg.arg1);
                    break;
                case MESSAGE_LIQUID_LEVEL_UPDATE:
                    downloadOnLiquidLevelUpdate(msg.arg1);
                    break;
                case MESSAGE_VALV1_UPDATE:
                    downloadOnValv1StateUpdate(msg.arg1);
                    break;
                case MESSAGE_VALV2_UPDATE:
                    downloadOnValv2StateUpdate(msg.arg1);
                    break;
                case MESSAGE_VALV3_UPDATE:
                    downloadOnValv3StateUpdate(msg.arg1);
                    break;
                case MESSAGE_VALV4_UPDATE:
                    downloadOnValv4StateUpdate(msg.arg1);
                    break;
                case MESSAGE_POST_EXECUTE:
                    downloadOnPostExecute();
                    break;
                default:
                    break;
            }
        }
    };

    private class AutomateS7 implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                //Définit le type de connexion à l'automate
                comS7.SetConnectionType(S7.S7_BASIC);
                //Retourne 1 si la connexion est réussie
                Integer res = comS7.ConnectTo(param[0], Integer.valueOf(param[1]), Integer.valueOf(param[2]));
                S7OrderCode orderCode = new S7OrderCode();
                //comS7 est un object de type S7Client, retourne 1 si le traitement est réussi
                Integer result = comS7.GetOrderCode(orderCode);
                int numCPU = -1;
                //Récupérer le numéro de CPU
                if(res.equals(0) && result.equals(0))
                {
                    //Ici on récupère les caractères 5 à 8 du code envoyé par l'automate, ils correspondent au numéro de référence du CPU
                    numCPU = Integer.valueOf(orderCode.Code().toString().substring(5, 8));
                }
                else
                {
                    numCPU = 0000;
                }
                sendPreExecuteMessage(numCPU);
                while(isRunning.get())
                {
                    //Si Android est connecté à l'automate. QUESTION :: Pourquoi = à 0 Si plus haut on précise = à 1 Slide 165
                    if(res.equals(0))
                    {
                        //ReadArea = Zone Mémoire;Adresse du bloc de données;Emplacement variable;Nombre de variable à récupérer;Zone de stockage des données
                        //Lecture des infos sur le mode d'utilisation de l'automate (Automatique - Manuel)
                        int modeInfo = comS7.ReadArea(S7.S7AreaDB,5,18,2, modePLC);
                        //Lecture des infos sur le niveau de liquide
                        int LiquidLevelInfo = comS7.ReadArea(S7.S7AreaDB,5,16,2,LLPLC);
                        //Lecture des infos sur l'état des valves
                        int VInfo = comS7.ReadArea(S7.S7AreaDB,5,22,2,ValvesPLC);
                        int ManuInfo = comS7.ReadArea(S7.S7AreaDB,5,22,2,ManuPLC);
                        int AutoInfo = comS7.ReadArea(S7.S7AreaDB,5,22,2,AutoPLC);


                        //Ci dessous les données récupérées par la lecture(Information de type WORD stockée sur 16 bits)
                        int modedata;
                        int LLdata;
                        int V1data;
                        int V2data;
                        int V3data;
                        int V4data;


                        //Envoie des infos récupérées lors de la lecture aux méthodes d'affichage
                        if(ManuInfo == 0)
                        {
                            modedata = S7.GetWordAt(ManuPLC, 0 );
                            SendModeMessage(modedata);
                        }
                        if(LiquidLevelInfo == 0)
                        {
                            LLdata = S7.GetWordAt(LLPLC,0);
                            SendLLMessage(LLdata);
                        }
                        if(VInfo == 0)
                        {
                            int valve1, valve2, valve3, valve4;
                            valve1 = S7.GetBitAt(ValvesPLC, 0, 1) ? 1 : 0;
                            valve2 = S7.GetBitAt(ValvesPLC, 0, 2) ? 1 : 0;
                            valve3 = S7.GetBitAt(ValvesPLC, 0, 3) ? 1 : 0;
                            valve4 = S7.GetBitAt(ValvesPLC, 0, 4) ? 1 : 0;
                            SendV1Message(valve1);
                            SendV2Message(valve2);
                            SendV3Message(valve3);
                            SendV4Message(valve4);
                        }

                        if(AutoInfo == 0)
                        {
                            modedata = S7.GetWordAt(AutoPLC,2);
                            SendV2Message(modedata);
                        }

                    }
                    try
                    {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                sendPostExecuteMessage();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    //Envoi des messages au Handler
    private void sendPostExecuteMessage()
    {
        Message postExecuteMsg = new Message();
        postExecuteMsg.what = MESSAGE_POST_EXECUTE;
        myhandler.sendMessage(postExecuteMsg);
    }
    private void sendPreExecuteMessage(int v)
    {
        Message preExecuteMsg = new Message();
        preExecuteMsg.what = MESSAGE_PRE_EXECUTE;
        preExecuteMsg.arg1 = v;
        myhandler.sendMessage(preExecuteMsg);
    }
    private void SendModeMessage(int v)
    {
        Message ModeMessage = new Message();
        ModeMessage.what = MESSAGE_MODE_UPDATE;
        ModeMessage.arg1 = v;
        myhandler.sendMessage(ModeMessage);
    }
    private void SendLLMessage(int v)
    {
        Message LLMessage = new Message();
        LLMessage.what = MESSAGE_LIQUID_LEVEL_UPDATE;
        LLMessage.arg1 = v;
        myhandler.sendMessage(LLMessage);
    }
    private void SendV1Message(int v)
    {
        Message V1Message = new Message();
        V1Message.what = MESSAGE_VALV1_UPDATE;
        V1Message.arg1 = v;
        myhandler.sendMessage(V1Message);
    }
    private void SendV2Message(int v)
    {
        Message V2Message = new Message();
        V2Message.what = MESSAGE_VALV2_UPDATE;
        V2Message.arg1 = v;
        myhandler.sendMessage(V2Message);
    }
    private void SendV3Message(int v)
    {
        Message V3Message = new Message();
        V3Message.what = MESSAGE_VALV3_UPDATE;
        V3Message.arg1 = v;
        myhandler.sendMessage(V3Message);
    }
    private void SendV4Message(int v)
    {
        Message V4Message = new Message();
        V4Message.what = MESSAGE_VALV4_UPDATE;
        V4Message.arg1 = v;
        myhandler.sendMessage(V4Message);
    }
}

