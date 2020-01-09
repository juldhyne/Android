package be.heh.juliendhyne.projetandroid.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordSecurity {

    private static String PasswordSecured;

    public PasswordSecurity(String PasswordCleared) {

        String salt = "¨µfE§f@X*e" + PasswordCleared.length() + "@43¨Gnp*^";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(PasswordCleared.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            PasswordSecured = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();

        }

    }

    public static String getPasswordSecured() {
        return PasswordSecured;
    }

}
