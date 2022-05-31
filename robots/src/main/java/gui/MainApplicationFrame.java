package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gui.windows.ButtonWindow;
import gui.windows.GameWindow;
import gui.windows.GetLocalizeLabel;
import gui.windows.LogWindow;
import localization.Localization;
import log.Logger;
import lombok.SneakyThrows;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 */
public class MainApplicationFrame extends JFrame implements GetLocalizeLabel {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private ButtonWindow buttonWindow;
    private final GameWindow gameWindow;

    public MainApplicationFrame() throws IOException {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);

        buttonWindow = createButtonWindow();
        addWindow(buttonWindow);

        gameWindow = new GameWindow();
        Logger.debug(String.valueOf(gameWindow.Width));
        Logger.debug(String.valueOf(gameWindow.Hight));

        if( gameWindow.Width != 0 && gameWindow.Hight != 0){
            gameWindow.setSize((int)gameWindow.Width, (int)gameWindow.Hight);
        }
        else{
            gameWindow.setSize(400, 400);

        }
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                OnClosing();
            }
        });
    }

    private void OnClosing(){
        var conf = Dialoger.confirmRecovery();
        if (conf == 0){
            logWindow.doDefaultCloseAction();
            gameWindow.doDefaultCloseAction();
        }

    }

    protected LogWindow createLogWindow() throws IOException {
        logWindow = new LogWindow(Logger.getDefaultLogSource());
        //logWindow.setLocation(800, 10);
        //logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(GetLocalizeLabel.getLocalization("loggerSuccess"));
        return logWindow;
    }

    protected ButtonWindow createButtonWindow() {
        buttonWindow = new ButtonWindow();
        buttonWindow.setLocation(500, 10);
        buttonWindow.setSize(300, 800);
        setMinimumSize(buttonWindow.getSize());
        buttonWindow.pack();
        return buttonWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(GetLookAndFeelMenu());
        menuBar.add(GetTestMenu());
        menuBar.add(GetExitMenu());
        menuBar.add(GetLangMenu());
        return menuBar;
    }

    private JMenu GetLangMenu() {
        JMenu langMenu = new JMenu(GetLocalizeLabel.getLocalization("language"));
        langMenu.getAccessibleContext()
                .setAccessibleDescription("Смена языка");
        {
            JMenuItem addChangeLocalizationItem = new JMenuItem("Сменить язык");
            addChangeLocalizationItem.addActionListener((event) -> {

                Localization.UpdateBundle();

                setJMenuBar(generateMenuBar());
                logWindow.setTitle(GetLocalizeLabel.getLocalization("protocolLabel"));
                buttonWindow.setTitle(GetLocalizeLabel.getLocalization("commandsLabel"));
                gameWindow.setTitle(GetLocalizeLabel.getLocalization("localizationGameField"));
                buttonWindow.updateButtonLabels();
                Logger.debug(GetLocalizeLabel.getLocalization("changeLang"));
            });
            langMenu.add(addChangeLocalizationItem);
        }
        return langMenu;
    }

    private JMenuItem GetExitMenu() {
        var exitMenu = new JMenuItem(Localization.getQuit());
        exitMenu.addActionListener((event) -> {
            Logger.debug(GetLocalizeLabel.getLocalization("exiterConfirmation"));
            if (Dialoger.onExit() == 0) System.exit(0);
        });
        {
            exitMenu.getAccessibleContext()
                    .setAccessibleDescription(GetLocalizeLabel.getLocalization("testLabel"));

            {
                JMenuItem addLogMessageItem =
                        new JMenuItem(GetLocalizeLabel
                                .getLocalization("testMessageLogLabel"),
                                KeyEvent.VK_E);
                addLogMessageItem.addActionListener((event) ->
                        Logger.debug(GetLocalizeLabel.getLocalization("getNewStringDebug")));
            }
        }
        return exitMenu;
    }

    private JMenu GetTestMenu() {
        var testMenu = new JMenu(GetLocalizeLabel.getLocalization("testLabel"));
        {
            JMenuItem addLogMessageItem = new JMenuItem(GetLocalizeLabel.getLocalization("logMessage"));
            addLogMessageItem.addActionListener((event) ->
                    Logger.debug(GetLocalizeLabel.getLocalization("newStringDebug")));
            testMenu.add(addLogMessageItem);
        }
        return testMenu;
    }

    private JMenu GetLookAndFeelMenu() {
        var lookAndFeelMenu = new JMenu(GetLocalizeLabel.getLocalization("testLookUpLabel"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext()
                .setAccessibleDescription(GetLocalizeLabel.getLocalization("testLookUpText"));

        {
            JMenuItem systemLookAndFeel =
                    new JMenuItem(GetLocalizeLabel.getLocalization("testLookUpTextItemSystemScheme"), KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel =
                    new JMenuItem(GetLocalizeLabel.getLocalization("testLookUpTextItemSystemScheme"), KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }
        return lookAndFeelMenu;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
}