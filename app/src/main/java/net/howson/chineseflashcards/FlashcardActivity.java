package net.howson.chineseflashcards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import net.howson.chineseflashcards.spacedrep.CardSelector;
import net.howson.chineseflashcards.spacedrep.FlashCard;
import net.howson.chineseflashcards.spacedrep.SimpleRandomizedCardSelector;

public class FlashcardActivity extends WearableActivity {

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
        cardSelector = new SimpleRandomizedCardSelector(DeckStore.getInstance().getDeck());

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
        flashcardTextView.setTextSize(32);
        topTooltipTextView.setText("Flip");
        bottomTooltipTextView.setText("Exit");
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
                cardSelector.recordCorrect(currentCard);
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
                cardSelector.recordIncorrect(currentCard);
                selectNewCardShowFront();
                break;


        }


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(FlashcardActivity.class.getName(), "Saw keyCode = " + keyCode);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Saw keyCode = " + keyCode)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();

        if (keyCode == KeyEvent.KEYCODE_STEM_1) {
            onTopButtonPress();
        } else if (keyCode == KeyEvent.KEYCODE_STEM_2) {
            onBottomButtonPress();
        } else if (keyCode == KeyEvent.KEYCODE_STEM_3) {
            Log.i(FlashcardActivity.class.getName(), "Saw stem 3");
        }
        return super.onKeyDown(keyCode, event);
    }


}
