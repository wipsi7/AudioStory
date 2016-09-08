package fi.metropolia.audiostory.visual;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import fi.metropolia.audiostory.R;

public class Feeling {

    private ImageView goodFeelingView, badFeelingView;
    private boolean isBadFeelingVisible = false;
    private Context context;
    private Animator mSetRightOut;
    private Animator mSetLeftIn;


    public Feeling(Context context, ImageView goodFeelingView, ImageView badFeelingView){
        this.goodFeelingView = goodFeelingView;
        this.badFeelingView = badFeelingView;
        this.context = context;
        loadAnimations();
        changeCameraDistance();

    }


    private void loadAnimations() {


        mSetRightOut = AnimatorInflater.loadAnimator(context.getApplicationContext(), R.animator.out_animation);
        mSetRightOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                makeViewsVisible();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideView();
            }

            //Override methods not used
            public void onAnimationCancel(Animator animation) {}
            public void onAnimationRepeat(Animator animation) {}
        });


        mSetLeftIn =  AnimatorInflater.loadAnimator(context.getApplicationContext(), R.animator.in_animation);
        mSetLeftIn.addListener(new Animator.AnimatorListener() {

            //checks if
            @Override
            public void onAnimationStart(Animator animation) {
                makeViewsVisible();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideView();
            }


            //Override methods not used
            public void onAnimationCancel(Animator animation) {}
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = context.getResources().getDisplayMetrics().density * distance;
        goodFeelingView.setCameraDistance(scale);
        badFeelingView.setCameraDistance(scale);
    }

    /** Hides not shown view */
    private void hideView() {
        if(goodFeelingView.getAlpha() == 0){
            goodFeelingView.setVisibility(View.INVISIBLE);
        }else{
            badFeelingView.setVisibility(View.INVISIBLE);
        }
    }

    /** makes views to be visible */
    private void makeViewsVisible() {
        if(goodFeelingView.getVisibility() == View.INVISIBLE){
            goodFeelingView.setVisibility(View.VISIBLE);
        }else if(badFeelingView.getVisibility() == View.INVISIBLE){
            badFeelingView.setVisibility(View.VISIBLE);
        }
    }


    /** starts animation of flipping view */
    public void flip() {
        if (!isBadFeelingVisible) {
            mSetRightOut.setTarget(goodFeelingView);
            mSetLeftIn.setTarget(badFeelingView);
            mSetRightOut.start();
            mSetLeftIn.start();
            isBadFeelingVisible = true;
        } else {
            mSetRightOut.setTarget(badFeelingView);
            mSetLeftIn.setTarget(goodFeelingView);
            mSetRightOut.start();
            mSetLeftIn.start();
            isBadFeelingVisible = false;
        }

    }


}
