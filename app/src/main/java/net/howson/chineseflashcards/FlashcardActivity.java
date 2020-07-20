package net.howson.chineseflashcards;

import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import net.howson.chineseflashcards.file.DeckLoader;
import net.howson.chineseflashcards.mainmenu.MainMenuItem;
import net.howson.chineseflashcards.spacedrep.CardSelector;
import net.howson.chineseflashcards.spacedrep.FlashCard;
import net.howson.chineseflashcards.spacedrep.LearningSetCardSelector;
import net.howson.chineseflashcards.storage.CardHistoryStore;

import java.util.List;
import java.util.Random;

import static net.howson.chineseflashcards.Constants.TEST_STYLE_EXTRA;
import static net.howson.chineseflashcards.Constants.SELECTED_ITEM_EXTRA;

public class FlashcardActivity extends WearableActivity {

    public static final int LARGE_POINT_SIZE = 38;
    public static final int SMALL_POINT_SIZE = 14;
    private TextView counterTextView;
    private CardHistoryStore store;
    private Random r= new Random();

    private enum GuiState {
        FRONT_SHOWN,
        BACK_SHOWN
    }

    private TextView flashcardTextView;
    private TextView topTooltipTextView;
    private TextView bottomTooltipTextView;
    private CardSelector cardSelector;
    private FlashCard currentCard;
    private TestStyle style;
    private boolean reversed = false;

    private GuiState state = GuiState.FRONT_SHOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);


        flashcardTextView = (TextView) findViewById(R.id.flashCardTextView);
        topTooltipTextView = (TextView) findViewById(R.id.topToolTipTextView);
        bottomTooltipTextView = (TextView) findViewById(R.id.bottomToolTipTextView);
        counterTextView = (TextView) findViewById(R.id.counterTextView);


        style = (TestStyle) getIntent().getSerializableExtra(TEST_STYLE_EXTRA);

        flashcardTextView.setText("Loading");
        flashcardTextView.setTextSize(LARGE_POINT_SIZE);
        topTooltipTextView.setText("");
        bottomTooltipTextView.setText("");


        this.store = new CardHistoryStore(getApplicationContext());


        // Enables Always-on
        setAmbientEnabled();


        final MainMenuItem item = (MainMenuItem) getIntent().getSerializableExtra(SELECTED_ITEM_EXTRA);

        List<FlashCard> deck = new DeckLoader().loadCards(getApplicationContext(), item.fileLocation, item.resourceType, item.fileType);
        cardSelector = new LearningSetCardSelector(10, 2, item.name, deck, store);
        topTooltipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTopButtonPress();
            }
        });

        bottomTooltipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBottomButtonPress();
            }
        });

        try (CardHistoryStore db = new CardHistoryStore(getApplicationContext());) {
            db.loadCounts(item.name, deck);
        }


        selectNewCardShowFront();
    }

    @Override
    protected void onStop() {

        Log.i(FlashcardActivity.class.getName(), "Stopping");
        if (this.store != null) {
            this.store.close();
        }


        super.onStop();
    }

    private void selectNewCardShowFront() {
        this.state = GuiState.FRONT_SHOWN;
        currentCard = cardSelector.getNextCard();


        switch (style) {
            case NORMAL:
                reversed =false;
                break;

            case REVERSED:
                reversed =true;
                break;

            case BOTH:
                reversed = r.nextBoolean();
                break;
        }


        if (reversed) {
            flashcardTextView.setText(currentCard.back);
        } else {
            flashcardTextView.setText(currentCard.front);
        }


        if (reversed) {
            flashcardTextView.setTextSize(SMALL_POINT_SIZE);
        } else {
            flashcardTextView.setTextSize(LARGE_POINT_SIZE);
        }


        topTooltipTextView.setText("Flip");
        bottomTooltipTextView.setText("Exit");

        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString spanned1 = new SpannableString(Integer.toString(currentCard.numTimesCorrect % 100));
        spanned1.setSpan(new ForegroundColorSpan(Color.GREEN), 0, spanned1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spanned2 = new SpannableString(" / ");

        SpannableString spanned3 = new SpannableString(Integer.toString(currentCard.numTimesIncorrect % 100));
        spanned3.setSpan(new ForegroundColorSpan(Color.RED), 0, spanned3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.append(spanned1);
        builder.append(spanned2);
        builder.append(spanned3);

        counterTextView.setText(builder);
    }

    private void showCardBack() {
        this.state = GuiState.BACK_SHOWN;

        if (reversed) {
            flashcardTextView.setTextSize(LARGE_POINT_SIZE);
        } else {
            flashcardTextView.setTextSize(SMALL_POINT_SIZE);
        }


        if (reversed) {

            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString spanned1 = new SpannableString(currentCard.front);
            SpannableString spanned3 = new SpannableString("\n" + currentCard.pinyin);
            spanned3.setSpan(new RelativeSizeSpan(0.5f), 0, spanned3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.append(spanned1);
            builder.append(spanned3);
            flashcardTextView.setText(builder);

        } else {


            flashcardTextView.setText(currentCard.pinyin + "\n" + currentCard.back);
        }

        topTooltipTextView.setText("Correct");
        bottomTooltipTextView.setText("Incorrect");
    }

    private void onBottomButtonPress() {
        Log.i(FlashcardActivity.class.getName(), "Bottom button press");
        switch (state) {
            case FRONT_SHOWN:
                finish();
                break;
            case BACK_SHOWN:
                cardSelector.recordIncorrect(currentCard);

                selectNewCardShowFront();
                break;
        }

    }

    private void onTopButtonPress() {
        switch (state) {
            case FRONT_SHOWN:
                showCardBack();
                break;
            case BACK_SHOWN:
                cardSelector.recordCorrect(currentCard);
                selectNewCardShowFront();
                break;


        }


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_STEM_1) {
            onTopButtonPress();
        } else if (keyCode == KeyEvent.KEYCODE_STEM_2) {
            onBottomButtonPress();
        }
        return super.onKeyDown(keyCode, event);
    }


}
