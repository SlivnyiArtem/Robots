package localization;

import java.util.Locale;

public class CurrentLocalizationSettings {
    private static volatile String CurrentLanguageSetting;
    private static String getOrUpdateLangSettings(){
        if (CurrentLanguageSetting == null)
            CurrentLanguageSetting = "ru";
        return CurrentLanguageSetting;
    }
    private static void setLangSettings(String newLangSettings){
        CurrentLanguageSetting = newLangSettings;
    }

    public static Locale getUpdateLocale() throws Exception {
        var current = getOrUpdateLangSettings();
        if (current.equals("ru")) {
            setLangSettings("en");
        }
        else if(current.equals("en")){
            System.out.println("!");
            setLangSettings("ru");
        }
        else{
            throw new Exception("Unsupported Language");
        }
        //var newLocale = current.equals("ru") ? new Locale("en") : new Locale("ru");
        return new Locale(CurrentLanguageSetting);
    }
}
