package net.howson.chineseflashcards.mainmenu;

import net.howson.chineseflashcards.MainMenuClickHandler;
import net.howson.chineseflashcards.file.FileType;
import net.howson.chineseflashcards.file.ResourceType;

public class MainMenuItem {
    public final String name;
    public final String icon;
    public final String fileLocation;
    public final ResourceType resourceType;
    public final FileType fileType;
    public final MainMenuClickHandler handler;


    public MainMenuItem(String name, String icon,
                        String fileLocation,
                        ResourceType resourceType,
                        FileType fileType,
                        MainMenuClickHandler handler) {
        this.name = name;
        this.icon = icon;
        this.fileLocation = fileLocation;
        this.resourceType = resourceType;
        this.fileType = fileType;

        this.handler = handler;
    }
}


