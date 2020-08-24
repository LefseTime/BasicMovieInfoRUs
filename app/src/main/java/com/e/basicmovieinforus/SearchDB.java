package com.e.basicmovieinforus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Collections;

public class SearchDB {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_SEARCH_TERM = "search_term";

    private static final String DATABASE_NAME = "SearchDB";
    private static final String DATABASE_TABLE = "SearchTable";
    private final int DATABASE_VERSION = 1;

    private DBHelper ourHelper;
    private final Context ourContext;
    private static SQLiteDatabase ourDatabase;

    public SearchDB (Context context){
        ourContext = context;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper (Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            String sqlCode = "CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ROWID +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_SEARCH_TERM + " TEXT NOT NULL);";

            db.execSQL(sqlCode);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public void open() throws SQLException {
        ourHelper = new DBHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
    }

    public void close(){
        ourHelper.close();
    }

    public static void createEntry(String searchTerm){
        ContentValues cv = new ContentValues();
        cv.put(KEY_SEARCH_TERM, searchTerm);
        ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public static ArrayList<String> getData(){
        String[] columns = new String[] {KEY_ROWID, KEY_SEARCH_TERM};

        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        String result = "";
        ArrayList<String> arrResult = new ArrayList<String>();

        int iRowID = c.getColumnIndex(KEY_ROWID);
        int iSearchTerm = c.getColumnIndex(KEY_SEARCH_TERM);

        String row;

        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            result = result + c.getString(iRowID) + ": " + c.getString(iSearchTerm) + "\n";

            row = c.getString(iSearchTerm);

            arrResult.add(row);
        }

        c.close();

        Collections.reverse(arrResult);

        return arrResult;
    }

    public long deleteEntry(String rowId){
        return ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=?", new String[]{rowId});
    }
}
