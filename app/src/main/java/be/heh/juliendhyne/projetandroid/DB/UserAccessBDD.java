package be.heh.juliendhyne.projetandroid.DB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import be.heh.juliendhyne.projetandroid.utils.PasswordSecurity;

public class UserAccessBDD {

    private static final int VERSION = 1;
    public static final  String NAME_DB = "User.db";
    public static final String TABLE_USER = "table_user";
    public static final String COL_ID = "_id";
    public static final int NUM_COL_ID = 0;
    public static final String COL_FIRSTNAME = "FIRSTNAME";
    public static final int NUM_COL_FIRSTNAME = 1;
    public static final String COL_LASTNAME = "LASTNAME";
    public static final int NUM_COL_LASTNAME = 2;
    public static final String COL_EMAIL = "EMAIL";
    public static final int NUM_COL_EMAIL = 3;
    public static final String COL_PASSWORD = "PASSWORD";
    public static final int NUM_COL_PASSWORD = 4;
    public static final String COL_LEVEL = "LEVEL";
    public static final int NUM_COL_LEVEL = 5;

    private SQLiteDatabase db;
    private UserBddSqlite userdb;

    public UserAccessBDD(Context c) {
        userdb = new UserBddSqlite(c, NAME_DB, null, VERSION);
    }

    public void openForWrite() {
        db = userdb.getWritableDatabase();
    }

    public void openForRead() {
        db = userdb.getReadableDatabase();
    }

    public void Close() {
        db.close();
    }

    public long insertUser(User u) {
        //PasswordSecurity password = new PasswordSecurity(u.getPassword());
        ContentValues content = new ContentValues();
        content.put(COL_FIRSTNAME, u.getFirstname());
        content.put(COL_LASTNAME, u.getLastname());
        content.put(COL_EMAIL, u.getEmail());
        content.put(COL_PASSWORD, u.getPassword());
        content.put(COL_LEVEL, u.getLevel());

        return db.insert(TABLE_USER, null, content);
    }

    public int updateUser(int i, User u) {
        ContentValues content = new ContentValues();
        content.put(COL_FIRSTNAME, u.getFirstname());
        content.put(COL_LASTNAME, u.getLastname());
        content.put(COL_EMAIL, u.getEmail());
        content.put(COL_PASSWORD, u.getPassword());
        content.put(COL_LEVEL, u.getLevel());

        return db.update(TABLE_USER, content, COL_ID + " = " + i, null);
    }

    public int removeUser(String email) {
        return db.delete(TABLE_USER, COL_EMAIL + " = ?", new String[] {email});
    }

    public User getUserByMail(String mail) {
        Cursor c = db.query(TABLE_USER, new String[] {COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PASSWORD, COL_LEVEL},
                COL_EMAIL + " LIKE \"" + mail + "\"",
                null, null, null, COL_EMAIL);
        return cursorToUser(c);
    }

    public User cursorToUser(Cursor c) {
        if (c.getCount() == 0) {
            c.close();
            return null;
        }
        c.moveToFirst();
        User user1 = new User();
        user1.setId(c.getInt(NUM_COL_ID));
        user1.setFirstname(c.getString(NUM_COL_FIRSTNAME));
        user1.setLastname(c.getString(NUM_COL_LASTNAME));
        user1.setEmail(c.getString(NUM_COL_EMAIL));
        user1.setPassword(c.getString(NUM_COL_PASSWORD));
        user1.setLevel(c.getInt(NUM_COL_LEVEL));
        c.close();
        return user1;
    }


    public Cursor myCursor()
    {
        Cursor c = db.query(TABLE_USER, new String[] {
                COL_ID , COL_FIRSTNAME, COL_LASTNAME, COL_PASSWORD, COL_EMAIL, COL_LEVEL}, null, null, null, null, COL_ID);
        return c;
    }

    public ArrayList<User> getAllUser() {
        Cursor c = db.query(
                TABLE_USER,
                new String[] { COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PASSWORD, COL_LEVEL },
                null,
                null,
                null,
                null,
                COL_EMAIL);
        ArrayList<User> tabUser= new ArrayList<User> ();

        if (c.getCount() == 0) {
            c.close();
            return tabUser;
        }
        while(c.moveToNext()) {
            User user1 = new User();
            user1.setId(c.getInt(NUM_COL_ID));
            user1.setFirstname(c.getString(NUM_COL_FIRSTNAME));
            user1.setLastname(c.getString(NUM_COL_LASTNAME));
            user1.setEmail(c.getString(NUM_COL_EMAIL));
            user1.setPassword(c.getString(NUM_COL_PASSWORD));
            user1.setLevel(c.getInt(NUM_COL_LEVEL));
            tabUser.add(user1);
        }
        c.close();
        return tabUser;
    }

    public boolean IsMyMailUsed(String wantedMail) {
        boolean boo = false;
        Cursor c = db.query(
                TABLE_USER,
                new String[] { COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PASSWORD, COL_LEVEL },
                null,
                null,
                null,
                null,
                COL_EMAIL);
        while(c.moveToNext()) {
            if((c.getString(NUM_COL_EMAIL)).equals(wantedMail)){
                boo = true;
            }
        }
        c.close();
        return boo;
    }
}
