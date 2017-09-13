package com.example.austin.myapplication;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

/**
 * Created by hodge on 8/13/2017.
 */

public class CardTextLabelAnimator {
    private TextView textView;
    private  String[] textList = new String[] { "" };
    private int position = 0;
    private Animation fadeiInAnimation;
    private Animation textDisplayAnimation;
    private Animation delayBetweenAnimation;
    private Animation fadeOutAnimation;
    private int fadeEffectDuration;
    private int delayDuration;
    private int displayLength;

    public CardTextLabelAnimator(TextView textView, String[] textList)
    {
        this(textView,700,500,2000, textList);
    }
    private CardTextLabelAnimator(TextView textView, int fadeEffectDuration, int delayDuration, int displayLength, String[] textList)
    {
        this.textView = textView;
        this.textList = textList;
        this.fadeEffectDuration = fadeEffectDuration;
        this.delayDuration = delayDuration;
        this.displayLength = displayLength;
        SetupAnimation();
    }
    private void SetupAnimation()
    {
        fadeiInAnimation = new AlphaAnimation(0f, 1f);
        fadeiInAnimation.setDuration(fadeEffectDuration);

        textDisplayAnimation= new AlphaAnimation(1f, 1f);
        textDisplayAnimation.setDuration(displayLength);

        delayBetweenAnimation = new AlphaAnimation(0f, 0f);
        delayBetweenAnimation.setDuration(delayDuration);

        fadeOutAnimation = new AlphaAnimation(1f, 0f);
        fadeOutAnimation.setDuration(fadeEffectDuration);

        fadeiInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                position++;
                if(position>= textList.length)
                {
                    position = 0;
                }
                textView.setText(textList[position]);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                textView.startAnimation(textDisplayAnimation);
            }
        });
        textDisplayAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                textView.startAnimation(fadeOutAnimation);
            }
        });
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                textView.startAnimation(delayBetweenAnimation);
            }
        });
        delayBetweenAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                textView.startAnimation(fadeiInAnimation);
            }
        });
    }
    public void startAnimation()
    {
        textView.startAnimation(fadeOutAnimation);
    }
}
