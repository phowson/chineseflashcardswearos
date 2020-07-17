package net.howson.chineseflashcards.mainmenu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.wear.widget.WearableRecyclerView;

import net.howson.chineseflashcards.MainMenuClickHandler;
import net.howson.chineseflashcards.R;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class MenuItemsAdapter extends
        WearableRecyclerView.Adapter<MainMenuViewHolder> {

    private final MainMenuClickHandler handler;
    private List<MainMenuItem> mainMenuItems;
    private Context context;

    // Pass in the contact array into the constructor
    public MenuItemsAdapter(List<MainMenuItem> mainMenuItems,  MainMenuClickHandler handler) {
        this.mainMenuItems = mainMenuItems;
        this.handler = handler;
    }



    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public MainMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.mainmenu_item, parent, false);


        // Return a new holder instance
        MainMenuViewHolder viewHolder = new MainMenuViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(MainMenuViewHolder holder, int position) {
        // Get the data model based on position
        final MainMenuItem mainMenuItem = mainMenuItems.get(position);

        ImageView menuIconImageView = holder.menuIconImageView;

        if (context != null) {
            int id = context.getResources().getIdentifier(mainMenuItem.icon, "drawable", context.getPackageName());
            menuIconImageView.setImageResource(id);
        }

        menuIconImageView.setClickable(true);
        menuIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(MenuItemsAdapter.class.getName(), "Click detected for " + mainMenuItem.name);
                if (handler!=null) {
                    handler.onClick(mainMenuItem);
                }

            }
        });


        // Set item views based on your views and data model
        TextView textView = holder.itemNameTextView;
        textView.setText(mainMenuItem.name);

        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(MenuItemsAdapter.class.getName(), "Click detected for " + mainMenuItem.name);
                if (handler!=null) {
                    handler.onClick(mainMenuItem);
                }
            }
        });

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mainMenuItems.size();
    }
}
