package net.howson.chineseflashcards.file;

import android.content.Context;
import android.util.Log;

import net.howson.chineseflashcards.MainActivity;
import net.howson.chineseflashcards.spacedrep.FlashCard;
import net.howson.chineseflashcards.storage.CardHistoryStore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class DeckLoader {
    private final Pattern pinYin = Pattern.compile(".*[āēīōūǖĀĒĪŌŪǕáéíóúǘÁÉÍÓÚǗǎěǐǒǔǚǍĚǏǑǓǙàèìòùǜÀÈÌÒÙǛaeiouüAEIOUÜ].*");
    private final Pattern pinYin2 = Pattern.compile(".*\\w+[1-5].*");


    public List<FlashCard> loadCards(Context context, final String fileLocation,
                                     ResourceType resourceType,
                                     FileType fileType,
                                     String deckName,
                                     CardHistoryStore store) {
        List<FlashCard> cards;

        switch (resourceType) {
            case Package:
                cards = loadCardsFromPackage(context, fileLocation, fileType);
                break;

            case Filesystem:
                cards = loadCardsFromFilesystem(context, fileLocation, fileType);
                break;

            default:
                throw new Error("Unexpected resource type");

        }


        store.loadCounts(deckName, cards);

        return cards;
    }

    private List<FlashCard> loadCardsFromPackage(Context context, String fileLocation, FileType fileType) {
        int deckId = context.getResources().getIdentifier(fileLocation, "raw", context.getPackageName());


        Log.i(MainActivity.class.getName(), "Deck ID : " + deckId);

        try (InputStream is = context.getResources().openRawResource(deckId);) {
            return doLoad(is, fileType);
        } catch (IOException e) {
            Log.e(MainActivity.class.getName(), "Could not open deck, error : " + e.getMessage(), e);
        }
        return Collections.emptyList();
    }


    private List<FlashCard> loadCardsFromFilesystem(Context context, String fileLocation, FileType fileType) {
        try (FileInputStream fis = new FileInputStream(fileLocation);) {
            return doLoad(fis, fileType);
        } catch (IOException e) {
            Log.e(MainActivity.class.getName(), "Could not open deck, error : " + e.getMessage(), e);
        }
        return Collections.emptyList();
    }


    private List<FlashCard> doLoad(InputStream is, FileType fileType) throws IOException {


        List<FlashCard> out = new ArrayList<>();
        BufferedReader ir = new BufferedReader(new InputStreamReader(is));


        String line;
        while ((line = ir.readLine()) != null) {


            line = line.trim();

            if (line.length() > 0 && !line.startsWith("//") && !line.startsWith("#")) {
                switch (fileType) {
                    case TSV:
                        out.add(parseTsvLine(line));
                        break;


                    case CSV:
                        out.add(parseCsvLine(line));
                        break;

                    default:
                        throw new Error("Unknown file type");
                }
            }
        }


        return out;


    }


    private FlashCard parseCsvLine(String line) {
        int i = line.indexOf(',');
        String front = line.substring(0, i);
        String s = line.substring(i + 1);
        int j = s.indexOf(',');


        String back;
        String pinYin = "";
        if (j != -1) {
            String s1 = s.substring(0, j);
            if (this.pinYin.matcher(s1).matches() || pinYin2.matcher(s1).matches()) {
                pinYin = s1;
                back = s.substring(j + 1);
            } else {
                back = s1 + "\n" + s.substring(j + 1);
            }
        } else {
            back = s;
        }


        return new FlashCard(front, pinYin, back);

    }

    private final Pattern tab = Pattern.compile("\\t");
//    private final  Pattern comma = Pattern.compile(",");

    private FlashCard parseTsvLine(String line) {
        String[] cols = tab.split(line);

        String front;
        String pinYin;
        String back;
        if (cols.length > 3) {
            front = cols[0];
            pinYin = cols[3];
            back = cols[4];
        } else {
            front = cols[0];
            pinYin = cols[1];
            back = cols[2];
        }

        return new FlashCard(front, pinYin, back);
    }


}
