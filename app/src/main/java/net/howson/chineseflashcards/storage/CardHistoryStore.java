package net.howson.chineseflashcards.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.howson.chineseflashcards.BuildConfig;
import net.howson.chineseflashcards.spacedrep.FlashCard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CardHistoryStore extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "ChineseFlashcardHistory";
    private static final String TABLE = "cardhistory";
    private static final String KEY_DECK = "deck";
    private static final String KEY_FRONT = "front";
    private static final String KEY_CORRECT_COUNT = "correctCount";
    private static final String KEY_INCORRECT_COUNT = "incorrectCount";

    public CardHistoryStore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) {
            Log.i(CardHistoryStore.class.getName(), "creating database");
        }
        String CREAT_TABLE = "CREATE TABLE " + TABLE + "("
                + KEY_DECK + " TEXT ," + KEY_FRONT + " TEXT,"
                + KEY_CORRECT_COUNT + " INTEGER,"
                + KEY_INCORRECT_COUNT + " INTEGER,"
                + " PRIMARY KEY (" + KEY_DECK +", " + KEY_FRONT +") )";
        db.execSQL(CREAT_TABLE);
        if (BuildConfig.DEBUG) {
            Log.i(CardHistoryStore.class.getName(), "database created OK");
        }
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG) {
            Log.i(CardHistoryStore.class.getName(), "upgrading database");
        }
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);

        // Create tables again
        onCreate(db);
    }


    public void loadCounts(String deckName, List<FlashCard> cards) {


        Map<String, FlashCard> cardIndex = new HashMap<>();

        for (Iterator<FlashCard>  it = cards.iterator(); it.hasNext(); ) {
            FlashCard card = it.next();
            cardIndex.put(card.front, card);
        }

//
//        private static final String KEY_DECK = "deck";
//        private static final String KEY_FRONT = "front";
//        private static final String KEY_CORRECT_COUNT = "correctCount";
//        private static final String KEY_INCORRECT_COUNT = "incorrectCount";
        String selectQuery = "SELECT  " +  KEY_FRONT +", " + KEY_CORRECT_COUNT +", " + KEY_INCORRECT_COUNT + " FROM " + TABLE +" WHERE "+KEY_DECK + " =?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =db.query(TABLE, new String[] { KEY_FRONT,
                        KEY_CORRECT_COUNT, KEY_INCORRECT_COUNT }, KEY_DECK + "=?",
                new String[] { deckName }, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String front = cursor.getString(0);

                FlashCard card = cardIndex.get(front);
                if (card!=null) {

                    if (BuildConfig.DEBUG) {
                        Log.i(CardHistoryStore.class.getName(), "Updating card from DB counters " + front);
                    }

                    card.numTimesCorrect = cursor.getInt(1);
                    card.numTimesIncorrect = cursor.getInt(2);

                    if (BuildConfig.DEBUG) {
                        Log.i(CardHistoryStore.class.getName(), card.toString());
                    }

                }


            } while (cursor.moveToNext());
        }

    }


    public void addHistory(String deckName, FlashCard card) {
        try (SQLiteDatabase db = this.getWritableDatabase();) {
            ContentValues values = new ContentValues();
            values.put(KEY_DECK, deckName);
            values.put(KEY_FRONT, card.front);
            values.put(KEY_CORRECT_COUNT, card.numTimesCorrect);
            values.put(KEY_INCORRECT_COUNT, card.numTimesIncorrect);

            if (BuildConfig.DEBUG) {
                Log.i(CardHistoryStore.class.getName(), "Doing row insert");
            }
            db.insert(TABLE, null, values);

            if (BuildConfig.DEBUG) {
                Log.i(CardHistoryStore.class.getName(), "Row insert done");
            }
            //2nd argument is String containing nullColumnHack
        } catch (Exception e) {
            Log.e(CardHistoryStore.class.getName(), "Database persistence failed: ", e);
        }
    }


    public void updateCardCounts(String deckname, FlashCard card) {
        SQLiteDatabase db = this.getReadableDatabase();

        //        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DECK, deckname);
        values.put(KEY_FRONT, card.front);
        values.put(KEY_CORRECT_COUNT, card.numTimesCorrect);
        values.put(KEY_INCORRECT_COUNT, card.numTimesIncorrect);

        // updating row
        int updated = db.update(TABLE, values, KEY_DECK + " = ? AND " + KEY_FRONT + "=?",
                new String[]{deckname, card.front});


        if (BuildConfig.DEBUG) {
            Log.i(CardHistoryStore.class.getName(), "Result of update : " + updated);
        }
        if (updated == 0) {
            addHistory(deckname, card);
        }

        // return contact

    }

//    // code to add the new contact
//    void addContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName()); // Contact Name
//        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone
//
//        // Inserting Row
//        db.insert(TABLE_CONTACTS, null, values);
//        //2nd argument is String containing nullColumnHack
//        db.close(); // Closing database connection
//    }
//
//    // code to get the single contact
//    Contact getContact(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
//                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1), cursor.getString(2));
//        // return contact
//        return contact;
//    }
//
//    // code to get all contacts in a list view
//    public List<Contact> getAllContacts() {
//        List<Contact> contactList = new ArrayList<Contact>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                Contact contact = new Contact();
//                contact.setID(Integer.parseInt(cursor.getString(0)));
//                contact.setName(cursor.getString(1));
//                contact.setPhoneNumber(cursor.getString(2));
//                // Adding contact to list
//                contactList.add(contact);
//            } while (cursor.moveToNext());
//        }
//
//        // return contact list
//        return contactList;
//    }
//
//    // code to update the single contact
//    public int updateContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName());
//        values.put(KEY_PH_NO, contact.getPhoneNumber());
//
//        // updating row
//        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//    }
//
//    // Deleting single contact
//    public void deleteContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//        db.close();
//    }
//
//    // Getting contacts Count
//    public int getContactsCount() {
//        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        // return count
//        return cursor.getCount();
//    }

}