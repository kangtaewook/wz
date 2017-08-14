package com.vinetech.ui;


import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class RandomImageView extends FrameLayout
    implements
        ViewTreeObserver.OnGlobalLayoutListener
{

    private static final String tag = RandomImageView.class.getSimpleName();

    private static final int MAX = 100;
    private static final int IDX_X = 0;
    private static final int IDX_Y = 1;
    private static final int IDX_TXT_LENGTH = 2;
    private static final int IDX_DIS_Y = 3;
    private static final int TEXT_SIZE = 12;

    private Random random;
    private Vector<Object> vecKeywords;
    private int width;
    private int height;
    private int mode = RippleViewWithImageView.MODE_OUT;
    private int fontColor = 0xff0000ff;
    private int shadowColor = 0xdd696969;

    public boolean isShow = false;

    public Context mContext;

    public interface OnRippleViewClickListener
    {
        void onRippleViewClicked(View view, Object obj);
    }

    private OnRippleViewClickListener onRippleOutViewClickListener;

    public RandomImageView(Context context)
    {
        super(context);
        init(null, context);
    }

    public RandomImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, context);
    }

    public RandomImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    @TargetApi(21)
    public RandomImageView(Context context, AttributeSet attrs, int defStyleAttr,
                           int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, context);
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public void setOnRippleViewClickListener(OnRippleViewClickListener listener)
    {
        onRippleOutViewClickListener = listener;
    }

    /**
     * 添加RippleOutView的内容
     * 
     * @param keyword
     */
    public void addKeyWord(Object keyword)
    {
        if (vecKeywords.size() < MAX)
        {

            if (!vecKeywords.contains(keyword)) {
                vecKeywords.add(keyword);
                isShow = false;
            }
        }
    }

    public Vector<Object> getKeyWords()
    {
        return vecKeywords;
    }

    public void removeKeyWord(Object keyword)
    {
        if (vecKeywords.contains(keyword))
        {
            vecKeywords.remove(keyword);
            isShow = false;
        }
    }

    public void removeKeyWordAll(){
        vecKeywords.removeAll(vecKeywords);
        isShow = false;
    }

    private void init(AttributeSet attrs, Context context)
    {
        random = new Random();
        vecKeywords = new Vector<Object>(MAX);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        mContext = context;
    }

    @Override
    public void onGlobalLayout()
    {
        int tmpW = getWidth();
        int tmpH = getHeight();
        if (width != tmpW || height != tmpH)
        {
            width = tmpW;
            height = tmpH;
            Log.d(tag, "RandomTextView width = " + width + "; height = " + height);
        }
    }

    public void show()
    {
        this.removeAllViews();

        if (width > 0 && height > 0 && vecKeywords != null && vecKeywords.size() > 0)
        {
            //找到中心点
            int xCenter = width >> 1;
            int yCenter = height >> 1;
            //关键字的个数。
            int size = vecKeywords.size();
            int xItem = width / (size + 1);
            int yItem = height / (size + 1);
            LinkedList<Integer> listX = new LinkedList<>();
            LinkedList<Integer> listY = new LinkedList<>();
            for (int i = 0; i < size; i++)
            {
                // 准备随机候选数，分别对应x/y轴位置
                listX.add(i * xItem);
                listY.add(i * yItem + (yItem >> 2));
            }
            LinkedList<RippleViewWithImageView> listTxtTop = new LinkedList<>();
            LinkedList<RippleViewWithImageView> listTxtBottom = new LinkedList<>();

            for (int i = 0; i < size; i++)
            {
                final Object object = vecKeywords.get(i);
                // 随机颜色  
                int ranColor = fontColor;
                // 随机位置，糙值  
                int xy[] = randomXY(random, listX, listY, xItem);

                int txtSize = TEXT_SIZE;
                // 实例化RippleOutView  
                final RippleViewWithImageView txt = new RippleViewWithImageView(getContext());
                txt.setBackgroundResource(R.drawable.ic_beacon_outline);
                txt.setPadding(10,10,10,10);
                if (mode == RippleViewWithImageView.MODE_IN)
                {
                    txt.setMode(RippleViewWithImageView.MODE_IN);
                }
                else
                {
                    txt.setMode(RippleViewWithImageView.MODE_OUT);
                }

                BluetoothLeDevice ble = (BluetoothLeDevice)object;

                if(ble.getDataBeacon() != null){
                    String strUrl = Define.BASE_URL + ble.getDataBeacon().img_url;

                    Glide.with(mContext).load(strUrl)
                            .crossFade()
                            .placeholder(R.drawable.ic_beacon_image)
                            .crossFade().bitmapTransform(new CropCircleTransformation(mContext)).into(txt);


                }else{
                   txt.setImageResource(R.drawable.ic_beacon_image);
                }

                txt.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onRippleOutViewClickListener != null)
                            onRippleOutViewClickListener.onRippleViewClicked(view, object);
                    }
                });
//                txt.startRippleAnimation();

                // 获取文本长度  
                //Paint paint = txt.getPaint();
                int strWidth = /* (int) Math.ceil(paint.measureText(keyword)) */txt
                        .getMeasuredWidth();
                xy[IDX_TXT_LENGTH] = strWidth;
                // 第一次修正:修正x坐标  
                if (xy[IDX_X] + strWidth > width - (xItem/* >> 1 */))
                {
                    int baseX = width - strWidth;
                    // 减少文本右边缘一样的概率  
                    xy[IDX_X] = baseX - xItem + random.nextInt(xItem >> 1);
                }
                else if (xy[IDX_X] == 0)
                {
                    // 减少文本左边缘一样的概率  
                    xy[IDX_X] = Math.max(random.nextInt(xItem), xItem / 3);
                }
                xy[IDX_DIS_Y] = Math.abs(xy[IDX_Y] - yCenter);
                txt.setTag(xy);
                if (xy[IDX_Y] > yCenter)
                {
                    listTxtBottom.add(txt);
                }
                else
                {
                    listTxtTop.add(txt);
                }
            }

            attach2Screen(listTxtTop, xCenter, yCenter, yItem);
            attach2Screen(listTxtBottom, xCenter, yCenter, yItem);

            isShow = true;
        }
    }

    /** 修正RippleOutView的Y坐标将将其添加到容器上。 */
    private void attach2Screen(LinkedList<RippleViewWithImageView> listTxt, int xCenter, int yCenter,
            int yItem)
    {
        int size = listTxt.size();
        sortXYList(listTxt, size);
        for (int i = 0; i < size; i++)
        {
            RippleViewWithImageView txt = listTxt.get(i);
            int[] iXY = (int[]) txt.getTag();
            int yDistance = iXY[IDX_Y] - yCenter;
            int yMove = Math.abs(yDistance);
            inner : for (int k = i - 1; k >= 0; k--)
            {
                int[] kXY = (int[]) listTxt.get(k).getTag();
                int startX = kXY[IDX_X];
                int endX = startX + kXY[IDX_TXT_LENGTH];
                // y轴以中心点为分隔线，在同一侧  
                if (yDistance * (kXY[IDX_Y] - yCenter) > 0)
                {
                    if (isXMixed(startX, endX, iXY[IDX_X], iXY[IDX_X]
                        + iXY[IDX_TXT_LENGTH]))
                    {
                        int tmpMove = Math.abs(iXY[IDX_Y] - kXY[IDX_Y]);
                        if (tmpMove > yItem)
                        {
                            yMove = tmpMove;
                        }
                        else if (yMove > 0)
                        {
                            // 取消默认值。  
                            yMove = 0;
                        }
                        break inner;
                    }
                }
            }

            if (yMove > yItem)
            {
                int maxMove = yMove - yItem;
                int randomMove = random.nextInt(maxMove);
                int realMove = Math.max(randomMove, maxMove >> 1) * yDistance
                    / Math.abs(yDistance);
                iXY[IDX_Y] = iXY[IDX_Y] - realMove;
                iXY[IDX_DIS_Y] = Math.abs(iXY[IDX_Y] - yCenter);
                // 已经调整过前i个需要再次排序  
                sortXYList(listTxt, i + 1);
            }
            FrameLayout.LayoutParams layParams = new FrameLayout.LayoutParams(
            /* FrameLayout.LayoutParams.WRAP_CONTENT */200,
            /* FrameLayout.LayoutParams.WRAP_CONTENT */200);
            layParams.gravity = Gravity.LEFT | Gravity.TOP;
            layParams.leftMargin = iXY[IDX_X];
            layParams.topMargin = iXY[IDX_Y];
            addView(txt, layParams);
        }
    }

    private int[] randomXY(Random ran, LinkedList<Integer> listX,
            LinkedList<Integer> listY, int xItem)
    {
        int[] arr = new int[4];
        arr[IDX_X] = listX.remove(ran.nextInt(listX.size()));
        arr[IDX_Y] = listY.remove(ran.nextInt(listY.size()));
        return arr;
    }

    /** A线段与B线段所代表的直线在X轴映射上是否有交集。 */
    private boolean isXMixed(int startA, int endA, int startB, int endB)
    {
        boolean result = false;
        if (startB >= startA && startB <= endA)
        {
            result = true;
        }
        else if (endB >= startA && endB <= endA)
        {
            result = true;
        }
        else if (startA >= startB && startA <= endB)
        {
            result = true;
        }
        else if (endA >= startB && endA <= endB)
        {
            result = true;
        }
        return result;
    }

    /**
     * 根据与中心点的距离由近到远进行冒泡排序。
     *
     * @param endIdx 起始位置。
     * @param listTxt 待排序的数组。
     *
     */
    private void sortXYList(LinkedList<RippleViewWithImageView> listTxt, int endIdx)
    {
        for (int i = 0; i < endIdx; i++)
        {
            for (int k = i + 1; k < endIdx; k++)
            {
                if (((int[]) listTxt.get(k).getTag())[IDX_DIS_Y] < ((int[]) listTxt
                        .get(i).getTag())[IDX_DIS_Y])
                {
                    RippleViewWithImageView iTmp = listTxt.get(i);
                    RippleViewWithImageView kTmp = listTxt.get(k);
                    listTxt.set(i, kTmp);
                    listTxt.set(k, iTmp);
                }
            }
        }
    }
}
