package net.howson.chineseflashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import net.howson.chineseflashcards.mainmenu.MainMenuItem;
import net.howson.chineseflashcards.mainmenu.MenuItemsAdapter;
import net.howson.chineseflashcards.tools.MagnifyingScrollingLayoutCallback;

import java.util.ArrayList;
import java.util.List;

import static net.howson.chineseflashcards.Constants.SELECTED_ITEM_EXTRA;
import static net.howson.chineseflashcards.Constants.TEST_STYLE_EXTRA;

public class ModeSelectActivity extends WearableActivity {


    public static final String NORMAL = "中➡Eng";
    public static final String REVERSE = "Eng➡中";
    public static final String BOTH = "Both";
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

        MenuItemsAdapter adapter = new MenuItemsAdapter(items, new TransitionToFlashcardsHandlerMainMenu());
        // Attach the adapter to the recyclerview to populate items
        menuItemsRecyclerView.setAdapter(adapter);

        // Enables Always-on
        setAmbientEnabled();
    }


    private class TransitionToFlashcardsHandlerMainMenu implements MainMenuClickHandler {


        @Override
        public void onClick(MainMenuItem item) {
            Intent intent = new Intent(ModeSelectActivity.this, FlashcardActivity.class);
            intent.putExtra(SELECTED_ITEM_EXTRA, getIntent().getSerializableExtra(SELECTED_ITEM_EXTRA));


            switch (item.name) {
                case NORMAL:
                    intent.putExtra(TEST_STYLE_EXTRA, TestStyle.NORMAL);
                    break;
                case REVERSE:
                    intent.putExtra(TEST_STYLE_EXTRA, TestStyle.REVERSED);
                    break;
                case BOTH:
                    intent.putExtra(TEST_STYLE_EXTRA, TestStyle.BOTH);
                    break;

            }

            startActivity(intent);

        }
    }


}

