package net.howson.chineseflashcards.spacedrep;

import java.util.List;
import java.util.Random;

public class SimpleRandomizedCardSelector implements CardSelector {

    private final Random r = new Random();
    private final List<FlashCard> deck;

    public SimpleRandomizedCardSelector(List<FlashCard> deck) {
        this.deck = deck;
    }


    @Override
    public FlashCard getNextCard() {
        return deck.get(r.nextInt(deck.size()));
    }


    @Override
    public void recordCorrect(FlashCard currentCard) {
        currentCard.numTimesCorrect++;
    }

    @Override
    public void recordIncorrect(FlashCard currentCard) {

    }
}
