package org.stjr.srb2;

import org.libsdl.app.SDLActivity;
import org.stjr.srb2.touch.MasterControls;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SRB2Game extends SDLActivity {
	public static MasterControls masterTouchClass;

	public static Border gameBorder;

	public static int gameWidth = 320;
	public static int gameHeight = 200;
	public static int gameDup = 1;

	private final Handler updateHandler = new Handler();

	public static native int nativeGetGameWidth();
	public static native int nativeGetGameHeight();
	public static native int nativeGetGameDup();

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
	private final Runnable updateRunnable = new Runnable() {
		@Override
		public void run() {
			int newWidth = nativeGetGameWidth();
			int newHeight = nativeGetGameHeight();
			int newDup = nativeGetGameDup();

			if (newWidth != gameWidth || newHeight != gameHeight || newDup != gameDup) {
				gameWidth = newWidth;
				gameHeight = newHeight;
				gameDup = newDup;
				onGameResolutionChanged(gameWidth, gameHeight, gameDup);
			}

			updateHandler.postDelayed(this, 200);
		}
	};

	protected void onGameResolutionChanged(int width, int height, int dup) {
		Log.d("SRB2", "Resolution changed: " + width + "x" + height + " (dup: " + dup + ")");

		int screenW = mLayout.getWidth();
		int screenH = mLayout.getHeight();

		if (screenW == 0 || screenH == 0 || width == 0 || height == 0)
			return;

		float screenScale = Math.min(
				(float) screenW / width,
				(float) screenH / height
		);

		int rectW = (int) (width * screenScale);
		int rectH = (int) (height * screenScale);

		int left = (screenW - rectW) / 2;
		int top = (screenH - rectH) / 2;

		Rect hole = new Rect(left, top, left + rectW, top + rectH);

		if (gameBorder != null) {
			gameBorder.setHole(hole);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gameBorder = new Border(this,
				BitmapFactory.decodeResource(
						getResources(),
						R.drawable.border
				),
				new Rect(
						0,0,0,0
				)
		);
		masterTouchClass = new MasterControls(this, "FakeNothing");

		mLayout.addView(gameBorder);
		mLayout.addView(masterTouchClass);

		updateHandler.post(updateRunnable);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		updateHandler.removeCallbacks(updateRunnable);
	}
}
