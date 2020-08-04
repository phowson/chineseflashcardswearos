package net.howson.chineseflashcards.mainmenu;

import net.howson.chineseflashcards.file.FileType;
import net.howson.chineseflashcards.file.ResourceType;

import java.io.Serializable;
import java.util.List;

public class MainMenuItem implements Serializable  {

    public final String icon;
    public final List<MainMenuItem> subMenu;

    public final String name;
    public final String fileLocation;
    public final ResourceType resourceType;
    public final FileType fileType;



    public MainMenuItem(String name, String icon,
                        List<MainMenuItem> subMenu) {
        this.name = name;
        this.icon = icon;
        this.subMenu = subMenu;


        fileLocation = null;
        resourceType = null;
        fileType = null;

    }


    public MainMenuItem(String name, String icon,
                        String fileLocation,
                        ResourceType resourceType,
                        FileType fileType
                        ) {
        this.name = name;
        this.icon = icon;
        this.fileLocation = fileLocation;
        this.resourceType = resourceType;
        this.fileType = fileType;
        this.subMenu = null;
    }
}


