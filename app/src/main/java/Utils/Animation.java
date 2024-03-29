package Utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.ncl.team5.lloydsmockup.R;

/**
 * Created by Thanh on 22-Mar-15.
 */
public class Animation {
    public static final long LONG = 600;
    public static final long SHORT = 150;

    public static enum POST_EFFECT {PERMANENTLY, TEMPORARILY}

    ;
    //Function that creates a fade out animation used for various notifications
    public static void fade_out(final View v, Context c, long duration, final POST_EFFECT e) {
        Log.d("Animation", Long.toString(duration));
        android.view.animation.Animation a = AnimationUtils.loadAnimation(c, R.anim.anim_fade_out);
        a.setInterpolator(new DecelerateInterpolator());
        a.setDuration(duration);
        a.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                if (e == POST_EFFECT.PERMANENTLY) {
                    v.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {

            }
        });
        v.startAnimation(a);
    }
    // Function that creates a fade in animation effect for various notifications
    public static void fade_in(final View v, Context c, long duration, final POST_EFFECT e) {
        android.view.animation.Animation a = AnimationUtils.loadAnimation(c, R.anim.anim_fade_in);
        a.setInterpolator(new AccelerateInterpolator());
        a.setDuration(duration);
        a.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                if (e == POST_EFFECT.PERMANENTLY) {
                    v.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {

            }
        });
        v.startAnimation(a);
    }
}
