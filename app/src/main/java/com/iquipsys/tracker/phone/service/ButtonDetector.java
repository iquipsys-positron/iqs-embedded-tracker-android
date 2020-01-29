package com.iquipsys.tracker.phone.service;

import android.content.Context;
import com.iquipsys.tracker.phone.status.ButtonPress;

public class ButtonDetector {
    private int _buttonPress = ButtonPress.NONE;

    private ButtonDetectorListener _listener;

    public interface ButtonDetectorListener {
        void onButtonPressed(int buttonPress);
    }

    public ButtonDetector(ButtonDetectorListener listener) {
        _listener = listener;
    }

    public void subscribe(Context context) {}

    public void unsubscribe(Context context) {}

    public void pressButton(boolean longPress) {
        _buttonPress = longPress ? ButtonPress.LONG : ButtonPress.SHORT;

        if (_listener != null) {
            _listener.onButtonPressed(_buttonPress);
        }
    }

    public int getButtonPress() {
        return _buttonPress;
    }

    public int readButtonPress() {
        int result = _buttonPress;
        _buttonPress = ButtonPress.NONE;
        return result;
    }

    public boolean isLongPressed() {
        return _buttonPress == ButtonPress.LONG;
    }

    public boolean isPressed() {
        return _buttonPress == ButtonPress.SHORT;
    }

    public void clearButtonPress() {
        _buttonPress = ButtonPress.NONE;
    }

}
