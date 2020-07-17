package net.howson.chineseflashcards;

import net.howson.chineseflashcards.file.FileType;
import net.howson.chineseflashcards.file.ResourceType;
import net.howson.chineseflashcards.mainmenu.MainMenuItem;

import java.util.ArrayList;
import java.util.List;

public class DeckDirectory {
    public void populateMainMenuItemsFromDecks(List<MainMenuItem> items) {


        for (int i = 1; i < 6; ++i) {
            items.add(new MainMenuItem("HSK" + i,
                    "deck_icon",
                    "hsk" + i,
                    ResourceType.Package,
                    FileType.TSV

            ));
        }

        List<MainMenuItem> subMenu = new ArrayList<>();
        for (int i = 1; i <= 3; ++i) {
            subMenu.add(new MainMenuItem("Lesson" + i,
                    "deck_icon",
                    "hsk6_" + i,
                    ResourceType.Package,
                    FileType.CSV
            ));
        }

        subMenu.add(new MainMenuItem("All",
                "deck_icon",
                "hsk6",
                ResourceType.Package,
                FileType.TSV
        ));

        items.add(new MainMenuItem("HSK6",
                "ic_baseline_folder_24",
                subMenu));


    }
}
