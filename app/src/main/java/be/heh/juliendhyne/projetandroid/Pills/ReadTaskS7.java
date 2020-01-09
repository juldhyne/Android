package be.heh.juliendhyne.projetandroid.Pills;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.juliendhyne.projetandroid.S7.S7;
import be.heh.juliendhyne.projetandroid.S7.S7Client;
import be.heh.juliendhyne.projetandroid.S7.S7OrderCode;

public class ReadTaskS7 {

    // Constantes pour gestion des messages Handler
    public static final int MESSAGE_PRE_EXECUTE = 1;
    public static final int MESSAGE_STATUS_UPDATE = 2;
    public static final int MESSAGE_BOTTLE_COMING_UPDATE = 3;
    public static final int MESSAGE_PILLS_ASKED_UPDATE = 4;
    public static final int MESSAGE_STORED_PILLS_UPDATE = 5;
    public static final int MESSAGE_STORED_BOTTLES_UPDATE = 6;
    public static final int MESSAGE_POST_EXECUTE = 7;

    //Etat de l'automate par défaut inactif
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    //Élément de l'activité Pills permettant la lecture
    private View vi_main_ui;
    private TextView tv_pills_status; //OK
    private TextView tv_pills_plc; //OK
    private TextView tv_pills_botlles_coming; //OK
    private TextView tv_pills_pills_asked; //OK
    private TextView tv_pills_stored_pills; //OK
    private TextView tv_pills_stored_bottles; //OK

    //L'automate
    private AutomateS7 plcS7;
    //Le Thread de lecture
    private Thread readThread;

    //Objet S7 nécessaire à la connexion
    private S7Client comS7;
    //Tableaux contenant les paramètres permettant les échanges avec l'automate
    private String[] param = new String[10];
    private byte[] dataPLC = new byte[512];
    private byte[] statusPLC = new byte[2];
    private byte[] BCPLC = new byte[2];
    private byte[] WPPLC = new byte[2];
    private byte[] PillsPLC = new byte[2];
    private byte[] BottlesPLC = new byte[2];


    //Constructeur de la classe ReadTaskS7
    public ReadTaskS7(View v, TextView tv1, TextView tv2, TextView tv3, TextView tv4, TextView tv5, TextView tv6)
    {
        vi_main_ui = v;
        tv_pills_status = tv1;
        tv_pills_plc = tv2;
        tv_pills_botlles_coming = tv3;
        tv_pills_pills_asked = tv4;
        tv_pills_stored_pills = tv5;
        tv_pills_stored_bottles = tv6;

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
        Toast.makeText(tv_pills_plc.getContext(), "le traitement de la tâche de fond est démarré", Toast.LENGTH_SHORT).show();
        tv_pills_plc.setText("PLC: " + String.valueOf(t));
    }

    //Affichage de l'état de l'automate
    private void downloadOnStatusUpdate(int t)
    {
        String status = (t == 1) ? "Non connecté" : "Connecté";
        tv_pills_status.setText(status);
    }

    //Affiche si les bouteilles circulent
    private void downloadOnBottleComingUpdate(int t)
    {
        String areComing = (t == 1) ? "Oui" : "Non";
        tv_pills_botlles_coming.setText("Arrivée des bouteilles: " + areComing);
    }

    //Affiche le nombre de comprimés demandés
    private void downloadOnAskedPillsUpdate(int t)
    {
        tv_pills_pills_asked.setText("Nombre de comprimés demandés: " + String.valueOf(t));
    }

    //Affiche le nombre de comprimés stockés
    private void downloadOnStoredPillsUpdate(int t)
    {
        tv_pills_stored_pills.setText("Nombre de comprimés stockés: " + String.valueOf(t));
    }

    //Affiche le nombre de bouteilles
    private void downloadOnStoredBottlesUpdate(int t)
    {
        tv_pills_stored_bottles.setText("Nombre de bouteilles stockées: " + String.valueOf(t));
    }

    //Affiche un /!\ au PLC une fois le traitement terminé
    private void downloadOnPostExecute()
    {
        tv_pills_plc.setText("PLC : /!\\ " );
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
                case MESSAGE_STATUS_UPDATE:
                    downloadOnStatusUpdate(msg.arg1);
                    break;
                case MESSAGE_BOTTLE_COMING_UPDATE:
                    downloadOnBottleComingUpdate(msg.arg1);
                    break;
                case MESSAGE_PILLS_ASKED_UPDATE:
                    downloadOnAskedPillsUpdate(msg.arg1);
                    break;
                case MESSAGE_STORED_PILLS_UPDATE:
                    downloadOnStoredPillsUpdate(msg.arg1);
                    break;
                case MESSAGE_STORED_BOTTLES_UPDATE:
                    downloadOnStoredBottlesUpdate(msg.arg1);
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
                int numCPU;
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
                        //Lecture des infos de status de l'automate (ON-OFF)
                        int statusInfo = comS7.ReadArea(S7.S7AreaDB,5,0,2, statusPLC);
                        //Lecture des infos de circulation des bouteilles (OUI-NON)
                        int BottlesComingInfo = comS7.ReadArea(S7.S7AreaDB,5,1,2,BCPLC);
                        //Lecture du nombre de comprimés par bouteille demandé
                        int WantedPillsInfo = comS7.ReadArea(S7.S7AreaDB,5,4,2,WPPLC);
                        //Lecture du nombre total de comprimés
                        int PillsInfo = comS7.ReadArea(S7.S7AreaDB,5,15,2,PillsPLC);
                        //Lecture du nombre total de bouteille
                        int BottlesInfo = comS7.ReadArea(S7.S7AreaDB,5,16,2,BottlesPLC);

                        //Ci dessous les données récupérées par la lecture(Information de type WORD stockée sur 16 bits)
                        int statusdata;
                        int BCdata;
                        int PAdata;
                        int SPdata;
                        int SBdata;

                        boolean[] wantedPillsArray = {false, false, false};

                        //Envoie des infos récupérées lors de la lecture aux méthodes d'affichage
                        if(statusInfo == 0)
                        {
                            statusdata = S7.GetBitAt(statusPLC, 0, 0) ? 1 : 0;
                            SendStatusMessage(statusdata);
                        }
                        if(BottlesComingInfo == 0)
                        {
                            BCdata = S7.GetBitAt(BCPLC, 0, 3) ? 1 : 0;
                            SendBCMessage(BCdata);
                        }
                        if(WantedPillsInfo == 0)
                        {
                            wantedPillsArray[0] = S7.GetBitAt(WPPLC, 0, 3);
                            wantedPillsArray[1] = S7.GetBitAt(WPPLC, 0, 4);
                            wantedPillsArray[2] = S7.GetBitAt(WPPLC, 0, 5);
                            PAdata = 0;
                            if(wantedPillsArray[0]){PAdata = 5;}
                            if(wantedPillsArray[1]){PAdata = 10;}
                            if(wantedPillsArray[2]){PAdata = 15;}


                            SendPAMessage(PAdata);
                        }
                        if(PillsInfo == 0)
                        {
                            SPdata = S7.BCDtoByte(PillsPLC[0]);
                            SendSPMessage(SPdata);
                        }
                        if(BottlesInfo == 0)
                        {
                            SBdata = S7.GetWordAt(BottlesPLC,0);
                            SendSBMessage(SBdata);
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
    private void SendStatusMessage(int v)
    {
        Message statusMessage = new Message();
        statusMessage.what = MESSAGE_STATUS_UPDATE;
        statusMessage.arg1 = v;
        myhandler.sendMessage(statusMessage);
    }
    private void SendBCMessage(int v)
    {
        Message BCMessage = new Message();
        BCMessage.what = MESSAGE_BOTTLE_COMING_UPDATE;
        BCMessage.arg1 = v;
        myhandler.sendMessage(BCMessage);
    }
    private void SendPAMessage(int v)
    {
        Message PAMessage = new Message();
        PAMessage.what = MESSAGE_PILLS_ASKED_UPDATE;
        PAMessage.arg1 = v;
        myhandler.sendMessage(PAMessage);
    }
    private void SendSPMessage(int v)
    {
        Message SPMessage = new Message();
        SPMessage.what = MESSAGE_STORED_PILLS_UPDATE;
        SPMessage.arg1 = v;
        myhandler.sendMessage(SPMessage);
    }
    private void SendSBMessage(int v)
    {
        Message SBMessage = new Message();
        SBMessage.what = MESSAGE_STORED_BOTTLES_UPDATE;
        SBMessage.arg1 = v;
        myhandler.sendMessage(SBMessage);
    }
}

