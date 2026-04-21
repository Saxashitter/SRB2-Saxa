package org.stjr.srb2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import org.libsdl.app.SDLActivity;

public class TouchButton {
    // Shared KeyMap for all buttons

    public float scaleX, scaleY, offsetX, offsetY, radius;
    public String name;
    public Integer bind;
    public boolean inputDown;
    public int touchId = -1;

    public TouchButton(float sx, float sy, float ox, float oy, float r, String n, Integer b) {
        this.scaleX = sx;
        this.scaleY = sy;
        this.offsetX = ox;
        this.offsetY = oy;
        this.radius = r;
        this.name = n;
        this.bind = b;
    }

    public float[] getXYPosition(int width, int height) {
        return new float[]{ (width * scaleX) + offsetX, (height * scaleY) + offsetY };
    }

    public boolean isPointInside(float x, float y, int width, int height) {
        float[] pos = getXYPosition(width, height);
        float dx = x - pos[0];
        float dy = y - pos[1];
        return (dx * dx + dy * dy) <= (radius * radius);
    }

    public void press(boolean activate, int id) {
        if (this.inputDown == activate) return; // Prevent spam
        this.inputDown = activate;
        this.touchId = activate ? id : -1;

        Integer keyCode = this.bind;
        if (keyCode != null) {
            if (activate) SDLActivity.onNativeKeyDown(keyCode);
            else SDLActivity.onNativeKeyUp(keyCode);
        }
    }

    public void handleTouch(int action, float x, float y, int id, int w, int h) {
        boolean inside = isPointInside(x, y, w, h);

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            if (inside) {
                press(true, id);
            }
        }
        else if (action == MotionEvent.ACTION_MOVE) {
           if (this.touchId == -1 && inside && !this.inputDown) {
                // Slide-to-activate
                press(true, id);
            }
        }
        else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {
            if (this.touchId == id) {
                press(false, -1);
            }
        }
    }

    public void draw(Canvas canvas, Paint paint, Paint pressedPaint, Paint textPaint) {
        float[] pos = getXYPosition(canvas.getWidth(), canvas.getHeight());
        canvas.drawCircle(pos[0], pos[1], radius, inputDown ? pressedPaint : paint);
        float textY = pos[1] - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(name, pos[0], textY, textPaint);
    }
}