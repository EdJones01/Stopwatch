import javax.swing.*;
import java.awt.event.ActionListener;

public class StopwatchMenuBar extends JMenuBar {
    private final ActionListener actionListener;

    public StopwatchMenuBar(ActionListener actionListener) {
        this.actionListener = actionListener;

        JMenu modeMenu = new JMenu("Change mode");

        modeMenu.add(createMenuItem("Stopwatch"));
        modeMenu.add(createMenuItem("Countdown"));

        add(modeMenu);
    }

    private JMenuItem createMenuItem(String name) {
        return createMenuItem(name, name.toLowerCase());
    }

    private JMenuItem createMenuItem(String name, String command) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(actionListener);
        menuItem.setActionCommand(command);
        return menuItem;
    }
}