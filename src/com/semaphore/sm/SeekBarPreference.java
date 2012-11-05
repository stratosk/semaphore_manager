/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 
 * Added mMin by Stratos Karafotis
 */
package com.semaphore.sm;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

    private static final String androidns = "http://schemas.android.com/apk/res/android";
    private static final String semaphorens = "http://semaphore.gr";
    private SeekBar mSeekBar;
    private TextView mSplashText, mValueText;
    private Context mContext;
    private String mDialogMessage, mSuffix;
    private int mDefault, mMax, mMin, mValue = 0;
    private int mStep = 1;

    public int getStep() {
        return mStep;
    }

    public void setStep(int Step) {
        if (Step > 0)
            this.mStep = Step;
    }

    public int getValue() {
        return mValue;
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mDialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
        mSuffix = attrs.getAttributeValue(androidns, "text");
        mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        mMax = attrs.getAttributeIntValue(androidns, "max", 100);
        mMin = attrs.getAttributeIntValue(semaphorens, "min", 0);
        mStep = attrs.getAttributeIntValue(semaphorens, "step", 1);
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 20, 6, 50);

        mSplashText = new TextView(mContext);
        if (mDialogMessage != null) {
            mSplashText.setText(mDialogMessage);
        }
        layout.addView(mSplashText);

        mValueText = new TextView(mContext);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(16);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mValueText, params);

        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if (shouldPersist()) {
            mValue = getPersistedInt(mDefault - mMin);
        }

        mSeekBar.setMax(mMax - mMin);
        mSeekBar.setProgress(mValue - mMin);
        return layout;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mSeekBar.setMax(mMax - mMin);
        mSeekBar.setProgress(mValue - mMin);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        if (restore) {
            mValue = shouldPersist() ? getPersistedInt(mDefault - mMin) : 0;
        } else {
            mValue = (Integer) defaultValue - mMin;
        }
    }

    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {

        value = ((int)Math.round(value / mStep)) * mStep;
        String t = String.valueOf(value + mMin);
        mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
//        mValue = value + mMin;
//        if (shouldPersist()) {
//            persistInt(value + mMin);
//        }
        callChangeListener(Integer.valueOf(value + mMin));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (!positiveResult) {
            return;
        }
        if (shouldPersist()) {
            mValue = ((int)Math.round(mSeekBar.getProgress() / mStep)) * mStep + mMin;
            int p = getProgress();
            persistInt(p);
            setSummary(String.valueOf(p));
        }

        notifyChanged();
    }

    public void onStartTrackingTouch(SeekBar seek) {
    }

    public void onStopTrackingTouch(SeekBar seek) {
    }

    public void setMax(int max) {
        mMax = max;
    }

    public int getMax() {
        return mMax;
    }

    public int getmMin() {
        return mMin;
    }

    public void setmMin(int mMin) {
        this.mMin = mMin;
    }

    public void setProgress(int progress) {
        mValue = progress - mMin;
        if (mSeekBar != null) {
            mSeekBar.setProgress(progress - mMin);
        }
    }

    public int getProgress() {
        return mValue;
    }
}