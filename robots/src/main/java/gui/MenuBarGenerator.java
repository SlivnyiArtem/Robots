package gui;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import localization.Localization;
import log.Logger;

public class MenuBarGenerator extends JMenuBar {
	
	public MenuBarGenerator() {
		super();
		 this.add(getUpLookAndFeelMenu());
	     this.add(getTestMenu());
	}
	
    private JMenu getTestMenu() {
        JMenu testMenu = new JMenu(Localization.getTestLabel());
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
        		Localization.getTestMenuLabel());
        
        {
            JMenuItem addLogMessageItem = new JMenuItem(Localization.getTestMessageLogLabel(), KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug(Localization.getTestMessageLogText());
            });
            testMenu.add(addLogMessageItem);
        }
        return testMenu;
    }
    
    private JMenu getUpLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu(Localization.getTestLookUpLabel());
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                Localization.getTestLookUpText());
        
        {
            JMenuItem systemLookAndFeel = new JMenuItem(Localization.getTestLookUpTextItemSystemScheme(), KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }
        

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem(Localization.getTestLookUpTextItemUniScheme(), KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        return lookAndFeelMenu;
    }
    
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

}
