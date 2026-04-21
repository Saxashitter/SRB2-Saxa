package org.stjr.srb2;

import org.libsdl.app.SDLActivity;

import android.os.Bundle;

public class SRB2Game extends SDLActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create your controls
		TouchControls controls = new TouchControls(this);

		// Add them to the existing SDL layout so they appear on top
		mLayout.addView(controls);
	}
}
