package net.howson.chineseflashcards.spacedrep;

public class FlashCard {

    public final String front;
    public final String pinyin;
    public final String back;

    public int numTimesCorrect;
    public int numTimesIncorrect;
    public long lastSeenTsMs;
    public int promotionCounter;


    public FlashCard(String front, String pinyin, String back) {
        this.front = front;
        this.back = back;
        this.pinyin = pinyin;
    }

    @Override
    public String toString() {
        return "FlashCard{" +
                "front='" + front + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", back='" + back + '\'' +
                ", numTimesCorrect=" + numTimesCorrect +
                ", numTimesIncorrect=" + numTimesIncorrect +
                ", lastSeenTsMs=" + lastSeenTsMs +
                ", promotionCounter=" + promotionCounter +
                '}';
    }
}
