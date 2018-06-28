package com.customwidget;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rey.material.widget.EditText;
import com.rey.material.widget.RippleManager;

/**
 * Created by ketul.patel on 12/1/16.
 */
public class materialEditText extends EditText {

    RippleManager mRippleManager;

    public materialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRippleManager = new RippleManager();





        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final MotionEvent motionEvent = MotionEvent.obtain(0, 0, 2, materialEditText.this.getWidth() / 2, materialEditText.this.getHeight() / 2, 0);
                    final Handler timerHandler = new Handler();
                    final Runnable timerRunnable = new Runnable() {
                        @Override
                        public void run() {
                            motionEvent.setAction(3);
                            mRippleManager.onTouchEvent(materialEditText.this, motionEvent);
                        }
                    };
                    timerHandler.postDelayed(timerRunnable, 100); //run every second
                    mRippleManager.onTouchEvent(materialEditText.this, motionEvent);
                }
            }
        });
    }

    public materialEditText(Context context) {
        super(context);


    }

    public materialEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


    }
}
