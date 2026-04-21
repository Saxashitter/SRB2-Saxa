package org.stjr.srb2;

import org.libsdl.app.SDLActivity;
import org.stjr.srb2.touch.MasterControls;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

public class SRB2Game extends SDLActivity {
	public static MasterControls masterTouchClass;
	public static boolean checkPermission(String permission) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
			return true;

		Activity activity = (Activity)getContext();
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
	public static boolean inMultiWindowMode() {
		if (Build.VERSION.SDK_INT >= 24) {
            return SRB2Game.mSingleton.isInMultiWindowMode();
		}
		return false;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MasterControls masterTouchClass = new MasterControls(this);
		mLayout.addView(masterTouchClass);
	}
}
