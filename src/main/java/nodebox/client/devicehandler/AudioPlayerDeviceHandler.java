package nodebox.client.devicehandler;

import nodebox.client.FileUtils;
import nodebox.client.MinimApplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Map;

public class AudioPlayerDeviceHandler implements DeviceHandler {
    private String name;
    private String fileName;
    private boolean autostart;
    private JFrame frame = null;

    private MinimApplet applet = null;

    public AudioPlayerDeviceHandler(String name) {
        this(name, "", false);
    }

    public AudioPlayerDeviceHandler(String name, String fileName, boolean autostart) {
        this.name = name;
        this.fileName = fileName;
        this.autostart = autostart;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isAutoStart() {
        return autostart;
    }

    public void start() {
        if (frame != null) stop();
        frame = new JFrame();
        applet = new MinimApplet(getFileName(), true);
        applet.init();
        frame.add(applet);
    }

    public void stop() {
        if (frame != null) {
            applet.stop();
            applet.dispose();
            frame.dispose();
            frame = null;
        }
    }

    @Override
    public void addData(Map<String, Object> map) {
        if (applet != null && applet.getPlayer() != null)
            map.put(getName() + ".player", applet.getPlayer());
    }

    public AbstractDeviceControl createControl() {
        return new AudioPlayerDeviceControl(this);
    }

    private class AudioPlayerDeviceControl extends AbstractDeviceControl {

        private JLabel deviceNameLabel;
        private JTextField fileNameField;
        private JCheckBox autoStartCheck;
        private JButton fileButton;
        private JButton startButton;
        private JButton stopButton;

        public AudioPlayerDeviceControl(AudioPlayerDeviceHandler deviceHandler) {
            super(deviceHandler);
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            Dimension d = new Dimension(450, 60);
            setPreferredSize(d);
            setMaximumSize(d);
            setSize(d);

            deviceNameLabel = new JLabel(deviceHandler.getName());

            add(Box.createHorizontalStrut(10));
            add(deviceNameLabel);
            add(Box.createHorizontalStrut(5));

            JPanel filePanel = new JPanel();
            filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));

            fileNameField = new JTextField(100);
            fileNameField.setText(new File(getFileName()).getName());
            fileNameField.setEditable(false);
            fileNameField.setPreferredSize(new Dimension(100, fileNameField.getHeight()));

            fileButton = new JButton("File...");
            fileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    File f = FileUtils.showOpenDialog(null, getFileName(), "mp3,wav", "Music files");
                    if (f != null) {
                        setFileName(f.getAbsolutePath());
                        fileNameField.setText(new File(getFileName()).getName());
                        setPropertyValue("filename", getFileName());
                    }
                }
            });

            autoStartCheck = new JCheckBox("autostart");
            autoStartCheck.setSelected(isAutoStart());
            autoStartCheck.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    autostart = autoStartCheck.isSelected();
                    setPropertyValue("autostart", String.valueOf(autostart));
                }
            });

            filePanel.add(fileNameField);
            add(Box.createVerticalStrut(5));
            filePanel.add(fileButton);
            add(Box.createHorizontalGlue());

            startButton = new JButton("Start");
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    start();
                }
            });
            stopButton = new JButton("Stop");
            stopButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    stop();
                }
            });

            JPanel startStopPanel = new JPanel();
            startStopPanel.setLayout(new BoxLayout(startStopPanel, BoxLayout.X_AXIS));
            startStopPanel.add(autoStartCheck);
            startStopPanel.add(Box.createHorizontalStrut(5));
            startStopPanel.add(startButton);
            startStopPanel.add(Box.createHorizontalStrut(5));
            startStopPanel.add(stopButton);
            startStopPanel.add(Box.createHorizontalGlue());

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(filePanel);
            mainPanel.add(startStopPanel);

            add(mainPanel);
            add(Box.createHorizontalGlue());
        }
    }
}
