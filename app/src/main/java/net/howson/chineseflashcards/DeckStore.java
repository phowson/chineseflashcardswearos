package net.howson.chineseflashcards;

import net.howson.chineseflashcards.spacedrep.FlashCard;

import java.util.List;

public final class DeckStore {

    private static final DeckStore instance = new DeckStore();


    public static DeckStore getInstance() {
        return instance;
    }


    private List<FlashCard> deck;


    public void setDeck(List<FlashCard> deck) {
        this.deck = deck;
    }


    public List<FlashCard> getDeck() {
        return deck;
    }
}
