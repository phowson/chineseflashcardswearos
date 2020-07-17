package net.howson.chineseflashcards.spacedrep;

import android.os.Parcelable;

public class FlashCard {

    public final String front;
    public final String back;

    public int numTimesCorrect;
    public long lastSeenTsMs;


    public FlashCard(String front, String back) {
        this.front = front;
        this.back = back;
    }


    @Override
    public String toString() {
        return "{ " +
                "front='" + front + '\'' +
                ", back='" + back + '\'' +
                ", numTimesCorrect=" + numTimesCorrect +
                ", lastSeenTsMs=" + lastSeenTsMs +
                '}';
    }
}
