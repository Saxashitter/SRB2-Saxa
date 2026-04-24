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
import android.util.LruCache;
import android.view.MotionEvent;

public class SRB2Game extends SDLActivity {
	public static MasterControls masterTouchClass;

	public static Border gameBorder;

	public static int gameWidth = 0;
	public static int gameHeight = 0;
	public static int gameDup = 0;

	private int lastLayoutWidth = 0;
	private int lastLayoutHeight = 0;

	private final Handler updateHandler = new Handler();

	public static native int nativeGetGameWidth();
	public static native int nativeGetGameHeight();
	public static native int nativeGetGameDup();

	public static native boolean nativeOnTouchDown(int id, float x, float y);
	public static native boolean nativeOnTouchUp(int id, float x, float y);

	private static LruCache<String, Bitmap> borderCache;

	static {
		// Use 1/8th of available memory for the cache
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		borderCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
	}

	public static void preloadBorderImage(final String name, final byte[] data) {
		mSingleton.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (gameBorder == null) return;

				// Check cache first
				Bitmap bitmap = borderCache.get(name);

				if (bitmap == null && data != null) {
					// Not in cache, decode it
					bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					if (bitmap != null) {
						borderCache.put(name, bitmap);
					}
				}
			}
		});
	}

	public static void updateBorderImage(final String name, final byte[] data) {
		mSingleton.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (gameBorder == null) return;

				// Check cache first
				Bitmap bitmap = borderCache.get(name);

				if (bitmap == null && data != null) {
					// Not in cache, decode it
					bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					if (bitmap != null) {
						borderCache.put(name, bitmap);
					}
				}

				if (bitmap != null) {
					gameBorder.setBorderBitmap(bitmap);
				}
			}
		});
	}

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

			int curLayoutW = mLayout != null ? mLayout.getWidth() : 0;
			int curLayoutH = mLayout != null ? mLayout.getHeight() : 0;

			if (newWidth != gameWidth || newHeight != gameHeight || newDup != gameDup
					|| curLayoutW != lastLayoutWidth || curLayoutH != lastLayoutHeight) {
				
				onGameResolutionChanged(newWidth, newHeight, newDup);

				if (curLayoutW > 0 && curLayoutH > 0 && newWidth > 0 && newHeight > 0) {
					gameWidth = newWidth;
					gameHeight = newHeight;
					gameDup = newDup;
					lastLayoutWidth = curLayoutW;
					lastLayoutHeight = curLayoutH;
				}
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

	static void resetBorder() {
		gameBorder.resetBitmap();
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
