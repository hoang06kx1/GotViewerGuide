package co.k2lab.gotguide.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FitWidthImage extends ImageView {
	public FitWidthImage(Context context) {
        super(context);
    }

    public FitWidthImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitWidthImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    int width = MeasureSpec.getSize(widthMeasureSpec);
	    int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
	    setMeasuredDimension(width, height);
	}

}
