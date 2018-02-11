package com.rafhaanshah.studyassistant;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class HelperUtils {

    private final static String LECTURE_DIRECTORY = "lectures";

    private HelperUtils() {
    }

    public static int darkenColor(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = darken(red, fraction);
        green = darken(green, fraction);
        blue = darken(blue, fraction);
        int alpha = Color.alpha(color);

        return Color.argb(alpha, red, green, blue);
    }

    private static int darken(int color, double fraction) {
        return (int) Math.max(color - (color * fraction), 0);
    }

    public static void hideSoftKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (NullPointerException E) {
            // Not a huge problem if keyboard is not hidden
        }
    }

    public static void showSoftKeyboard(Context context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (NullPointerException E) {
            // Not a huge problem if keyboard is not automatically shown
        }
    }

    public static ArrayList<File> getLectureFiles(Context context) {
        return new ArrayList<>(Arrays.asList(getLectureDirectory(context).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(MainActivity.PDF);
            }
        })));
    }

    public static File getLectureDirectory(Context context) {
        return new File(context.getFilesDir().getAbsolutePath() + File.separator + LECTURE_DIRECTORY);
    }

    public static void rotateView(View view, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ROTATION, 360);
        animator.setDuration(duration);
        animator.start();
    }

    public static void fadeOutView(View view, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0, 1);
        animator.setDuration(duration);
        animator.start();
    }

    public static void fadeInView(View view, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1, 0);
        animator.setDuration(duration);
        animator.start();
    }

    public static void fadeTextChange(final String text, final TextView textView, final int duration) {
        final AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(duration);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.setText(text);
                final AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                fadeIn.setDuration(duration);
                textView.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

        });
        textView.startAnimation(fadeOut);
    }
}