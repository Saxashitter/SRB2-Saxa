package org.stjr.srb2.touch;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Joystick extends Button {
    public Button up, down, left, right;
    public float spacing;
    public double joystickInputAngle = 60;

    public Joystick(float sx, float sy, float ox, float oy, float r, float spacing,
                    Integer lb, Integer db, Integer ub, Integer rb) {
        super(sx, sy, ox, oy, r, "Move", -1);
        this.spacing = spacing;
        up    = new Button(sx, sy, ox, oy - spacing, r, "U", ub);
        down  = new Button(sx, sy, ox, oy + spacing, r, "D", db);
        left  = new Button(sx, sy, ox - spacing, oy, r, "L", lb);
        right = new Button(sx, sy, ox + spacing, oy, r, "R", rb);
    }

    @Override
    public void draw(Canvas canvas, Paint paint, Paint pressedPaint, Paint textPaint) {
        float[] pos = getXYPosition(canvas.getWidth(), canvas.getHeight());
        canvas.drawCircle(pos[0], pos[1], spacing + radius, paint);
        up.draw(canvas, paint, pressedPaint, textPaint);
        down.draw(canvas, paint, pressedPaint, textPaint);
        left.draw(canvas, paint, pressedPaint, textPaint);
        right.draw(canvas, paint, pressedPaint, textPaint);
    }

    public void updateDirectionalInput(float x, float y, int width, int height, int id) {
        float[] pos = getXYPosition(width, height);
        double angle = Math.toDegrees(Math.atan2(y - pos[1], x - pos[0]));
        float dist = (float) Math.sqrt(Math.pow(x - pos[0], 2) + Math.pow(y - pos[1], 2));

        boolean deadzone = dist < (spacing / 2f);

        up.press(!deadzone && angle > (-90 - joystickInputAngle) && angle < (-90 + joystickInputAngle), id);
        down.press(!deadzone && angle > (90 - joystickInputAngle) && angle < (90 + joystickInputAngle), id);
        left.press(!deadzone && (angle > (180 - joystickInputAngle) || angle < (-180 + joystickInputAngle)), id);
        right.press(!deadzone && angle > -joystickInputAngle && angle < joystickInputAngle, id);
    }

    // TODO: make spacing NOT add onto radius. this sucks ass and causes inconsistencies with layout making
    @Override
    public boolean isPointInside(float x, float y, int w, int h) {
        float[] pos = getXYPosition(w, h);
        return Math.sqrt(Math.pow(x - pos[0], 2) + Math.pow(y - pos[1], 2)) <= (spacing + radius);
    }

    @Override
    public void press(boolean activate, int id) {
        // this doesnt actually register presses, this is only for unsticking the buttons when changed via lua
        if (activate) return;

        this.touchId = -1;
        up.press(false, -1);
        down.press(false, -1);
        left.press(false, -1);
        right.press(false, -1);
    }

    @Override
    public boolean handleTouch(int action, float x, float y, int id, int w, int h) {
        boolean inside = isPointInside(x, y, w, h);

        if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && inside) {
            this.touchId = id;
            updateDirectionalInput(x, y, w, h, id);
        } else if (action == MotionEvent.ACTION_MOVE && touchId == id) {
            updateDirectionalInput(x, y, w, h, id);
        } else if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) && touchId == id) {
            press(false, -1);
        }
        return inside;
    }
}