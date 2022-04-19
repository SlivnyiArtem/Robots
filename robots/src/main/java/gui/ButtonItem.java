package gui;

import localization.Localization;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ButtonItem {
    public Supplier<String> TitleSupplier;
    public Button ItemButton;


    public ButtonItem(Supplier<String> supplier, Button button){
        TitleSupplier = supplier;
        ItemButton = button;
    }

    public void updateItem(){
        ItemButton.setLabel(TitleSupplier.get());
    }
}
