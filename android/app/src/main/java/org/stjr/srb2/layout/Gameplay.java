package org.stjr.srb2.layout;

import android.view.KeyEvent;

import org.stjr.srb2.touch.Button;
import org.stjr.srb2.touch.Joystick;
import org.stjr.srb2.touch.Layout;

public class Gameplay extends Layout {
    private static final float finalOffset = 80f;
    private static final float finalRadius = 60;
    private static final float finalSpacing = 130;
    private static final float finalJoystickRadius = 150;
    private static final float finalJoystickSpacing = 100;
    private static final float screenOffset = 25;
    public Gameplay() {
        this.buttons = new Button[]{
            // First, we will create a joystick so the player can move.
            new Joystick(
                    0f, // The X scale across the screen. 0 is left, 1 is right, 0.5 is middle.
                    1f, // The Y scale across the screen. 0 is up, 1 is down, 0.5 is middle.
                    screenOffset + finalJoystickRadius, // The X offset for the button.
                    -finalJoystickRadius - screenOffset, // The Y offset for the button.
                    finalJoystickRadius - finalJoystickSpacing, // The radius of the joystick.
                    finalJoystickSpacing, // The radius of the spacing.
                    KeyEvent.KEYCODE_A,
                    KeyEvent.KEYCODE_S,
                    KeyEvent.KEYCODE_W,
                    KeyEvent.KEYCODE_D
            ),

                    // Finally, a simple button.
                    new Button(
                            1f, // The X scale across the screen.
                            1f, // The Y scale across the screen.
                            -finalOffset - screenOffset, // The X offset for the button.
                            -finalOffset - screenOffset, // The Y offset for the button.
                            finalRadius, // The width of the button.
                            "Jump",
                            KeyEvent.KEYCODE_SPACE
                    ),
                    new Button(
                            1f, // The X scale across the screen.
                            1f, // The Y scale across the screen.
                            -finalOffset - screenOffset - finalSpacing, // The X offset for the button.
                            -finalOffset - screenOffset, // The Y offset for the button.
                            finalRadius, // The width of the button.
                            "Spin",
                            KeyEvent.KEYCODE_SHIFT_LEFT
                    )
        };
    }
}
