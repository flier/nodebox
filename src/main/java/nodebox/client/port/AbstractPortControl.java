package nodebox.client.port;

import nodebox.node.Port;

import javax.swing.*;

public abstract class AbstractPortControl extends JComponent implements PortControl {

    protected Port port;
    private String displayName;
    private OnValueChangeListener onValueChangeListener;

    protected AbstractPortControl(Port port) {
        this.port = port;
        displayName = port.getName();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Port getPort() {
        return port;
    }

    public void setPortValue(Object value) {
        if (onValueChangeListener != null)
            onValueChangeListener.onValueChange(this, value);
    }

    public void setValueChangeListener(OnValueChangeListener l) {
        onValueChangeListener = l;
    }

    public OnValueChangeListener getValueChangeListener() {
        return onValueChangeListener;
    }
}
