package nodebox.client.devicehandler;

import com.google.common.collect.ImmutableList;
import nodebox.node.Device;
import oscP5.OscEventListener;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OSCDeviceHandler implements DeviceHandler {

    private String name;
    private OscP5 oscP5;
    private int oscPort;
    private boolean autostart;
    private Map<String, List<Object>> oscMessages = new HashMap<String, List<Object>>();
    private boolean paused;

    public OSCDeviceHandler(String name) {
        this(name, -1, false);
    }

    public OSCDeviceHandler(String name, int oscPort, boolean autostart) {
        this.name = name;
        this.oscPort = oscPort;
        this.autostart = autostart;
        oscP5 = null;
        oscMessages.clear();
        paused = false;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getPort() {
        return oscPort;
    }

    public boolean isAutoStart() {
        return autostart;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRunning() {
        return oscP5 != null;
    }

    public Map<String, List<Object>> getOscMessages() {
        return oscMessages;
    }

    private static int randomOSCPort() {
        return 1024 + (int) Math.round(Math.random() * 10000);
    }

    public void start() {
        if (oscP5 != null)
            stop();
        if (oscPort == -1) return;
        oscMessages.clear();
        oscP5 = new OscP5(new Object(), oscPort);
        oscP5.addListener(new OscEventListener() {
            @Override
            public void oscEvent(OscMessage m) {
                if (! isPaused()) {
                    ImmutableList<Object> arguments = ImmutableList.copyOf(m.arguments());
                    oscMessages.put(m.addrPattern(), arguments);
                }
            }

            @Override
            public void oscStatus(OscStatus ignored) {
            }
        });
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    @Override
    public void stop() {
        if (oscP5 != null)
            oscP5.stop();
        oscP5 = null;
        paused = false;
    }

    @Override
    public void addData(Map<String, Object> map) {
        map.put(getName() + ".messages", getOscMessages());
    }

    @Override
    public AbstractDeviceControl createControl() {
        return new OSCDeviceControl(this);
    }

    private class OSCDeviceControl extends AbstractDeviceControl {

        private JLabel deviceNameLabel;
        private JTextField portNumberField;
        private JCheckBox autoStartCheck;
        private JButton startButton;
        private JButton stopButton;
        private JButton clearButton;

        public OSCDeviceControl(OSCDeviceHandler deviceHandler) {
            super(deviceHandler);
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            Dimension d = new Dimension(450, 30);
            setPreferredSize(d);
            setMaximumSize(d);
            setSize(d);

            deviceNameLabel = new JLabel(deviceHandler.getName());
            portNumberField = new JTextField();
            portNumberField.setText(String.valueOf(getPort()));
            portNumberField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    changePortNumber();
                }
            }
            );
            portNumberField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent focusEvent) {
                    changePortNumber();
                }
            });
            portNumberField.setPreferredSize(new Dimension(70, portNumberField.getHeight()));
            portNumberField.setMinimumSize(new Dimension(70, portNumberField.getHeight()));

            autoStartCheck = new JCheckBox("autostart");
            autoStartCheck.setSelected(isAutoStart());
            autoStartCheck.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    autostart = autoStartCheck.isSelected();
                    setPropertyValue("autostart", String.valueOf(autostart));
                }
            });
            startButton = new JButton();

            if (isRunning()) {
                startButton.setText(isPaused() ? "Start" : "Pause");
            } else {
                startButton.setText("Start");
            }
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (! isRunning()) {
                        startOSC();
                    } else if (isPaused()) {
                        resumeOSC();
                    } else {
                        pauseOSC();
                    }
                }
            });
            stopButton = new JButton("Stop");
            stopButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    stopOSC();
                }
            });
            clearButton = new JButton("Clear");
            clearButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    oscMessages.clear();
                }
            });
            add(Box.createHorizontalStrut(10));
            add(deviceNameLabel);
            add(Box.createHorizontalStrut(5));
            add(portNumberField);
            add(Box.createHorizontalStrut(5));
            add(autoStartCheck);
            add(Box.createHorizontalStrut(5));
            add(startButton);
            add(Box.createHorizontalStrut(5));
            add(stopButton);
            add(Box.createHorizontalStrut(5));
            add(clearButton);
            add(Box.createHorizontalGlue());
        }

        private void startOSC() {
            start();
            if (isRunning())
            startButton.setText(isRunning() ? "Pause" : "Start");
        }

        private void resumeOSC() {
            resume();
            startButton.setText(isRunning() ? "Pause" : "Start");
        }

        private void pauseOSC() {
            pause();
            startButton.setText(isRunning() ? "Resume" : "Start");
        }

        private void stopOSC() {
            stop();
            startButton.setText("Start");
        }

        private void changePortNumber() {
            try {
                int newPort = Integer.parseInt(portNumberField.getText());
                stopOSC();
                oscPort = newPort;
                setPropertyValue("port", String.valueOf(newPort));
            } catch (Exception e) {
                // todo: better error handling of invalid port values
                portNumberField.setText(String.valueOf(getPort()));
                return;
            }
        }

    }

}
