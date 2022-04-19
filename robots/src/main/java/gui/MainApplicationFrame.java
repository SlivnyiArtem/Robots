package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import localization.Localization;
import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private ButtonWindow buttonWindow;
    private GameWindow gameWindow;
    

    //private CurrentLocalizationSettings localizationSettings;
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);
        
        logWindow = createLogWindow();
        addWindow(logWindow);

        buttonWindow = createButtonWindow();
        addWindow(buttonWindow);

        gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    protected LogWindow createLogWindow()
    {
        logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(800,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(Localization.getLoggerSuccess());
        return logWindow;
    }

    //protected void createLocalizationSettings()
    //{
        //localizationSettings = new CurrentLocalizationSettings("ru");
    //}

    protected ButtonWindow createButtonWindow()
    {
        buttonWindow = new ButtonWindow();
        buttonWindow.setLocation(500,10);
        buttonWindow.setSize(300, 800);
        setMinimumSize(buttonWindow.getSize());
        buttonWindow.pack();
        return buttonWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }
    
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = new JMenu(Localization.getTestLookUpLabel());
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                Localization.getTestLookUpText());

        {
            JMenuItem systemLookAndFeel =
                    new JMenuItem(Localization.getTestLookUpTextItemSystemScheme(), KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel =
                    new JMenuItem(Localization.getTestLookUpTextItemUniScheme(), KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }
        //----------------------------------------------------------------------------------------------------

        var exitMenu = new JMenuItem(Localization.getQuit());
        exitMenu.addActionListener((event) -> {
            Logger.debug(Localization.getExitConfirmation());
            if (Exiter.onExit() == 0) System.exit(0);
        });
        {
            exitMenu.getAccessibleContext().setAccessibleDescription( Localization.getTestLabel());

            {
                JMenuItem addLogMessageItem = new JMenuItem(Localization.getTestMessageLogLabel(), KeyEvent.VK_E);
                addLogMessageItem.addActionListener((event) -> {
                    Logger.debug(Localization.getNewStringDebug());
                });
            }
        }


        //-------------------------------------------------------------------------------------------------

        JMenu testMenu = new JMenu(Localization.getTestLabel());
        testMenu.getAccessibleContext().setAccessibleDescription(Localization.getTestMenuLabel());

        {
            JMenuItem addLogMessageItem = new JMenuItem(Localization.getLogMessage());
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug(Localization.getNewStringDebug());
            });
            testMenu.add(addLogMessageItem);
        }

        //-----------------------------------------------------------------------------------------------

        JMenu langMenu = new JMenu(Localization.getLanguageLabel());
        testMenu.getAccessibleContext()
                .setAccessibleDescription("Смена языка");
        {
            JMenuItem addChangeLocalizationItem = new JMenuItem("Сменить язык");
            addChangeLocalizationItem.addActionListener((event) -> {

                Localization.UpdateBundle();

                setJMenuBar(generateMenuBar());
                logWindow.setTitle(Localization.getProtocolLabel());
                buttonWindow.setTitle(Localization.getCommandsLabel());
                gameWindow.setTitle(Localization.getGameField());
                Logger.debug(Localization.getChangeLangDebug());
            });
            langMenu.add(addChangeLocalizationItem);
        }



        //-----------------------------------------------------------------------------------------------
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(exitMenu);
        menuBar.add(langMenu);
        return menuBar;
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
