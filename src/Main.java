import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String args[]) {
        FlatLightLaf.setup();

        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        MainFrame mainFrame = new MainFrame();
        StopwatchMenuBar stopwatchMenuBar = new StopwatchMenuBar(mainFrame);
        mainFrame.setJMenuBar(stopwatchMenuBar);
        mainFrame.setVisible(true);
    }
}