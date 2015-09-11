package com.pd.noteonthego.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pd.noteonthego.R;

/**
 * This is a custom layout for list row with a checkable option
 * Created by pradey on 9/8/2015.
 */
public class OneLineCheckableListItem extends RelativeLayout implements Checkable{

    public OneLineCheckableListItem(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    private boolean checked;

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;

        ImageView iv = (ImageView) findViewById(R.id.SelectImageView);
        iv.setImageResource(checked ? R.drawable.ic_toggle_check_box : R.drawable.ic_toggle_check_box_outline_blank);

    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        this.checked = !this.checked;
    }
}
