import org.apache.commons.lang3.time.StopWatch;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.*;

public class CountdownPanel extends JPanel implements ActionListener {
    private JLabel timeLabel;

    private final int fps = 144;

    private Timer frameTimer;

    private Timer flashTimer;
    private int flashCounter;

    private long countdownPeriod = 0;

    private JButton startButton;

    private int[] numpadOrder = new int[]{7, 8, 9, 4, 5, 6, 1, 2, 3, 0};
    private LinkedList<String> input = new LinkedList<>();

    private StopWatch stopWatch = new StopWatch();

    public CountdownPanel(int panelWidth, int panelHeight) {
        setLayout(new BorderLayout());

        JPanel numpadPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        numpadPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (int i = 0; i <= 9; i++) {
            numpadPanel.add(createNumpadButton(numpadOrder[i]));
        }

        numpadPanel.add(new JLabel(""));

        JButton deleteButton = new JButton();
        BufferedImage image = Tools.resizeBufferedImage(Tools.loadBufferedImage("backspace.png"),
                20, 20);
        deleteButton.setIcon(new ImageIcon(image));
        deleteButton.addActionListener(this);
        deleteButton.setActionCommand("delete");
        numpadPanel.add(deleteButton);

        timeLabel = new JLabel("00:00:00");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setVerticalAlignment(SwingConstants.TOP);
        timeLabel.setFont(getFont().deriveFont(Font.PLAIN, 60));
        timeLabel.setBounds(new Rectangle(0, 0, panelWidth, panelHeight));

        add(numpadPanel, BorderLayout.CENTER);
        add(timeLabel, BorderLayout.NORTH);

        JPanel adminPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        adminPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        startButton = createAdminButton("Start");
        adminPanel.add(startButton);
        adminPanel.add(createAdminButton("Reset"));

        add(adminPanel, BorderLayout.SOUTH);

        frameTimer = new Timer(1000 / fps, this);
        frameTimer.setActionCommand("frame");

        frameTimer.start();

        flashTimer = new Timer(1000, this);
        flashTimer.setActionCommand("flash");

        stopWatch.start();
        stopWatch.suspend();
    }

    private void reset() {
        input.clear();
        stopWatch.reset();
        stopWatch.start();
        stopWatch.suspend();
        startButton.setText("Start");
        timeLabel.setText(getParsedTimeString().replaceAll("..(?!$)", "$0:"));
    }

    private JButton createAdminButton(String name) {
        JButton button = new JButton(name);
        button.addActionListener(this);
        button.setActionCommand(name.toLowerCase());
        return button;
    }

    private JButton createNumpadButton(int index) {
        JButton button = new JButton("" + index);
        button.addActionListener(this);
        button.setActionCommand("" + index);
        return button;
    }

    private long convertToNanoseconds() {
        int[] hms = convertToHMS();
        return (((long) hms[0] * 60 + hms[1]) * 60 + hms[2]) * 1_000_000_000L;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("frame")) {
            if (!stopWatch.isSuspended()) {
                timeLabel.setText(formatNanoseconds(countdownPeriod + (long) Math.pow(10, 9)
                        - stopWatch.getNanoTime()));

                if (stopWatch.getNanoTime() > countdownPeriod - (long) Math.pow(10, 9))
                    flashTimer.start();

                if (stopWatch.getNanoTime() > countdownPeriod) {
                    reset();
                }
            }
        }
        if (cmd.equals("flash")) {
            if (flashCounter > -1) {
                if (flashCounter % 2 != 0)
                    timeLabel.setForeground(Color.red.darker());
                else
                    timeLabel.setForeground(new JLabel().getForeground());
            } else {
                flashTimer.stop();
            }
            flashCounter--;
        }
        if (cmd.equals("start")) {
            if (stopWatch.isSuspended()) {
                if (convertToNanoseconds() != 0) {
                    flashCounter = 3;
                    flashTimer.stop();
                    timeLabel.setForeground(new JLabel().getForeground());
                    startButton.setText("Stop");
                    countdownPeriod = convertToNanoseconds();
                    stopWatch.resume();
                }
            } else {
                startButton.setText("Start");
                stopWatch.suspend();
            }
        }
        if (cmd.length() == 1) {
            if (input.size() < 6) {
                input.addLast(cmd);
                timeLabel.setText(getParsedTimeString().replaceAll("..(?!$)", "$0:"));
                updateInput();
            }
        }
        if (cmd.equals("delete")) {
            if (input.size() > 0) {
                input.removeLast();
                timeLabel.setText(getParsedTimeString().replaceAll("..(?!$)", "$0:"));
                updateInput();
            }
        }
        if (cmd.equals("reset")) {
            reset();
        }
    }

    private String formatNanoseconds(long nanos) {
        long seconds = (nanos / 1000000000) % 60;
        long minutes = (nanos / 60000000000L) % 60;
        long hours = nanos / 3600000000000L;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void updateInput() {
        String timeString = (getParsedTimeString().replaceAll(":", ""))
                .replaceAll("^0+(?=.)", "");
        input.clear();
        for (int i = 0; i < timeString.length(); i++) {
            input.addLast("" + timeString.charAt(i));
        }
    }

    private int[] convertToHMS() {
        String timeString = "";
        for (String s : input)
            timeString += s;
        while (timeString.length() < 6) {
            timeString = "0" + timeString;
        }

        int hours = Integer.parseInt(timeString.substring(0, 2));
        int minutes = Integer.parseInt(timeString.substring(2, 4));
        int seconds = Integer.parseInt(timeString.substring(4, 6));

        if (minutes >= 60) {
            hours += 1;
            minutes = minutes % 60;
        }

        if (seconds >= 60) {
            minutes += 1;
            seconds = seconds % 60;
        }

        return new int[]{hours, minutes, seconds};
    }

    private String getParsedTimeString() {
        int[] hms = convertToHMS();
        String parsedTimeString = "";
        for (int i = 0; i < 3; i++) {
            parsedTimeString += checkForLeadingZero("" + hms[i]);
        }
        return parsedTimeString;
    }

    private String checkForLeadingZero(String string) {
        if (string.length() == 1)
            return "0" + string;
        return string;
    }
}