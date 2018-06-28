package com.customwidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

/**
 * Created by ketul.patel on 23/1/16.
 */
public class CheckBoxView extends com.rey.material.widget.CheckBox {

    //attrs
    int mIndicatorId;
    int mIndicatorColor;



    public CheckBoxView(Context context) {
        super(context);
        init(null, 0);
    }

    public CheckBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CheckBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }



    private void init(AttributeSet attrs, int defStyle) {
//        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Loa);
//        mIndicatorId=a.getInt(R.styleable.LoadingIndicatorView_indicator, BallPulse);
//        mIndicatorColor=a.getColor(R.styleable.LoadingIndicatorView_indicator_color, Color.WHITE);
//        a.recycle();


    }

}
