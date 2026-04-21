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
            // First, we will create a joystick so the player can move.
            // Although the joystick isn't a REAL one (All the buttons on it are keys!), it is still essential to offer good move to your player.
            // It even acts like a D-Pad if you wanna treat it like that!
            // Button positions are done in such a weird way to intuitively design them based on any resolution without resetting their positions based on width changes.

            new TouchJoystick(
                    0f, // The X scale across the screen. 0 is left, 1 is right, 0.5 is middle.
                    1f, // The Y scale across the screen. 0 is up, 1 is down, 0.5 is middle.
                    finalJoystickRadius, // The X offset for the button. This has pixel positioning, so use absolute positions!
                    -finalJoystickRadius, // The Y offset for the button. This has pixel positioning, so use absolute positions!
                    finalJoystickRadius - finalJoystickSpacing, // The radius of the joystick. The reason why I used radius - spacing here is cause the spacing extends the radius of it.
                    finalJoystickSpacing, // The radius of the spacing of the buttons for the joystick.
                    KeyEvent.KEYCODE_DPAD_LEFT, // The keycode for the left button.
                    KeyEvent.KEYCODE_DPAD_DOWN, // The keycode for the down button.
                    KeyEvent.KEYCODE_DPAD_UP, // The keycode for the up button.
                    KeyEvent.KEYCODE_DPAD_RIGHT // The keycode for the right button.
            ),

            // Finally, a simple button.
            new TouchButton(
                    1f, // The X scale across the screen. 0 is left, 1 is right, 0.5 is middle.
                    1f, // The Y scale across the screen. 0 is up, 1 is down, 0.5 is middle.
                    -finalOffset, // The X offset for the button. This has pixel positioning, so use absolute positions!
                    -finalOffset, // The Y offset for the button. This has pixel positioning, so use absolute positions!
                    finalRadius, // The radius of the button.
                    "Jump", // The text of the button. This displays on screen, but has no other use.
                    KeyEvent.KEYCODE_SPACE // The keycode this button uses once pressed.
            )
    };
    private MotionEvent event;

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

        // Loop through every finger currently touching the screen
        for (int i = 0; i < event.getPointerCount(); i++) {
            float x = event.getX(i);
            float y = event.getY(i);
            int id = event.getPointerId(i);

            // For DOWN or UP events, Android only cares about the finger that just changed.
            // We filter that finger here.
            int actionIndex = event.getActionIndex();

            if (action == MotionEvent.ACTION_MOVE) {
                // Move events apply to all pointers
                for (TouchButton b : buttons) {
                    b.handleTouch(action, x, y, id, width, height);
                }
            } else if (i == actionIndex) {
                // Down/Up/PointerDown/PointerUp only apply to the finger at the action index
                for (TouchButton b : buttons) {
                    b.handleTouch(action, x, y, id, width, height);
                }
            }
        }

        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (TouchButton b : buttons) b.draw(canvas, paint, pressedPaint, textPaint);
    }

    public void setControlsVisible(final boolean visible) {
        // Since this touches the UI, it's best to ensure it runs on the UI thread
        post(new Runnable() {
            @Override
            public void run() {
                setVisibility(visible ? VISIBLE : GONE);
            }
        });
    }
}