package net.howson.chineseflashcards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.KeyEvent;

import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import net.howson.chineseflashcards.file.DeckLoader;
import net.howson.chineseflashcards.file.FileType;
import net.howson.chineseflashcards.file.ResourceType;
import net.howson.chineseflashcards.mainmenu.MainMenuItem;
import net.howson.chineseflashcards.mainmenu.MenuItemsAdapter;
import net.howson.chineseflashcards.spacedrep.FlashCard;
import net.howson.chineseflashcards.tools.MagnifyingScrollingLayoutCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends WearableActivity {


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


        items.add(new MainMenuItem("HSK4",
                "deck_icon",
                "hsk4",
                ResourceType.Package,
                FileType.TSV,
                new TransitionToFlashcardsHandlerMainMenu()));
        items.add(new MainMenuItem("HSK5",
                "deck_icon",
                "hsk5",
                ResourceType.Package,
                FileType.TSV,
                new TransitionToFlashcardsHandlerMainMenu()));
        items.add(new MainMenuItem("HSK6",
                "deck_icon",
                "hsk6",
                ResourceType.Package,
                FileType.TSV,
                new TransitionToFlashcardsHandlerMainMenu()));


        items.add(new MainMenuItem("Reset Learn", "ic_baseline_clear_24", null, null, null, new ResetLearnHandler()));
        items.add(new MainMenuItem("About", "ic_baseline_info_24",null, null, null, new AboutHandler()));

        MenuItemsAdapter adapter = new MenuItemsAdapter(items);
        // Attach the adapter to the recyclerview to populate items
        menuItemsRecyclerView.setAdapter(adapter);


        // Enables Always-on
        setAmbientEnabled();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(MainActivity.class.getName(), "Saw keyCode = " + keyCode);

        if (keyCode == KeyEvent.KEYCODE_STEM_1) {
            Log.i(MainActivity.class.getName(), "Saw stem 1");
        } else if (keyCode == KeyEvent.KEYCODE_STEM_2) {
            Log.i(MainActivity.class.getName(), "Saw stem 2");
        } else if (keyCode == KeyEvent.KEYCODE_STEM_3) {
            Log.i(MainActivity.class.getName(), "Saw stem 3");
        }
        return super.onKeyDown(keyCode, event);
    }

    private class TransitionToFlashcardsHandlerMainMenu implements MainMenuClickHandler {


        @Override
        public void onClick(MainMenuItem item ) {


            Intent intent = new Intent(MainActivity.this, FlashcardActivity.class);
            List<FlashCard> deck = new DeckLoader().loadCards(getApplicationContext(), item.fileLocation, item.resourceType, item.fileType );
            DeckStore.getInstance().setDeck(deck);
            startActivity(intent);
        }
    }

    private class ResetLearnHandler implements MainMenuClickHandler {
        @Override
        public void onClick(MainMenuItem item) {

        }
    }

    private class AboutHandler implements MainMenuClickHandler {
        @Override
        public void onClick(MainMenuItem item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Chinese Flashcards\nGPL License\nPhilip Howson 2020")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // User cancelled the dialog
//                        }
//                    });
            // Create the AlertDialog object and return it
            builder.create().show();
        }
    }
}

