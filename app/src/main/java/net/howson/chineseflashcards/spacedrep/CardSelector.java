package net.howson.chineseflashcards.spacedrep;

public interface CardSelector {
    FlashCard getNextCard();

    void recordCorrect(FlashCard currentCard);

    void recordIncorrect(FlashCard currentCard);
}
