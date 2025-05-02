package org.agritrace.services;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LanguageManager {
    private static LanguageManager instance;
    private final ObjectProperty<Locale> currentLocale;
    private ResourceBundle messages;
    private static final String PREF_LANGUAGE = "app_language";

    private LanguageManager() {
        // Load saved language preference or default to English
        String savedLanguage = Preferences.userRoot().get(PREF_LANGUAGE, "en");
        currentLocale = new SimpleObjectProperty<>(new Locale(savedLanguage));
        loadMessages();
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public void setLanguage(String language) {
        currentLocale.set(new Locale(language));
        // Save language preference
        Preferences.userRoot().put(PREF_LANGUAGE, language);
        loadMessages();
    }

    private void loadMessages() {
        messages = ResourceBundle.getBundle("i18n.messages", currentLocale.get());
    }

    public String getMessage(String key) {
        return messages.getString(key);
    }

    public String getMessage(String key, Object... args) {
        return String.format(messages.getString(key), args);
    }

    public ObjectProperty<Locale> currentLocaleProperty() {
        return currentLocale;
    }

    public Locale getCurrentLocale() {
        return currentLocale.get();
    }

    public ResourceBundle getMessages() {
        return messages;
    }
}
