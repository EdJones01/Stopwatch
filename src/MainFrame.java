import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

public class MainFrame extends JFrame implements ActionListener {
    Dimension panelSize = new Dimension(300, 250);

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setToPanel(StopwatchPanel.class);

        pack();
        setLocationRelativeTo(null);
    }

    private void setToPanel(Class<? extends JPanel> panelClass) {
        try {
            JPanel panel = panelClass.getDeclaredConstructor(int.class, int.class)
                    .newInstance(panelSize.width, panelSize.height);
            panel.setPreferredSize(panelSize);
            setContentPane(panel);
            validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("stopwatch"))
            setToPanel(StopwatchPanel.class);
        if (cmd.equals("countdown"))
            setToPanel(CountdownPanel.class);
    }
}
