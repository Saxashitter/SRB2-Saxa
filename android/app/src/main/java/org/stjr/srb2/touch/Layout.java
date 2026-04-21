package org.stjr.srb2.touch;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class Layout {
    protected Button[] buttons;

    // Every scheme will define its own buttons in the constructor
    public Layout() {}

    public void draw(Canvas canvas, Paint paint, Paint pressedPaint, Paint textPaint) {
        if (buttons == null) return;
        for (Button b : buttons) {
            b.draw(canvas, paint, pressedPaint, textPaint);
        }
    }

    public void handleTouch(int action, float x, float y, int id, int w, int h) {
        if (buttons == null) return;
        for (Button b : buttons) {
            b.handleTouch(action, x, y, id, w, h);
        }
    }

    public void releaseAll() {
        if (buttons == null) return;
        for (Button b : buttons) b.press(false, -1);
    }
}