package org.stjr.srb2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class TouchControls extends View {
    private final Paint paint, pressedPaint, textPaint;

    // general variables
    private static final float finalOffset = 80f;
    private static final float finalRadius = 60;
    private static final float finalJoystickRadius = 150;
    private static final float finalJoystickSpacing = 100;
    private final TouchButton[] buttons = {
            new TouchJoystick(0f, 1f, finalJoystickRadius + finalJoystickSpacing, -finalJoystickRadius - finalJoystickSpacing, finalJoystickRadius - finalJoystickSpacing, finalJoystickSpacing, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_RIGHT),
            new TouchButton(1f, 1f, -finalOffset, -finalOffset, finalRadius, "Jump", KeyEvent.KEYCODE_SPACE)
    };

    public TouchControls(Context context) {
        super(context);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        pressedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pressedPaint.setColor(Color.RED);
        pressedPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pressedPaint.setAlpha(128);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int width = getWidth();
        int height = getHeight();

        for (int i = 0; i < event.getPointerCount(); i++) {
            float x = event.getX(i);
            float y = event.getY(i);
            int id = event.getPointerId(i);
            for (TouchButton b : buttons) {
                b.handleTouch(action, x, y, id, width, height);
            }
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (TouchButton b : buttons) b.draw(canvas, paint, pressedPaint, textPaint);
    }
}