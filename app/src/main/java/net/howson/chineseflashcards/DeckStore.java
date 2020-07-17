package net.howson.chineseflashcards;

import net.howson.chineseflashcards.spacedrep.FlashCard;

import java.util.List;

public final class DeckStore {

    private static final DeckStore instance = new DeckStore();
    private String deckName;


    public static DeckStore getInstance() {
        return instance;
    }


    private List<FlashCard> deck;


    public void setDeck(List<FlashCard> deck, String deckName) {
        this.deck = deck;
        this.deckName = deckName;
    }

    public String getDeckName() {
        return deckName;
    }

    public List<FlashCard> getDeck() {
        return deck;
    }
}
