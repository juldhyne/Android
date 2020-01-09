package be.heh.juliendhyne.projetandroid.Pills;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.juliendhyne.projetandroid.S7.S7;
import be.heh.juliendhyne.projetandroid.S7.S7Client;

public class WriteTaskS7
{
    //Etat de l'automate par défaut inactif
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    //Thread d'écriture
    private Thread writeThread;
    //Classe interne implémentant Runnable et le Thread
    private AutomateS7 plcS7;
    //Objet S7 nécessaire à la connexion à l'API
    private S7Client comS7;
    //Tableaux contenant les paramètres de connexion et permettant les échanges avec l'automate
    private String[] parConnexion = new String[10];
    private byte[] dbb5 = new byte[2], dbb6 = new byte[2];
    private byte[] dbb7 = new byte[2], dbb8 = new byte[2], dbw18 = new byte[2];

    //Constructeur de la classe WriteTaskS7
    public WriteTaskS7()
    {
        comS7 = new S7Client();
        plcS7 = new AutomateS7();
        writeThread = new Thread(plcS7);
    }

    //Méthode permettant de stopper l'automate et le thread
    public void Stop()
    {
        isRunning.set(false);
        comS7.Disconnect();
        writeThread.interrupt();
    }

    //Méthode permettant de démarrer l'automate et le thread
    public void Start(String address, String rack, String slot)
    {
        if (!writeThread.isAlive())
        {
            parConnexion[0] = address;
            parConnexion[1] = rack;
            parConnexion[2] = slot;
            writeThread.start();
            isRunning.set(true);
        }
    }

    private class AutomateS7 implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                //Type de connexion
                comS7.SetConnectionType(S7.S7_BASIC);
                //Retourne 1 si connexion réussie
                Integer res = comS7.ConnectTo(parConnexion[0], Integer.valueOf(parConnexion[1]),Integer.valueOf(parConnexion[2]));
                while(isRunning.get() && (res.equals(0)))
                {
                    //WriteArea = ZoneMémoire;Adresse du bloc de données;Emplacement variable;Nombre de variable à transférer;Zone de stockage
                    comS7.WriteArea(S7.S7AreaDB, 26, 5, 2, dbb5);
                    comS7.WriteArea(S7.S7AreaDB, 26, 6, 2, dbb6);
                    comS7.WriteArea(S7.S7AreaDB, 26, 7, 2, dbb7);
                    comS7.WriteArea(S7.S7AreaDB, 26, 8, 2, dbb8);
                    comS7.WriteArea(S7.S7AreaDB, 26, 18, 2, dbw18);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setWriteBool(int dbb, String value) {
        char[] array = value.toCharArray();
        int len = array.length;
        byte[] chosenDBB;
        if (dbb == 5) chosenDBB = dbb5;
        else if (dbb == 6) chosenDBB = dbb6;
        else chosenDBB = dbb7;
        for (int i = 0; i < len; i++) {
            S7.SetBitAt(chosenDBB, 0, i, array[len-(i+1)] == '1' ? true : false);
        }
    }

    public void setWriteInt(String value) {
        S7.SetWordAt(dbw18, 0, Integer.parseInt(value));
    }
}
