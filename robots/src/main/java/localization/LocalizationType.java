package localization;

public enum LocalizationType {
	RU("ru"),
	EN("en");
	
	   private String string;
	   
	   LocalizationType(String name){string = name;}

	   @Override
	   public String toString() {
	       return string;
	   }
}
