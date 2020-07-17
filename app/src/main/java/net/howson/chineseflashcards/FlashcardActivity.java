package net.howson.chineseflashcards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import net.howson.chineseflashcards.spacedrep.CardSelector;
import net.howson.chineseflashcards.spacedrep.FlashCard;
import net.howson.chineseflashcards.spacedrep.LearningSetCardSelector;
import net.howson.chineseflashcards.spacedrep.SimpleRandomizedCardSelector;

public class FlashcardActivity extends WearableActivity {

    private TextView counterTextView;

    private enum GuiState {
        FRONT_SHOWN,
        BACK_SHOWN
    }

    private TextView flashcardTextView;
    private TextView topTooltipTextView;
    private TextView bottomTooltipTextView;
    private CardSelector cardSelector;
    private FlashCard currentCard;

    private GuiState state = GuiState.FRONT_SHOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);

        flashcardTextView = (TextView) findViewById(R.id.flashCardTextView);
        topTooltipTextView = (TextView) findViewById(R.id.topToolTipTextView);
        bottomTooltipTextView = (TextView) findViewById(R.id.bottomToolTipTextView);
        counterTextView = (TextView) findViewById(R.id.counterTextView);
        //cardSelector = new SimpleRandomizedCardSelector(DeckStore.getInstance().getDeck());
        cardSelector = new LearningSetCardSelector(10, 2, DeckStore.getInstance().getDeck() );


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

        // Enables Always-on
        setAmbientEnabled();

        selectNewCardShowFront();
    }

    private void selectNewCardShowFront() {
        this.state = GuiState.FRONT_SHOWN;
        currentCard = cardSelector.getNextCard();

        flashcardTextView.setText(currentCard.front);
        flashcardTextView.setTextSize(38);
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
        flashcardTextView.setTextSize(14);
        flashcardTextView.setText(currentCard.back);

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
