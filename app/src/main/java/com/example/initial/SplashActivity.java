package com.example.initial;

import android.animation.ArgbEvaluator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.animation.AccelerateDecelerateInterpolator;

public class SplashActivity extends AppCompatActivity {

    private TextView appNameTextView;
    private static final String APP_NAME = "Style Stack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appNameTextView = findViewById(R.id.appNameTextView);
        animateAppName();

        // Wait for animation to complete, then start main activity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, 3000); // Adjust delay as needed
    }

    private void animateAppName() {
        ValueAnimator animator = ValueAnimator.ofInt(1, APP_NAME.length());
        animator.setDuration(2000); // Duration of the animation
        // Animator for color transition
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.BLUE, Color.RED);
        colorAnimator.setDuration(2000);
        // Set the AccelerateDecelerateInterpolator
        animator.setInterpolator(new TimeInterpolator() {
            private final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

            @Override
            public float getInterpolation(float input) {
                return interpolator.getInterpolation(input);
            }
        });
//        colorAnimator.setInterpolator(new TimeInterpolator() {
//            private final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
//
//            @Override
//            public float getInterpolation(float input) {
//                return interpolator.getInterpolation(input);
//            }
//        });
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            appNameTextView.setText(APP_NAME.substring(0, animatedValue));

            // Update color
            int color = (int) colorAnimator.getAnimatedValue();
            appNameTextView.setTextColor(color);
        });
        colorAnimator.start();
        animator.start();
    }
}
