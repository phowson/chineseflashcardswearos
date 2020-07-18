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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends WearableActivity {


    private static final String RESET_STATS_NAME = "Reset Stats";
    private static final String ABOUT_NAME = "About";
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
//        menuItems.setCircularScrollingGestureEnabled(true);
//        menuItems.setBezelFraction(0.5f);
//        menuItems.setScrollDegreesPerScreen(90);
        menuItemsRecyclerView.setHasFixedSize(true);
        menuItemsRecyclerView.setEdgeItemsCenteringEnabled(true);

        List<MainMenuItem> items = new ArrayList<>();

        Object menuInInstanceState = null;
        Intent intent = getIntent();
        if (intent != null) {
            menuInInstanceState = intent.getSerializableExtra("menu");
        }


        if (menuInInstanceState == null) {

            new DeckDirectory().populateMainMenuItemsWithDecks(getApplicationContext(), items);

            items.add(new MainMenuItem(RESET_STATS_NAME, "ic_baseline_clear_24", null, null, null));
            items.add(new MainMenuItem(ABOUT_NAME, "ic_baseline_info_24", null, null, null));
        } else {
            items.addAll((Collection<? extends MainMenuItem>) menuInInstanceState);
        }

        MenuItemsAdapter adapter = new MenuItemsAdapter(items, new TransitionToFlashcardsHandlerMainMenu());
        // Attach the adapter to the recyclerview to populate items
        menuItemsRecyclerView.setAdapter(adapter);


        // Enables Always-on
        setAmbientEnabled();
    }


    private class TransitionToFlashcardsHandlerMainMenu implements MainMenuClickHandler {


        @Override
        public void onClick(MainMenuItem item) {


            if (item.name.equals(RESET_STATS_NAME)) {
                resetLearn();
            } else if (item.name.equals(ABOUT_NAME)) {
                about();
            } else if (item.subMenu == null) {
                openFlashcards(item);
            } else {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("menu", (Serializable) item.subMenu);
                startActivity(intent);
            }
        }

        private void openFlashcards(MainMenuItem item) {
            Intent intent = new Intent(MainActivity.this, FlashcardActivity.class);
            List<FlashCard> deck = new DeckLoader().loadCards(getApplicationContext(), item.fileLocation, item.resourceType, item.fileType);

            try (CardHistoryStore db = new CardHistoryStore(getApplicationContext());) {
                db.loadCounts(item.name, deck);
            }
            DeckStore.getInstance().setDeck(deck, item.name);
            startActivity(intent);
        }

        private void about() {


            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Chinese Flashcards\nGPL License\nPhilip Howson 2020")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.create().show();
        }

        private void resetLearn() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("This will reset all scores for all cards in every deck.\nAre you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            try (CardHistoryStore db = new CardHistoryStore(getApplicationContext());) {
                                db.reset();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();
        }
    }


}

