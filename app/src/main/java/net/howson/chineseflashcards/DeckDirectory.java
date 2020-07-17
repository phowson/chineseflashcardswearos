package net.howson.chineseflashcards;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import net.howson.chineseflashcards.file.FileType;
import net.howson.chineseflashcards.file.ResourceType;
import net.howson.chineseflashcards.mainmenu.MainMenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeckDirectory {
    public void populateMainMenuItemsWithDecks(Context context, List<MainMenuItem> items) {





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



        List<MainMenuItem> userSubMenu = new ArrayList<>();

        File flashcardDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        Log.i(MainActivity.class.getName(), flashcardDir.getAbsolutePath());


        if (flashcardDir.exists() && flashcardDir.isDirectory()) {


            File[] files = flashcardDir.listFiles();
            if (files!=null) {
                for (File f : files) {
                    final String name = f.getName();
                    if (f.isFile() && name.endsWith(".csv")) {
                        userSubMenu.add(new MainMenuItem(name.substring(0, name.indexOf('.')),
                                "deck_icon",
                                f.getAbsolutePath(),
                                ResourceType.Filesystem,
                                FileType.CSV
                        ));
                    }

                }
            }

            if (!userSubMenu.isEmpty()) {
                items.add(new MainMenuItem("Your decks",
                        "ic_baseline_folder_24",
                        userSubMenu));
            }

        }


    }
}
