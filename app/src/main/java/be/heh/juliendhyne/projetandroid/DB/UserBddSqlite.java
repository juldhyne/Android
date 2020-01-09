package be.heh.juliendhyne.projetandroid.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserBddSqlite extends SQLiteOpenHelper {

    private static final String TABLE_USER = "table_user";
    private static final String COL_ID = "_id";
    private static final String COL_FIRSTNAME = "FIRSTNAME";
    private static final String COL_LASTNAME = "LASTNAME";
    private static final String COL_EMAIL = "EMAIL";
    private static final String COL_PASSWORD = "PASSWORD";
    private static final String COL_LEVEL = "LEVEL";

    private static final String CREATE_DB = "CREATE TABLE "
            + TABLE_USER
            + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FIRSTNAME + " TEXT NOT NULL, "
            + COL_LASTNAME + " TEXT NOT NULL, "
            + COL_EMAIL + " TEXT NOT NULL, "
            + COL_PASSWORD + " TEXT NOT NULL,"
            + COL_LEVEL + " INTEGER);";

    private static final String CREATE_ADMIN = "INSERT INTO "
            + TABLE_USER +
            " (" + COL_FIRSTNAME + ", " +
            COL_LASTNAME + ", " + COL_EMAIL + ", " +
            COL_PASSWORD + ", " + COL_LEVEL + ") VALUES (" +
            "'Julien', 'Dhyne'," +
            "'admin@gmail.com'," +
            "'2779088bbfdb005f40b9f0b630bfd787c5a76d286137a376cbed0df9c668307a43635b3e55e7b8877b54babd3bccc5d04629ea7a99ff23a8e44396ea7388bd5d', '2');";

    public UserBddSqlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super (context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
        db.execSQL(CREATE_ADMIN);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Dans cette méthode, vous devez gérer les révisions de version de votre base de données
        db.execSQL("DROP TABLE " + TABLE_USER); onCreate(db);
    }


    }
