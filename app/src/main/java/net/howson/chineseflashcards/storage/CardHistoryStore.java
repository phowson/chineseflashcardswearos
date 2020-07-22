package net.howson.chineseflashcards.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.howson.chineseflashcards.BuildConfig;
import net.howson.chineseflashcards.spacedrep.FlashCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CardHistoryStore extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "ChineseFlashcardHistory";

    private static final String LEARNING_SET_TABLE = "learningset";


    private static final String HISTORY_TABLE = "cardhistory";
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
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + HISTORY_TABLE + "("
                + KEY_DECK + " TEXT ," + KEY_FRONT + " TEXT,"
                + KEY_CORRECT_COUNT + " INTEGER,"
                + KEY_INCORRECT_COUNT + " INTEGER,"
                + " PRIMARY KEY (" + KEY_DECK + ", " + KEY_FRONT + ") )";
        db.execSQL(CREATE_HISTORY_TABLE);

        String CREATE_LEARNING_SET = "CREATE TABLE " + LEARNING_SET_TABLE + "("
                + KEY_DECK + " TEXT ," + KEY_FRONT + " TEXT,"
                + " PRIMARY KEY (" + KEY_DECK + ", " + KEY_FRONT + ") )";
        db.execSQL(CREATE_LEARNING_SET);


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
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LEARNING_SET_TABLE);

        // Create tables again
        onCreate(db);
    }


    public void saveLearningSet(String deckName, List<FlashCard> cards) {
        try (SQLiteDatabase db = this.getWritableDatabase();) {
            db.delete(LEARNING_SET_TABLE, KEY_DECK + "=?",
                    new String[]{deckName});

            for (FlashCard card : cards) {

                ContentValues values = new ContentValues();
                values.put(KEY_DECK, deckName);
                values.put(KEY_FRONT, card.front);

                if (BuildConfig.DEBUG) {
                    Log.i(CardHistoryStore.class.getName(), "Doing row insert");
                }
                db.insert(LEARNING_SET_TABLE, null, values);

                if (BuildConfig.DEBUG) {
                    Log.i(CardHistoryStore.class.getName(), "Row insert done");
                }
                //2nd argument is String containing nullColumnHack

            }
        } catch (Exception e) {
            Log.e(CardHistoryStore.class.getName(), "Database persistence failed: ", e);
        }
    }


    public List<FlashCard> getLearningSet(String deckName, List<FlashCard> cards) {
        Map<String, FlashCard> lookup = new HashMap<>();

        for (FlashCard c : cards) {
            lookup.put(c.front, c);
        }


        String selectQuery = "SELECT  " + KEY_FRONT + " FROM " + LEARNING_SET_TABLE + " WHERE " + KEY_DECK + " =?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(HISTORY_TABLE, new String[]{KEY_FRONT,
                }, KEY_DECK + "=?",
                new String[]{deckName}, null, null, null, null);

        List<FlashCard> out = new ArrayList<>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String front = cursor.getString(0);
                FlashCard card = lookup.get(front);
                if (card != null) {
                    out.add(card);
                }

            } while (cursor.moveToNext());
        }
        return out;

    }


    public void loadCounts(String deckName, List<FlashCard> cards) {


        Map<String, FlashCard> cardIndex = new HashMap<>();

        for (Iterator<FlashCard> it = cards.iterator(); it.hasNext(); ) {
            FlashCard card = it.next();
            cardIndex.put(card.front, card);
        }

        String selectQuery = "SELECT  " + KEY_FRONT + ", " + KEY_CORRECT_COUNT + ", " + KEY_INCORRECT_COUNT + " FROM " + HISTORY_TABLE + " WHERE " + KEY_DECK + " =?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(HISTORY_TABLE, new String[]{KEY_FRONT,
                        KEY_CORRECT_COUNT, KEY_INCORRECT_COUNT}, KEY_DECK + "=?",
                new String[]{deckName}, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String front = cursor.getString(0);

                FlashCard card = cardIndex.get(front);
                if (card != null) {

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
            db.insert(HISTORY_TABLE, null, values);

            if (BuildConfig.DEBUG) {
                Log.i(CardHistoryStore.class.getName(), "Row insert done");
            }
            //2nd argument is String containing nullColumnHack
        } catch (Exception e) {
            Log.e(CardHistoryStore.class.getName(), "Database persistence failed: ", e);
        }
    }


    public void updateCardCounts(String deckName, FlashCard card) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DECK, deckName);
        values.put(KEY_FRONT, card.front);
        values.put(KEY_CORRECT_COUNT, card.numTimesCorrect);
        values.put(KEY_INCORRECT_COUNT, card.numTimesIncorrect);

        // updating row
        int updated = db.update(HISTORY_TABLE, values, KEY_DECK + " = ? AND " + KEY_FRONT + "=?",
                new String[]{deckName, card.front});


        if (BuildConfig.DEBUG) {
            Log.i(CardHistoryStore.class.getName(), "Result of update : " + updated);
        }
        if (updated == 0) {
            addHistory(deckName, card);
        }

        // return contact

    }

    public void reset(String deckName) {
        try (SQLiteDatabase db = this.getWritableDatabase();) {
            db.delete(HISTORY_TABLE, KEY_DECK +" = ?",
                    new String[]{deckName});

            db.delete(LEARNING_SET_TABLE, KEY_DECK +" = ?",
                    new String[]{deckName});
        }
    }

    public void reset() {
        try (SQLiteDatabase db = this.getWritableDatabase();) {
            db.delete(HISTORY_TABLE, "",
                    new String[]{});

            db.delete(LEARNING_SET_TABLE, "",
                    new String[]{});
        }
    }

}