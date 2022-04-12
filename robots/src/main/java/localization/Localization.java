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

	public static void UpdateBundle()
	{
		try {
			r = ResourceBundle.getBundle(BUNDLE_STRING, CurrentLocalizationSettings.getUpdateLocale());
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(r.getLocale());
	}
	
	public static void changeLocale(LocalizationType language) {
		r = ResourceBundle.getBundle(BUNDLE_STRING, new Locale(language.toString()));
	}
	
	public static String getTestLabel() {
		return r.getString("testLabel");
	}

	public static String getTestMenuLabel() {
		return r.getString("testDescription");
	}

	public static String getTestMessageLogLabel() {
		return r.getString("testMessageLogLabel");
	}

	public static String getTestMessageLogText() {
		return r.getString("testMessageLogText");
	}

	public static String getTestLookUpLabel() {
		return r.getString("testLookUpLabel");
	}

	public static String getTestLookUpText() {
		return r.getString("testLookUpText");
	}

	public static String getTestLookUpTextItemSystemScheme() {
		return r.getString("testLookUpTextItemSystemScheme");
	}
	
public static String getTestLookUpTextItemUniScheme() {
	return r.getString("testLookUpTextItemUniScheme");
	}

public static String getDocument() {
	return r.getString("document");
	}

public static String getQuit() {
	return r.getString("quit");
	}

public static String getNew() {
	return r.getString("new");
	}

public static String getLoggerSuccess() {
	return r.getString("loggerSuccess");
	}

public static String getProtocolLabel() {
	return r.getString("protocolLabel");
	}

public static String getGameField() {
	return r.getString("localizationGameField");
}

public static String getCommandsLabel() {return r.getString("commandsLabel");}

public static String getExitConfirmation() {
		return r.getString("exitConfirm");
	}

//Не знаю как лучше
//public static String getSomeMessage(String input){return r.getString(input);}

}
