package localization;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {

    private static final String BUNDLE_STRING = "language";
    private static ResourceBundle r;

    static {
        try {
            r = ResourceBundle.getBundle(BUNDLE_STRING, CurrentLocalizationSettings.getUpdateLocale());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResourceBundle getResourceBundle() {
        return r;
    }

    public static void UpdateBundle() {
        try {
            r = ResourceBundle.getBundle(BUNDLE_STRING, CurrentLocalizationSettings.getUpdateLocale());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void changeLocale(LocalizationType language) {
        r = ResourceBundle.getBundle(BUNDLE_STRING, new Locale(language.toString()));
    }

    public static String getQuit() {
        return r.getString("quit");
    }
    public static String getLoad(){return "load";}
}