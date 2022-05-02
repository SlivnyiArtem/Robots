package gui.windows;

import localization.Localization;
import log.Logger;

public interface GetLocalizeLabel {
    static String getLocalization(String componentName) {
        var r = Localization.getResourceBundle();
        if (r.containsKey(componentName))
            return r.getString(componentName);
        var message = GetLocalizeLabel.getLocalization("localizationIsNotFound");
        Logger.debug(message);
        return message;
    }
}