package com.pixelnx.eacademy.utils.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CustomTextSemiBold extends TextView {
    public CustomTextSemiBold(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomTextSemiBold(Context context,  @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomTextSemiBold(Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    public CustomTextSemiBold(Context context,  @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyCustomFont(context);
    }
    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("Poppins-SemiBold.ttf", context);
        setTypeface(customFont);
        setTextSize(14);

    }



}
