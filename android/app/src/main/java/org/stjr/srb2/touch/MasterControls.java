package org.stjr.srb2.touch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

// CONTROL SCHEMES
import org.stjr.srb2.layout.MenuNavigation;

import java.util.HashMap;
import java.util.Map;

public class MasterControls extends View {
    private final Paint paint, pressedPaint, textPaint;
    private Layout currentLayout; // The active behavior
    private String currentLayoutName;
    private final Map<Integer, Boolean> currentPressedIDs = new HashMap<>();
    // Initalizes controls and gets control layout.
    private int cameraId = -1;
    private float cameraX = 0;
    private float cameraY = 0;

    public MasterControls(Context context, String layoutName) {
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

        // Start with the standard layout
        setLayoutByName(layoutName);
    }

    // Used in C and during initalization.
    public void setLayoutByName(String layoutName) {
        this.currentLayoutName = layoutName;
        post(() -> {
            try {
                // Dynamically find the class in the layout package
                String className = "org.stjr.srb2.layout." + layoutName;
                Class<? extends Layout> layoutClass = Class.forName(className).asSubclass(Layout.class);

                // Instantiate it using the default constructor
                Layout newLayout = layoutClass.getDeclaredConstructor().newInstance();
                setLayout(newLayout);
            } catch (Exception e) {
                android.util.Log.e("MasterControls", "Could not find or create layout: " + layoutName, e);
            }
        });
    }

    // Used in C
    public void setControlsVisible(final boolean visible) {
        post(() -> setVisibility(visible ? VISIBLE : GONE));
    }

    // Used in C
    public boolean getControlsVisible() {
        return getVisibility() == VISIBLE;
    }

    // Used in C
    public String getLayoutName() {
        return currentLayoutName;
    }

    // Used in C and initalizing
    public void setLayout(Layout layout) {
        if (currentLayout != null) {
            currentLayout.releaseAll(); // Clean up keys before swapping
        }
        this.currentLayout = layout;
        invalidate();
    }

    // What happens on touch?
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currentLayout == null) return false;

        int action = event.getActionMasked();
        int width = getWidth();
        int height = getHeight();

        if (action == MotionEvent.ACTION_MOVE) {
            for (int i = 0; i < event.getPointerCount(); i++) {
                float x = event.getX(i);
                float y = event.getY(i);
                int id = event.getPointerId(i);

                if (id == cameraId) {
                    float offsetX = x - cameraX;
                    float offsetY = y - cameraY;

                    org.libsdl.app.SDLActivity.onNativeMouse(0, MotionEvent.ACTION_MOVE, offsetX, offsetY, true);

                    cameraX = x;
                    cameraY = y;
                } else {
                    currentLayout.handleTouch(action, x, y, id, width, height);
                }
            }
        } else {
            int idx = event.getActionIndex();
            int id = event.getPointerId(idx);
            float x = event.getX(idx);
            float y = event.getY(idx);

            if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && !org.stjr.srb2.SRB2Game.nativeOnTouchDown(id, x, y)) {
                if (currentLayout.handleTouch(action, x, y, id, width, height)) {
                    currentPressedIDs.put(id, true);
                } else if (cameraId == -1) {
                    // initialize camera
                    // this kind of implementation for mobile controls does something really weird
                    // putting a finger down on the screen with nothing else pressed lets you manipulate mouse movement
                    // we dont even have to put the effort ourselves lol
                    // except we do, when other fingers come into play
                    cameraId = id;
                    cameraX = x;
                    cameraY = y;
                    org.stjr.srb2.SRB2Game.nativeOnTouchDown(id, x, y);
                }
            } else if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) && !org.stjr.srb2.SRB2Game.nativeOnTouchUp(id, x, y)) {
                if (currentPressedIDs.containsKey(id)) {
                    currentLayout.handleTouch(action, x, y, id, width, height);
                    currentPressedIDs.remove(id);
                } else if (id == cameraId) {
                    cameraId = -1;
                }
            }
        }

        invalidate();
        return true; // ALWAYS return true so we don't lose tracking of any finger
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentLayout == null) return;
        currentLayout.draw(canvas, paint, pressedPaint, textPaint);
    }
}

//public class TouchControls extends View {
//    private final Paint paint, pressedPaint, textPaint;
//
//    // general variables
//    private final TouchButton[] buttons = {
//            // First, we will create a joystick so the player can move.
//            // Although the joystick isn't a REAL one (All the buttons on it are keys!), it is still essential to offer good move to your player.
//            // It even acts like a D-Pad if you wanna treat it like that!
//            // Button positions are done in such a weird way to intuitively design them based on any resolution without resetting their positions based on width changes.
//
//            new TouchJoystick(
//                    0f, // The X scale across the screen. 0 is left, 1 is right, 0.5 is middle.
//                    1f, // The Y scale across the screen. 0 is up, 1 is down, 0.5 is middle.
//                    finalJoystickRadius, // The X offset for the button. This has pixel positioning, so use absolute positions!
//                    -finalJoystickRadius, // The Y offset for the button. This has pixel positioning, so use absolute positions!
//                    finalJoystickRadius - finalJoystickSpacing, // The radius of the joystick. The reason why I used radius - spacing here is cause the spacing extends the radius of it.
//                    finalJoystickSpacing, // The radius of the spacing of the buttons for the joystick.
//                    KeyEvent.KEYCODE_DPAD_LEFT, // The keycode for the left button.
//                    KeyEvent.KEYCODE_DPAD_DOWN, // The keycode for the down button.
//                    KeyEvent.KEYCODE_DPAD_UP, // The keycode for the up button.
//                    KeyEvent.KEYCODE_DPAD_RIGHT // The keycode for the right button.
//            ),
//
//            // Finally, a simple button.
//            new TouchRectButton(
//                    1f, // The X scale across the screen. 0 is left, 1 is right, 0.5 is middle.
//                    1f, // The Y scale across the screen. 0 is up, 1 is down, 0.5 is middle.
//                    -finalOffset, // The X offset for the button. This has pixel positioning, so use absolute positions!
//                    -finalOffset, // The Y offset for the button. This has pixel positioning, so use absolute positions!
//                    0,
//                    0,
//                    finalRadius, // The radius of the button.
//                    finalRadius, // The radius of the button.
//                    "Jump", // The text of the button. This displays on screen, but has no other use.
//                    KeyEvent.KEYCODE_SPACE // The keycode this button uses once pressed.
//            )
//    };
//    private MotionEvent event;
//
//    public TouchControls(Context context) {
//        super(context);
//
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getActionMasked();
//        int width = getWidth();
//        int height = getHeight();
//
//        // For MOVE events, we MUST loop because actionIndex is unreliable/hardcoded to 0 by Android
//        if (action == MotionEvent.ACTION_MOVE) {
//            for (int i = 0; i < event.getPointerCount(); i++) {
//                float x = event.getX(i);
//                float y = event.getY(i);
//                int id = event.getPointerId(i);
//
//                for (TouchButton b : buttons) {
//                    // Only process the move if this button is currently tracking THIS specific ID
//                    // OR if the button is not pressed, and we want to check for a 'slide-in'
//                    if (b.touchId == id || (!b.inputDown)) {
//                        b.handleTouch(action, x, y, id, width, height);
//                    }
//                }
//            }
//        } else {
//            // For DOWN, UP, POINTER_DOWN, POINTER_UP, actionIndex IS accurate.
//            int actionIndex = event.getActionIndex();
//            float x = event.getX(actionIndex);
//            float y = event.getY(actionIndex);
//            int id = event.getPointerId(actionIndex);
//
//            for (TouchButton b : buttons) {
//                b.handleTouch(action, x, y, id, width, height);
//            }
//
//            Log.d("TouchControls", "Event: " + action + " ID: " + id + " X: " + x);
//        }
//
//        invalidate();
//        return true;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        for (TouchButton b : buttons) b.draw(canvas, paint, pressedPaint, textPaint);
//    }
//
//    public void setControlsVisible(final boolean visible) {
//        // Since this touches the UI, it's best to ensure it runs on the UI thread
//        post(new Runnable() {
//            @Override
//            public void run() {
//                setVisibility(visible ? VISIBLE : GONE);
//            }
//        });
//    }
//}