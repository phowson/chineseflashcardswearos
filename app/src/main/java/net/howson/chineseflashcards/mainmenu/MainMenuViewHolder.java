package net.howson.chineseflashcards.mainmenu;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.wear.widget.WearableRecyclerView;

import net.howson.chineseflashcards.R;

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
public class MainMenuViewHolder extends WearableRecyclerView.ViewHolder {
    public final ImageView menuIconImageView;
    // Your holder should contain a member variable
    // for any view that will be set as you render a row
    public final TextView itemNameTextView;
//            public Button messageButton;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public MainMenuViewHolder(View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);

        menuIconImageView = (ImageView) itemView.findViewById(R.id.menu_icon);
        itemNameTextView = (TextView) itemView.findViewById(R.id.item_name);
//                messageButton = (Button) itemView.findViewById(R.id.message_button);
    }
}
