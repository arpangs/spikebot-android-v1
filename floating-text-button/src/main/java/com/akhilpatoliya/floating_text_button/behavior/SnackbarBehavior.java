package com.akhilpatoliya.floating_text_button.behavior;

import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.akhilpatoliya.floating_text_button.FloatingTextButton;
import com.google.android.material.snackbar.Snackbar;


public class SnackbarBehavior extends CoordinatorLayout.Behavior<FloatingTextButton> {

    private static final Interpolator HIDE_INTERPOLATOR = new FastOutSlowInInterpolator();
    private static final Long HIDE_DURATION = 250L;

    private ViewPropertyAnimatorCompat animation = null;

    public SnackbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(
            CoordinatorLayout parent,
            FloatingTextButton child,
            View dependency
    ) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(
            CoordinatorLayout parent,
            FloatingTextButton child,
            View dependency
    ) {
        if (child.getTranslationY() > 0) {
            return true;
        }
        if (animation != null) {
            animation.cancel();
            animation = null;
        }

        child.setTranslationY(
                Math.min(0f, dependency.getTranslationY() - dependency.getHeight())
        );
        return true;
    }

    @Override
    public void onDependentViewRemoved(
            CoordinatorLayout parent,
            FloatingTextButton child,
            View dependency
    ) {
        if (dependency instanceof Snackbar.SnackbarLayout) {

            animation = ViewCompat.animate(child)
                    .translationY(0f)
                    .setInterpolator(HIDE_INTERPOLATOR)
                    .setDuration(HIDE_DURATION);

            animation.start();
        }
        super.onDependentViewRemoved(parent, child, dependency);
    }
}