package net.howson.chineseflashcards.spacedrep;

import net.howson.chineseflashcards.storage.CardHistoryStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class LearningSetCardSelector implements CardSelector {

    private final Random r = new Random();
    private final int learningSetSize;
    private final List<FlashCard> allCards;
    private final List<FlashCard> otherCards;
    private final CardHistoryStore store;
    private final String deckName;

    private List<FlashCard> learningSet;
    private int minTimesCorrectInSet;
    private int learnThreshold;
    private FlashCard lastCard;

    public LearningSetCardSelector(int learningSetSize, int learnThreshold, String deckName, List<FlashCard> allCards, CardHistoryStore store) {

        
        this.deckName = deckName;
        this.learnThreshold = learnThreshold;
        this.store = store;
        
        
        int learningSetSize1;
        learningSetSize1 = learningSetSize;
        this.allCards = allCards;
        if (learningSetSize1 > allCards.size()) {
            learningSetSize1 = allCards.size();
        }

        this.learningSetSize = learningSetSize1;
        this.learningSet = new ArrayList<>(learningSetSize);
        this.otherCards = new ArrayList<>(allCards);
        this.learningSet.addAll(this.store.getLearningSet(this.deckName, this.allCards));
        this.fillLearningSet();

    }

    private void fillLearningSet() {

        Collections.shuffle(otherCards);

        otherCards.sort(new Comparator<FlashCard>() {
            @Override
            public int compare(FlashCard o1, FlashCard o2) {
                return o1.numTimesCorrect - o2.numTimesCorrect;
            }
        });


        while (learningSet.size() > learningSetSize) {
            otherCards.add(learningSet.remove(0));
        }

        while (learningSet.size() < learningSetSize) {
            learningSet.add(otherCards.remove(0));
        }

        // Make sure this is the right size
        while (learningSet.size() > learningSetSize) {
            otherCards.add(learningSet.remove(0));
        }

        minTimesCorrectInSet = Integer.MAX_VALUE;

        for (int i = 0; i < learningSetSize; ++i) {
            minTimesCorrectInSet = Math.min(minTimesCorrectInSet, learningSet.get(i).numTimesCorrect);
        }

    }




    @Override
    public FlashCard getNextCard() {
        FlashCard x;


        do {
            x = learningSet.get(r.nextInt(learningSet.size()));
        } while (lastCard ==x && learningSetSize>1);
        lastCard = x;
        return lastCard;
    }

    @Override
    public void recordCorrect(FlashCard currentCard) {
        currentCard.numTimesCorrect++;
        currentCard.promotionCounter++;
        store.updateCardCounts(this.deckName, currentCard);

        if (currentCard.promotionCounter >= learnThreshold) {
            currentCard.promotionCounter = 0;
            learningSet.remove(currentCard);
            otherCards.add(currentCard);
            fillLearningSet();
        }

    }

    @Override
    public void recordIncorrect(FlashCard currentCard) {
        currentCard.numTimesIncorrect++;
        currentCard.promotionCounter = 0;
        store.updateCardCounts(this.deckName, currentCard);
    }

    @Override
    public void persistWorkingSet() {
        store.saveLearningSet(this.deckName, this.learningSet);
    }
}
