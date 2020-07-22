package net.howson.chineseflashcards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import net.howson.chineseflashcards.file.DeckLoader;
import net.howson.chineseflashcards.mainmenu.MainMenuItem;
import net.howson.chineseflashcards.mainmenu.MenuItemsAdapter;
import net.howson.chineseflashcards.spacedrep.FlashCard;
import net.howson.chineseflashcards.storage.CardHistoryStore;
import net.howson.chineseflashcards.tools.MagnifyingScrollingLayoutCallback;

import java.util.ArrayList;
import java.util.List;

import static net.howson.chineseflashcards.Constants.SELECTED_ITEM_EXTRA;
import static net.howson.chineseflashcards.Constants.TEST_STYLE_EXTRA;
import static net.howson.chineseflashcards.spacedrep.Constants.LEARN_THRESHOLD;

public class ModeSelectActivity extends WearableActivity {


    public static final String NORMAL = "中➡Eng";
    public static final String REVERSE = "Eng➡中";
    public static final String BOTH = "Both";
    public static final String VIEW_STATS = "View stats";
    public static final String RESET_STATS = "Reset stats";


    private WearableRecyclerView menuItemsRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        menuItemsRecyclerView = (WearableRecyclerView) findViewById(R.id.menuItems);
        MagnifyingScrollingLayoutCallback customScrollingLayoutCallback =
                new MagnifyingScrollingLayoutCallback();
        menuItemsRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));
        menuItemsRecyclerView.setHasFixedSize(true);
        menuItemsRecyclerView.setEdgeItemsCenteringEnabled(true);

        List<MainMenuItem> items = new ArrayList<>();


        items.add(new MainMenuItem(NORMAL, "ic_baseline_play_circle_filled_24", null, null, null));
        items.add(new MainMenuItem(REVERSE, "ic_baseline_play_circle_filled_24", null, null, null));
        items.add(new MainMenuItem(BOTH, "ic_baseline_play_circle_filled_24", null, null, null));
        items.add(new MainMenuItem(VIEW_STATS, "ic_baseline_settings_24", null, null, null));
        items.add(new MainMenuItem(RESET_STATS, "ic_baseline_clear_24", null, null, null));

        MenuItemsAdapter adapter = new MenuItemsAdapter(items, new TransitionToFlashcardsHandlerMainMenu());
        // Attach the adapter to the recyclerview to populate items
        menuItemsRecyclerView.setAdapter(adapter);

        // Enables Always-on
        setAmbientEnabled();
    }


    private void showStats(MainMenuItem item) {


        int numCards;
        int seenCards;
        int familiarCards;

        try (CardHistoryStore store = new CardHistoryStore(this.getApplicationContext())) {
            List<FlashCard> deck = new DeckLoader().loadCards(getApplicationContext(), item.fileLocation, item.resourceType, item.fileType, item.name, store);
            numCards = deck.size();

            seenCards = 0;
            familiarCards = 0;
            for (FlashCard f : deck) {
                if (f.numTimesIncorrect > 0 || f.numTimesCorrect > 0) {
                    seenCards++;
                }
                if (f.numTimesCorrect >= f.numTimesIncorrect + LEARN_THRESHOLD) {
                    familiarCards++;

                }
            }

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ModeSelectActivity.this);
        builder.setMessage("Cards : " + numCards + "\nSeen : " + seenCards + "\nFamiliar : " + familiarCards)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();


    }


    private class TransitionToFlashcardsHandlerMainMenu implements MainMenuClickHandler {


        @Override
        public void onClick(MainMenuItem item) {

            final MainMenuItem deckItem = (MainMenuItem) getIntent().getSerializableExtra(SELECTED_ITEM_EXTRA);
            final Intent intent = new Intent(ModeSelectActivity.this, FlashcardActivity.class);
            intent.putExtra(SELECTED_ITEM_EXTRA, deckItem);


            switch (item.name) {
                case NORMAL:
                    intent.putExtra(TEST_STYLE_EXTRA, TestStyle.NORMAL);
                    startActivity(intent);
                    break;
                case REVERSE:
                    intent.putExtra(TEST_STYLE_EXTRA, TestStyle.REVERSED);
                    startActivity(intent);
                    break;
                case BOTH:
                    intent.putExtra(TEST_STYLE_EXTRA, TestStyle.BOTH);
                    startActivity(intent);
                    break;

                case RESET_STATS: {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ModeSelectActivity.this);
                    builder.setMessage("This will scores for all cards in this deck.\nAre you sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try (CardHistoryStore db = new CardHistoryStore(getApplicationContext());) {
                                        db.reset(deckItem.name);
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.create().show();

                    break;
                }

                case VIEW_STATS:
                    showStats(deckItem);
                    break;

            }


        }
    }


}

