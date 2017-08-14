package com.vinetech.ui;

/**
 * Created by galuster on 2017-02-14.
 */

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Region;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import com.vinetech.wezone.R;

import java.util.concurrent.TimeUnit;

public class RangeSliderView extends View{

    private static final String TAG = RangeSliderView.class.getSimpleName();

    private static final long RIPPLE_ANIMATION_DURATION_MS = TimeUnit.MILLISECONDS.toMillis(700);

    private static final int DEFAULT_PAINT_STROKE_WIDTH = 5;

    private static final int DEFAULT_WHITE_COLOR = Color.parseColor("#00ff0000");

    private static final int DEFAULT_FILLED_COLOR = Color.parseColor("#87CEFA"); // blue

    private static final int DEFAULT_EMPTY_COLOR = Color.parseColor("#C3C3C3"); //빨강?

    private static final int DEFAULT_EMPTYZ_COLOR = Color.parseColor("#7F7F7F"); // 진한 회색

    private static final int DEFAULT_FILLED2_COLOR = Color.parseColor("#00AAAA"); // 진한 blue

    private static final float DEFAULT_BAR_HEIGHT_PERCENT = 0.10f;

    private static final float DEFAULT_SLOT_RADIUS_PERCENT = 0.125f;

    private static final float DEFAULT_SLIDER_RADIUS_PERCENT = 0.25f;

    private static final int DEFAULT_RANGE_COUNT = 4;

    private static final int DEFAULT_HEIGHT_IN_DP = 50;

    private static final int DEFAULT_MAX_VALUE = 100;

    private int default_value = DEFAULT_MAX_VALUE;  // 간격

    protected Paint paint;

    protected Paint ripplePaint;

    protected float radius;

    protected float slotRadius;

    private int currentIndex;

    private float currentSlidingX;

    private float currentSlidingY;

    private float selectedSlotX;

    private float selectedSlotY;

    private boolean gotSlot = false;

    private float[] slotPositions;

    private int filledColor = DEFAULT_FILLED_COLOR;

    private int whiteColor = DEFAULT_WHITE_COLOR;

    private int emptyColor = DEFAULT_EMPTY_COLOR;

    private int emptyColorZ = DEFAULT_EMPTYZ_COLOR;

    private int filledColorZ =  DEFAULT_FILLED2_COLOR;

    private float barHeightPercent = DEFAULT_BAR_HEIGHT_PERCENT;

    private int rangeCount = DEFAULT_RANGE_COUNT;

    private int barHeight;

    private OnSlideListener listener;

    private float rippleRadius = 0.0f;

    private float downX;

    private float downY;

    private Path innerPath = new Path();

    private Path outerPath = new Path();

    private float slotRadiusPercent = DEFAULT_SLOT_RADIUS_PERCENT;

    private float sliderRadiusPercent = DEFAULT_SLIDER_RADIUS_PERCENT;


    private int layoutHeight;


    public RangeSliderView(Context context) {
        this(context, null);
    }

    public RangeSliderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RangeSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RangeSliderView);
            TypedArray sa = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.layout_height});
            try {

                rangeCount = a.getInt(
                        R.styleable.RangeSliderView_rangeCount, DEFAULT_RANGE_COUNT);
                filledColor = a.getColor(
                        R.styleable.RangeSliderView_filledColor, DEFAULT_FILLED_COLOR);
                emptyColor = a.getColor(
                        R.styleable.RangeSliderView_emptyColor, DEFAULT_EMPTY_COLOR);
                barHeightPercent = a.getFloat(
                        R.styleable.RangeSliderView_barHeightPercent, DEFAULT_BAR_HEIGHT_PERCENT);
                barHeightPercent = a.getFloat(
                        R.styleable.RangeSliderView_barHeightPercent, DEFAULT_BAR_HEIGHT_PERCENT);
                slotRadiusPercent = a.getFloat(
                        R.styleable.RangeSliderView_slotRadiusPercent, DEFAULT_SLOT_RADIUS_PERCENT);
                sliderRadiusPercent = a.getFloat(
                        R.styleable.RangeSliderView_sliderRadiusPercent, DEFAULT_SLIDER_RADIUS_PERCENT);
            } finally {
                a.recycle();
                sa.recycle();
            }
        }

        setBarHeightPercent(barHeightPercent);
        setRangeCount(rangeCount);
        setSlotRadiusPercent(slotRadiusPercent);
        setSliderRadiusPercent(sliderRadiusPercent);

        slotPositions = new float[rangeCount];
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(DEFAULT_PAINT_STROKE_WIDTH);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint.setStrokeWidth(2.0f);
        ripplePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //OnPreDrawListener() 뷰가 그려지기 전 호출
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);

                // Update radius after we got new height
                updateRadius(getHeight());

                // Compute drawing position again
                preComputeDrawingPosition();

                // Ready to draw now
                return true;
            }
        });




    }



    //@AnimateMethod
    public void setRadius(final float radius) {
        rippleRadius = radius;
        if (rippleRadius > 0) {
            RadialGradient radialGradient = new RadialGradient(
                    downX,
                    downY,
                    rippleRadius * 3,
                    Color.TRANSPARENT,
                    Color.BLACK,
                    Shader.TileMode.MIRROR
            );
            ripplePaint.setShader(radialGradient);
        }
        invalidate();
    }

    public void setDefaultValue(int value){
        default_value = value;
    }







    public int getHeightWithPadding() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    public int getWidthWithPadding() {
        // return getWidth() - getPaddingLeft() - getPaddingRight();

        return getWidth();
    }


    private void drawBar(Canvas canvas, int from, int to, int color) {
        paint.setColor(color);
        int h = getHeightWithPadding(); //h 150
        int half = (barHeight >> 1); // barHeight 15    from 135, to 945, half = 7
        int y = getPaddingTop() + (h >> 1); // y=75
        canvas.drawRect(from, y - half, to, y + half, paint); // x 와 y 설정은 bar 두깨
    }

    private void preComputeDrawingPosition() {
        int w = getWidthWithPadding(); // w 1080

        int h = getHeightWithPadding();  // h 150

        /** Space between each slot **/
        int spacing = w / rangeCount; // spacing 270

        /** Center vertical */
        int y = getPaddingTop() + h / 2;
        currentSlidingY = y;   // 75
        selectedSlotY = y;    // 75
        /**
         * Try to center it, so start by half
         * <pre>
         *
         *  Example for 4 slots
         *
         *  ____o____|____o____|____o____|____o____
         *  --space--
         *
         * </pre>
         */
        int x = getPaddingLeft() + (spacing / 2);

        /** Store the position of each slot index */
        for (int i = 0; i < rangeCount; ++i) {
            slotPositions[i] = x;
            if (i == currentIndex) {
                currentSlidingX = x;  // 135
                Log.d("x","x["+x+"]");
                selectedSlotX = x; // 135
            }
            x += spacing;
        }
    }



    private void updateRadius(int height) {
        barHeight = (int) (height * barHeightPercent);
        radius = height * sliderRadiusPercent;
        slotRadius = height * slotRadiusPercent;
    }

    public int getRangeCount() {
        return rangeCount;
    }

    public void setRangeCount(int rangeCount) {
        if (rangeCount < 2) {
            throw new IllegalArgumentException("rangeCount must be >= 2");
        }
        this.rangeCount = rangeCount;
    }

    public void setBarHeightPercent(float percent) {
        if (percent <= 0.0 || percent > 1.0) {
            throw new IllegalArgumentException("Bar height percent must be in (0, 1]");
        }
        this.barHeightPercent = percent;
    }

    public float getSlotRadiusPercent() {
        return slotRadiusPercent;
    }

    public void setSlotRadiusPercent(float percent) {
        if (percent <= 0.0 || percent > 1.0) {
            throw new IllegalArgumentException("Slot radius percent must be in (0, 1]");
        }
        this.slotRadiusPercent = percent;
    }

    public float getSliderRadiusPercent() {
        return sliderRadiusPercent;
    }

    public void setSliderRadiusPercent(float percent) {
        if (percent <= 0.0 || percent > 1.0) {
            throw new IllegalArgumentException("Slider radius percent must be in (0, 1]");
        }
        this.sliderRadiusPercent = percent;
    }



    public void setInitialIndex(int index) {
        if (index < 0 || index >= rangeCount) {
            throw new IllegalArgumentException("Attempted to set index=" + index + " out of range [0," + rangeCount + "]");
        }
        currentIndex = index;
        currentSlidingX = selectedSlotX = slotPositions[currentIndex];
        invalidate();
    }

    public int getFilledColor() {
        return filledColor;
    }

    public void setFilledColor(int filledColor) {
        this.filledColor = filledColor;
        invalidate();
    }

    public int getEmptyColor() {
        return emptyColor;
    }

    public void setEmptyColor(int emptyColor) {
        this.emptyColor = emptyColor;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * Measures height according to the passed measure spec
     *
     * @param measureSpec int measure spec to use
     * @return int pixel size
     */
    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            final int height;
            if (layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = dpToPx(getContext(), DEFAULT_HEIGHT_IN_DP);
            } else if (layoutHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                height = getMeasuredHeight();
            } else {
                height = layoutHeight;
            }
            result = height + getPaddingTop() + getPaddingBottom() + (2 * DEFAULT_PAINT_STROKE_WIDTH);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = specSize + getPaddingLeft() + getPaddingRight() + (2 * DEFAULT_PAINT_STROKE_WIDTH) + (int) (2 * radius);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    static int dpToPx(final Context context, final float dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }

    Canvas canvas;
    private void updateCurrentIndex() {
        float min = Float.MAX_VALUE;
        int j = 0;
        /** Find the closest to x */
        for (int i = 0; i < rangeCount; ++i) {
            float dx = Math.abs(currentSlidingX - slotPositions[i]);
            if (dx < min) {
                min = dx;
                j = i;
            }
        }
        /** This is current index of slider */
        if(j == 0){
            currentIndex = 1;
        }
        if (j != currentIndex) {
            if (listener != null) {
                listener.onSlide(j);
            }
        }





        currentIndex = j;
        /** Correct position */
        currentSlidingX = slotPositions[j];
        selectedSlotX = currentSlidingX;
        downX = currentSlidingX;
        downY = currentSlidingY;
        animateRipple();
        invalidate();
    }

    private void animateRipple() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "radius", 0, radius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(RIPPLE_ANIMATION_DURATION_MS);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                rippleRadius = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }



    private boolean isInSelectedSlot(float x, float y) {
        Log.d("Select","selectSlotX["+selectedSlotX+"] radius["+radius+"]");
        return
                selectedSlotX - radius <= x && x <= selectedSlotX + radius &&
                        selectedSlotY - radius <= y && y <= selectedSlotY + radius;


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        float x = event.getX();
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                gotSlot = isInSelectedSlot(x, y);
                downX = x;
                downY = y;

                Log.d("touch","downX["+downX+"]");

                if(true) {
                    gotSlot=true;
                    Log.d("Select","gotSlot["+gotSlot+"]");
                    if (x >= slotPositions[0]-30 && x <= slotPositions[rangeCount - 1]) {
                        currentSlidingX = downX;
                        currentSlidingY = downY;

                        listener.onChangeSlide((int) currentSlidingX);
                        invalidate();
                    }

                }

                break;

            case MotionEvent.ACTION_MOVE: {

                if(listener != null){
                    int realWidth =     (int)slotPositions[3]-(int)slotPositions[0];  //getWidth() - getPaddingLeft() - getPaddingRight();
                    double ratio = (double)default_value / (double)realWidth ;
                    double realValue = x * ratio;
                    //Log.d("TEST","realWidth["+realWidth+"] ratio["+ratio+"] realValue["+realValue+"] x["+x+"]");
                    //listener.onChangeSlide((int)realValue);
                }

                if (gotSlot) {

                    if (x >= slotPositions[0] && x <= slotPositions[rangeCount - 1]) {
                        currentSlidingX = x;
                        currentSlidingY = y;
                        //Log.d("TEST","currentSlidingX["+currentSlidingX+"] currentSlidingY["+currentSlidingY+"]");

                        if(listener != null){
                            listener.onChangeSlide((int)currentSlidingX);
                            invalidate();
                        }
                    }
                }
            }
            break;

            case MotionEvent.ACTION_UP:
                if (gotSlot) {
                    gotSlot = false;
                    currentSlidingX = x;
                    currentSlidingY = y;
                    updateCurrentIndex();
                }
                break;
        }
        return true;
    }



    public interface OnSlideListener {

        /**
         * Notify when slider change to new index position
         *
         * @param index The index value of range count [0, rangeCount - 1]
         */

        void onSlide(int index);

        void onChangeSlide(int value);

    }

    public void setOnSlideListener(OnSlideListener listener) {

        this.listener = listener;


    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.saveIndex = this.currentIndex;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.currentIndex = ss.saveIndex;
    }

    static class SavedState extends BaseSavedState {
        int saveIndex;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.saveIndex = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.saveIndex);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidthWithPadding(); // w 1080
        int h = getHeightWithPadding(); // h 150
        int spacing = w / rangeCount;  // rangeCount 4, spacing 270
        int border = (spacing >> 1);  // border 135
        int x0 = getPaddingLeft() + border; // x 135
        int y0 = getPaddingTop() + (h >> 1); // y 75
        paint.setStrokeWidth(1f);


        /** Draw empty bar */
        drawBar(canvas,0, getWidth(), emptyColor); //회색

        drawBar(canvas,0, (int)slotPositions[0], filledColor);


        /** Draw filled bar */
        drawBar(canvas, x0, (int) currentSlidingX, filledColor);
        Log.d("currentSlidingX","currentSlidingX["+currentSlidingX+"]");




        drawEmptySlots2(canvas);
        drawEmptySlots(canvas);  //회색 원?
        drawFilledSlots(canvas); //주황 원


        String str = "100m";





        /** Draw the selected range circle */
        paint.setColor(filledColorZ);
        canvas.drawCircle(currentSlidingX, y0, slotRadius, paint); // CurrentSlidingX = 135.0, y0 = 75, radius = 37.5,    (x,y) 좌표 에서 반지름 37.5 만큼 원을 그린다.
        drawRippleEffect(canvas);
    }


    private void drawEmptySlots(Canvas canvas) {  // 회색 원
        String str;
        paint.setStrokeWidth(1f);
        paint.setColor(emptyColorZ);
        paint.setTextSize(40);
        int h = getHeightWithPadding();
        int y = getPaddingTop() + (h >> 1);
        for (int i = 0; i < rangeCount; ++i) {
            canvas.drawCircle(slotPositions[i], y, slotRadius, paint);


            switch (i){
                case 1:
                    str = "400m";
                    break;
                case 2:
                    str = "1.6km";
                    break;
                case 3:
                    str = "3.2km";
                    break;
                default:
                    str = "200m";
                    break;
            }
            canvas.drawText(str,slotPositions[i]-(radius),y+((slotRadius*2)*2)-5 ,paint);


        }
    }


    private void drawFilledSlots(Canvas canvas) { // blue
        String str;
        paint.setColor(filledColorZ);
        paint.setStrokeWidth(1f);
        //paint.setTextSize(30);
        int h = getHeightWithPadding();
        int y = getPaddingTop() + (h >> 1);
        for (int i = 0; i < rangeCount; ++i) {
            if (slotPositions[i] <= currentSlidingX) {
                canvas.drawCircle(slotPositions[i], y, slotRadius, paint);

                switch (i){
                    case 1:
                        str = "400m";
                        break;
                    case 2:
                        str = "1.6km";
                        break;
                    case 3:
                        str = "3.2km";
                        break;
                    default:
                        str = "200m";
                        break;
                }
                canvas.drawText(str,slotPositions[i]-(radius),y+((slotRadius*2)*2)-5 ,paint);  // 수정
            }

        }
    }

    private void drawEmptySlots2(Canvas canvas) {  // 흰색 원
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1f);
        int h = getHeightWithPadding();
        int y = getPaddingTop() + (h >> 1);
        for (int i = 0; i < rangeCount; ++i) {
            canvas.drawCircle(slotPositions[i], y, slotRadius*2, paint);



        }
    }





    private void drawRippleEffect(Canvas canvas) {
        if (rippleRadius != 0) {
            paint.setStrokeWidth(1f);
            canvas.save();
            ripplePaint.setColor(Color.GRAY);
            outerPath.reset();
            outerPath.addCircle(downX, downY, rippleRadius, Path.Direction.CW);
            canvas.clipPath(outerPath);
            innerPath.reset();
            innerPath.addCircle(downX, downY, rippleRadius / 3, Path.Direction.CW);
            canvas.clipPath(innerPath, Region.Op.DIFFERENCE);
            canvas.drawCircle(downX, downY, rippleRadius, ripplePaint); // 0, 0, 0, 0
            canvas.restore();
        }
    }


    public void setSelectedSlot(int value){

        currentSlidingX = slotPositions[value];
        invalidate();
    }
}
