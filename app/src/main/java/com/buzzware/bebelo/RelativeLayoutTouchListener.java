package com.buzzware.bebelo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.buzzware.bebelo.Activities.BarDetail;
import com.buzzware.bebelo.Activities.Home;
import com.buzzware.bebelo.Fragments.ExploreFragment;
import com.buzzware.bebelo.eventBusModel.MessageEvent;

import org.greenrobot.eventbus.EventBus;

public class RelativeLayoutTouchListener implements View.OnTouchListener {

    static final String logTag = "ActivitySwipeDetector";
    private Fragment activity;
    static final int MIN_DISTANCE = 150;// TODO change this runtime based on screen resolution. for 1920x1080 is to small the 100 distance
    private float downX, downY, upX, upY;

    // private MainActivity mMainActivity;

    public RelativeLayoutTouchListener(ExploreFragment mainActivity) {
        activity = mainActivity;
    }

    public void onRightToLeftSwipe() {
        Log.i(logTag, "RightToLeftSwipe!");

    }

    public void onLeftToRightSwipe() {
        Log.i(logTag, "LeftToRightSwipe!");
    }

    public void onTopToBottomSwipe() {
        Log.i(logTag, "onTopToBottomSwipe!");
    }

    public void onBottomToTopSwipe() {
        EventBus.getDefault().post(new MessageEvent(true));
        Log.i(logTag, "onBottomToTopSwipe!");
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true;
                    }
                } else {
                    Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long horizontally, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                // swipe vertical?
                if (Math.abs(deltaY) >= MIN_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                    if (deltaY >= 0) {
                        this.onBottomToTopSwipe();
                        return true;
                    }
                } else {
                    Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long vertically, need at least " + MIN_DISTANCE);

                    EventBus.getDefault().post(new MessageEvent(true));
                    // return false; // We don't consume the event
                }

                return false; // no swipe horizontally and no swipe vertically
            }// case MotionEvent.ACTION_UP:
        }
        return false;
    }

}