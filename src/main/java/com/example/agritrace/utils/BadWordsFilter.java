package com.example.agritrace.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class BadWordsFilter {

    private static final Set<String> badWords = new HashSet<>();

    static {
        loadBadWords();
    }

    private static void loadBadWords() {
        try (InputStream inputStream = BadWordsFilter.class.getResourceAsStream("/com/example/agritrace/badwords.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                badWords.add(line.trim().toLowerCase());
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Erreur lors du chargement des bad words : " + e.getMessage());
        }
    }

    public static boolean containsBadWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        String lowerText = text.toLowerCase();
        for (String badWord : badWords) {
            if (lowerText.contains(badWord)) {
                return true;
            }
        }
        return false;
    }
}
