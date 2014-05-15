package com.ehsan.hadithbd;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBMS extends SQLiteOpenHelper {

    Activity activity;
    Context _context;
    SQLiteDatabase app_db;

    Layout_Listener listenerInstPage;

    String DestinationFile;
    SQLiteDatabase theHadithHub;
    SQLiteDatabase.CursorFactory factory;
    Cursor databaseCursor;

    ArrayList<String> AllHadithId=new ArrayList<String>();



    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "userData";

    public DBMS(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        app_db = this.getReadableDatabase();
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db){
        //db.execSQL("CREATE TABLE IF NOT EXISTS 'users' ('row_id' INTEGER NOT NULL,'user_id' TEXT NOT NULL,'password' TEXT NOT NULL,'status' TEXT NOT NULL,PRIMARY KEY ('row_id') )");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        // onCreate(db);
    }

    void setup(Activity _activity, Context context)
    {
        this.activity = _activity;
        this._context=context;
    }

    void OpenDatabase()
    {
        DestinationFile = _context.getFilesDir().getPath() + File.separator + "db2.db";
        theHadithHub = SQLiteDatabase.openDatabase(DestinationFile, factory, SQLiteDatabase.OPEN_READONLY|SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    void setUpDatabase()
    {
        DestinationFile = _context.getFilesDir().getPath() + File.separator + "db2.db";

        if (!new File(DestinationFile).exists()) {
            try {
                CopyFromAssetsToStorage(_context, "db2.db", DestinationFile);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            listenerInstPage.onFired("copy_completed");
        }

        if (new File(_context.getFilesDir().getPath() + File.separator + "db.db").exists()){
            boolean deleted = new File(_context.getFilesDir().getPath() + File.separator + "db.db").delete();
            Log.i("Database", "Previous Database Deleted");
        };

    }

    ArrayList<BookDetails> GetBookList()
    {
        OpenDatabase();

        ArrayList<BookDetails> bl=new ArrayList<BookDetails>();

        try {
            Cursor cc = theHadithHub.query("hadithbook", new String[] {"BookID", "BookNameBD"}, null, null, null, null, null);
            if (cc.moveToFirst()) {
                do {

                    BookDetails bd=new BookDetails();
                    bd.id=cc.getString(0);
                    bd.bookName=cc.getString(1);
                    bd.thehadithnumber=getHadithBookMetaData("hadith_number", cc.getString(0));
                    bd.sectionnumber=getHadithBookMetaData("section_number", cc.getString(0));

                    bl.add(bd);
                } while (cc.moveToNext());
            }

        } finally {
            theHadithHub.close();
        }

        return bl;
    }


    int getNumberOfSection(String BookID)
    {
        int no=0;

        OpenDatabase();
        try {
            Cursor cc = theHadithHub.query("hadithsection", new String[] {"SectionID","SectionBD"}, "BookID='"+BookID+"'", null, null, null, null);
            if (cc.moveToFirst()) {
                no=cc.getCount();
            }

        } finally {
            theHadithHub.close();
        }

        return no;
    }

    int getNumberOfHadith(String SectionID)
    {
        int no=0;

        OpenDatabase();
        try {
            Cursor cc = theHadithHub.query("hadithmain", null, "SectionID='"+SectionID+"'", null, null, null, null);
            if (cc.moveToFirst()) {
                no=cc.getCount();
            }

        } finally {
            theHadithHub.close();
        }

        return no;
    }


    ArrayList<SectionDetails> GetSectionList(String BookID, int limit)
    {
        OpenDatabase();
       ArrayList<SectionDetails> bl=new ArrayList<SectionDetails>();

        try {
            String SQL="SELECT SectionID, SectionBD FROM hadithsection WHERE BookID = '"+BookID+"' AND SecActive=1 ORDER BY SectionID ASC LIMIT 25 OFFSET "+String.valueOf(limit);

            Cursor cc = theHadithHub.rawQuery(SQL, null);

            if (cc.moveToFirst()) {
                do {

                    SectionDetails bd=new SectionDetails();
                    bd.id=cc.getString(0);
                    bd.sectionName=cc.getString(1);
                    bd.thehadithnumber=getHadithSectionMetaData("hadith_number", cc.getString(0));
                    bl.add(bd);
                } while (cc.moveToNext());
            }

        } finally {
            theHadithHub.close();
        }

        return bl;
    }

    ArrayList<HadithDetails> GetHadithList(String SectionID,int limit)
    {
        OpenDatabase();
        ArrayList<HadithDetails> bl=new ArrayList<HadithDetails>();

        try {
            String SQL="SELECT HadithID, HadithNo, BanglaHadith FROM hadithmain WHERE SectionID = '"+SectionID+"' ORDER BY HadithID ASC LIMIT 25 OFFSET "+String.valueOf(limit);

            Cursor cc = theHadithHub.rawQuery(SQL, null);

            if (cc.moveToFirst()) {
                do {

                    HadithDetails bd=new HadithDetails();
                    bd.id=cc.getString(0);
                    bd.thehadithno=cc.getString(1);
                    bd.sectionID=SectionID;
                    bd.BanglaHadith=cc.getString(2);
                    bl.add(bd);
                } while (cc.moveToNext());

                Log.i("The Hadith List Number", ">" +cc.getCount());
                Log.i("The SQL", ">" +cc.getCount());
            }

            Log.i("The SQL", ">" +SQL);

        } finally {
            theHadithHub.close();
        }

        return bl;
    }

    HadithDetails GetHadithDetails(String hadithid)
    {
        OpenDatabase();
        HadithDetails bd=new HadithDetails();

        try {
            String SQL="SELECT BanglaHadith, ArabicHadith, EnglishHadith, SectionID, BookID FROM hadithmain WHERE HadithID = '"+hadithid+"'";

            Cursor cc = theHadithHub.rawQuery(SQL, null);

            if (cc.moveToFirst()) {
                do {
                    bd.id=hadithid;
                    bd.BanglaHadith=cc.getString(0);
                    bd.EnglishHadith=cc.getString(2);
                    bd.ArabicHadith=cc.getString(1);

                    Cursor nameOfSection = theHadithHub.rawQuery("SELECT SectionBD FROM hadithsection WHERE BookID = "+cc.getString(4)+" AND SectionID="+cc.getString(3), null);
                    if (nameOfSection.moveToFirst()) {
                        bd.sectionName=nameOfSection.getString(0);
                    }

                    Cursor nameOfBook = theHadithHub.rawQuery("SELECT BookNameBD FROM hadithbook WHERE BookID = "+cc.getString(4), null);
                    if (nameOfBook.moveToFirst()) {
                        bd.bookName=nameOfBook.getString(0);
                    }

                } while (cc.moveToNext());
            }

        } finally {
            theHadithHub.close();
        }

        return bd;
    }

    String getHadithBookMetaData(String mk, String bkid)
    {
        String md="";

        Cursor hadith_book_meta = theHadithHub.query("hadith_book_meta", new String[] {"meta_data"}, "meta_key='"+mk+"' AND book_id='"+bkid+"'", null, null, null, null);

        if (hadith_book_meta.moveToFirst()) {
            do {
                md=hadith_book_meta.getString(0);
            } while (hadith_book_meta.moveToNext());
        }

        return md;
    }

    String getHadithSectionMetaData(String mk, String secid)
    {
        String md="";

        Cursor hadith_book_meta = theHadithHub.query("hadith_section_meta", new String[] {"meta_data"}, "meta_key='"+mk+"' AND section_id='"+secid+"'", null, null, null, null);

        if (hadith_book_meta.moveToFirst()) {
            do {
                md=hadith_book_meta.getString(0);
            } while (hadith_book_meta.moveToNext());
        }

        return md;
    }

    void ArrayFYallHadithId(String Bookid)
    {
        OpenDatabase();
        try {
            String SQL="SELECT HadithID FROM hadithmain WHERE BookID="+Bookid+" ORDER BY BookID, SectionID, HadithNo, HadithID ASC";

            Cursor cc = theHadithHub.rawQuery(SQL, null);
            if (cc.moveToFirst()) {
                do {
                    AllHadithId.add(cc.getString(cc.getColumnIndex("HadithID")));
                   // Log.i("Found", "> "+cc.getString(cc.getColumnIndex("HadithID"))+"<  >"+AllHadithId.size());
                } while (cc.moveToNext());
            }
        } finally {
            theHadithHub.close();
        }
    }

    ArrayList<String> getAllHadithId()
    {
        return this.AllHadithId;
    }


    private void CopyFromAssetsToStorage(Context Context, String SourceFile, String DestinationFile) throws IOException {
        InputStream IS = Context.getAssets().open(SourceFile);
        OutputStream OS = new FileOutputStream(DestinationFile);
        CopyStream(IS, OS);
        OS.flush();
        OS.close();
        IS.close();
        listenerInstPage.onFired("copy_completed");
    }

    private void CopyStream(InputStream Input, OutputStream Output) throws IOException {
        byte[] buffer = new byte[5120];
        int length = Input.read(buffer);
        while (length > 0) {
            Output.write(buffer, 0, length);
            length = Input.read(buffer);
        }
    }

    public void setListener(Layout_Listener listener) {
        this.listenerInstPage = listener;
    }
}


class BookDetails{
    String id;
    String bookName="";
    String sectionnumber="";
    String thehadithnumber="";
}

class SectionDetails{
    String id;
    String sectionName="";
    String thehadithnumber="";
}

class HadithDetails{
    String id;
    String sectionID="";
    String sectionName="";
    String bookName="";

    String BanglaHadith="";
    String EnglishHadith="";
    String ArabicHadith="";
    String thehadithno="";
}