package localization;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {

	private static final String BUNDLE_STRING = "language";

	private static ResourceBundle r = ResourceBundle.getBundle(BUNDLE_STRING);

	public static void UpdateBundle()
	{
		System.out.println("Updating");
		//Locale locale = new Locale("ru");
		//r = ResourceBundle.getBundle("LangBundle", locale);
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
}
