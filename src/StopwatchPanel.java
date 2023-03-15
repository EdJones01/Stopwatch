import org.apache.commons.lang3.time.StopWatch;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

public class StopwatchPanel extends JPanel implements ActionListener {
    private final int fps = 144;

    private Timer frameTimer;

    private StopWatch stopWatch = new StopWatch();

    JLabel timeLabel;
    JLabel millisecondLabel;

    JTextArea lapTextArea;

    JButton startButton;

    LinkedList<String> laps = new LinkedList<>();

    public StopwatchPanel(int panelWidth, int panelHeight) {
        setLayout(new BorderLayout());
        JPanel timerPanel = new JPanel();
        timerPanel.setLayout(new BorderLayout());

        timeLabel = new JLabel("00:00:00");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setVerticalAlignment(SwingConstants.TOP);
        timeLabel.setFont(getFont().deriveFont(Font.PLAIN, 60));
        timeLabel.setBounds(new Rectangle(0, 0, panelWidth, panelHeight));
        timerPanel.add(timeLabel, BorderLayout.CENTER);

        millisecondLabel = new JLabel("000");
        millisecondLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        timerPanel.add(millisecondLabel, BorderLayout.EAST);

        add(timerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout());
        startButton = createButton("Start");
        buttonPanel.add(startButton);

        buttonPanel.add(createButton("Lap"));
        buttonPanel.add(createButton("Reset"));

        add(buttonPanel, BorderLayout.CENTER);

        lapTextArea = new JTextArea();
        lapTextArea.setMaximumSize(new Dimension(panelWidth / 2, panelHeight / 2 - 60));
        lapTextArea.setEditable(false);
        lapTextArea.setRows(6);
        lapTextArea.setBackground(Tools.DARK_GRAY);
        lapTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (Tools.showYesNoDialog("Do you want to clear all saved laps?")) {
                    laps.clear();
                    updateLapTextArea();
                }
            }
        });

        JScrollPane lapScrollPane = new JScrollPane(lapTextArea);
        lapScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        lapScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        lapScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(lapScrollPane, BorderLayout.SOUTH);


        frameTimer = new Timer(1000 / fps, this);
        frameTimer.setActionCommand("frame");

        frameTimer.start();

        stopWatch.start();
        stopWatch.suspend();
    }

    private JButton createButton(String name) {
        JButton button = new JButton(name);
        button.setFont(getFont().deriveFont(Font.PLAIN, 20));
        button.addActionListener(this);
        button.setActionCommand(name.toLowerCase());
        return button;
    }

    private void updateLapTextArea() {
        lapTextArea.setText("");
        for (String s : laps)
            lapTextArea.append(s + "\n");
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    }

    public String[] formatNanoseconds(long nanos) {
        long milliseconds = nanos / 1000000;
        long seconds = (nanos / 1000000000) % 60;
        long minutes = (nanos / 60000000000L) % 60;
        long hours = nanos / 3600000000000L;
        String[] formattedTime = new String[2];
        formattedTime[0] = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        formattedTime[1] = getLastThreeLetters(String.format("%03d", milliseconds));
        return formattedTime;
    }

    private String getLastThreeLetters(String str) {
        if (str.length() >= 3) {
            return str.substring(str.length() - 3);
        } else {
            String paddedStr = String.format("%3s", str).replace(' ', '0');
            return paddedStr;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("frame")) {
            timeLabel.setText(formatNanoseconds(stopWatch.getNanoTime())[0]);
            millisecondLabel.setText(formatNanoseconds(stopWatch.getNanoTime())[1]);

        }
        if (cmd.equals("start")) {
            if (stopWatch.isSuspended()) {
                startButton.setText("Stop");
                stopWatch.resume();
            } else {
                startButton.setText("Start");
                stopWatch.suspend();
            }
        }
        if (cmd.equals("lap")) {
            laps.add(formatNanoseconds(stopWatch.getNanoTime())[0] + " " +
                    formatNanoseconds(stopWatch.getNanoTime())[1]);

            updateLapTextArea();
        }
        if (cmd.equals("reset")) {
            stopWatch = new StopWatch();
            stopWatch.start();
            stopWatch.suspend();
            laps.clear();
            startButton.setText("Start");
            updateLapTextArea();
        }
    }
}