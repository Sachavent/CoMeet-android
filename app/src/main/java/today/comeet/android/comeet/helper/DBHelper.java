package today.comeet.android.comeet.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Annick on 08/10/2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    // On définit le nom de la base de données

    public static final String DATABASE_NAME = "Evenement.db";
    public static final String TABLE_NAME = "evenement_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "DESCRIPTION";
    public static final String COL_4 = "LOCALISATION";
    public static final String COL_5 = "DATE";
    public static final String COL_6 = "LATTITUDE";
    public static final String COL_7 = "LONGITUDE";
    public static final String COL_8 = "AMIS";

    // version de la base de données
    public static final int DATABASE_VERSION = 1;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,DESCRIPTION TEXT,LOCALISATION TEXT, DATE TEXT, LATTITUDE INTEGER, LONGITUDE INTEGER, AMIS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

}
