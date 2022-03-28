package gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import localization.Localization;

public class MenuBar extends JMenuBar {

	public MenuBar(JFrame where) {
		super();
		JMenu menu = new JMenu(Localization.getDocument());
      menu.setMnemonic(KeyEvent.VK_D);
      this.add(menu);

     JMenuItem menuItem = new JMenuItem(Localization.getNew());
     menuItem.setMnemonic(KeyEvent.VK_N);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_N, ActionEvent.ALT_MASK));
      menuItem.setActionCommand("new");
      menu.add(menuItem);

      
     menuItem = new JMenuItem(Localization.getQuit());
      menuItem.setMnemonic(KeyEvent.VK_Q);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_Q, ActionEvent.ALT_MASK));
      menuItem.setActionCommand("quit");
      menuItem.addActionListener((event) -> {
    	  
        	  where.setVisible(false);
        	  where.dispose();
              System.exit(0);
         
      });
      menu.add(menuItem);

	}
	
}
