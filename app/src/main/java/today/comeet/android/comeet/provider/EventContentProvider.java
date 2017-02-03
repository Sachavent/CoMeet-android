package today.comeet.android.comeet.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import today.comeet.android.comeet.helper.DBHelper;

/**
 * Created by Mika on 10/11/16.
 */

public class EventContentProvider extends ContentProvider {
    //:::::::::::::::::::::::::://
//:: URI d'exposition
//:::::::::::::::::::::::::://

    public static final Uri CONTENT_URL = Uri.parse("content://today.comeet.android.comeet/elements");

    // Constantes pour identifier les requetes
    private static final int ALLROWS​ = 1;
    private static final int SINGLE_ROW​ = 2;
    // Uri matcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher((UriMatcher.NO_MATCH));
        uriMatcher.addURI("today.comeet.android.comeet", "elements", ALLROWS​);
        uriMatcher.addURI("today.comeet.android.comeet", "elements/#", SINGLE_ROW​);
    }

    private DBHelper myDBHelper;

    @Override
    public boolean onCreate() {
        myDBHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Open the Database
        SQLiteDatabase db = myDBHelper.getWritableDatabase();

        //parametres de la requete SQL
        String groupBy = null;
        String having = null;

        //construction de la requete
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        //ajout de la table
        queryBuilder.setTables(DBHelper.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW​:
                // Permet de récupérer l'id de la colonne qu'on souhaite récupérer
                String rowid = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(myDBHelper.COL_1 + "=" + rowid);
            default: break;
        }
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, having, sortOrder);

        //Log.d("cursor", "test cursor :" + cursor);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALLROWS​: return
                    "vdn.android.cursor.dir/vnd.paad.elemental";
            case SINGLE_ROW​:
                return "vdn.android.cursor.item/vnd.paad.elemental";
            default:
                throw new IllegalArgumentException("URI non reconnue");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = myDBHelper.getWritableDatabase();

        String nullColumnHack=null;

        long id = db.insert(DBHelper.TABLE_NAME, nullColumnHack, values);
        //si valeurs inseré
        Log.d("creation","provider :"+id);
        if (id > 1) {
            // construit l'uri de la ligne crée
            Uri insertedId= ContentUris.withAppendedId(CONTENT_URL, id);
            // Notifie le changement de données
            getContext().getContentResolver().notifyChange(insertedId,null);
            return insertedId;
        }
        return null;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
