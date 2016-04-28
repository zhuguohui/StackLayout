package com.zhuguohui.learn;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by yuelin on 2016/4/26.
 */
public class StackLayout extends ViewGroup implements  Observer {

    //y轴上的偏移量
    private static int mOffsetY = 50;
    //x轴的偏移量
    private static int mOffsetX = 50;
    //缩放比例
    private static float mOffsetScale = 0.01f;
    private final int mTouchSlop;
    //滑出的最低速度
    private final int mMinVelocity = 600;
    //用来记录View初始化的中心点
    private List<Point> mViewPositionList = new ArrayList<>();
    private VelocityTracker mVelocityTracker = null;
    private float mMaxVelocity;


    public StackLayout(Context context) {
        this(context, null);
    }

    public StackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取TouchSlop值
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        // mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        //应用属性
        applyAttribute(context, attrs);

    }

    private void applyAttribute(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.StackLayout);
        mOffsetY=array.getDimensionPixelSize(R.styleable.StackLayout_offsetY,50);
        mOffsetX=array.getDimensionPixelSize(R.styleable.StackLayout_offsetX,0);
        int scale=array.getInt(R.styleable.StackLayout_offseetScale,10);
        if(scale<0){
            scale=0;
        }
        if(scale>100){
            scale=100;
        }
        mOffsetScale= (float) (scale*1.0/100);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("zgh", "onMeasure被调用");
        int childcount = getChildCount();
        for (int i = 0; i < childcount; i++) {
            //测量子view大小
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i("zgh", "onLayout 被调用");
        //从中心布局
        int mWidth = getWidth();
        int mHight = getHeight();
        int left = l + mWidth / 2;
        int top = t + mHight / 2;
        int childcount = getChildCount();

        mViewPositionList.clear();
        for (int i = 0; i < childcount; i++) {
            mViewPositionList.add(new Point(0, 0));
        }
        for (int i = childcount - 1, j = 0; i >= 0; i--, j++) {
            View childView = getChildAt(i);
            int childTop = top - childView.getMeasuredHeight() / 2;
            int childLeft = left - childView.getMeasuredWidth() / 2;
            int childbuttom = childTop + childView.getMeasuredHeight();
            int childright = childLeft + childView.getMeasuredWidth();
            childView.layout(childLeft, childTop, childright, childbuttom);
            childView.setTranslationY(j * mOffsetY);
            childView.setTranslationX(j*mOffsetX);
            childView.setScaleX((float) (1 - j * mOffsetScale));
            childView.setScaleY((float) (1 - j * mOffsetScale));

            //记录view的起始位置
            Point point = mViewPositionList.get(i);
            point.set(childLeft + childView.getMeasuredWidth() / 2, childTop + childView.getMeasuredHeight() / 2);
            //设置监听


        }
    }

    int xDown = 0;
    int yDown = 0;
    int xMove = 0;
    int yMove = 0;
    int xLastMove = 0;
    int yLastMove = 0;
    int mSelectIndex = -1;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        acquireVelocityTracker(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = (int) ev.getRawX();
                yDown = (int) ev.getRawY();
                xLastMove = xDown;
                yLastMove = yDown;
                getSelectView(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = (int) ev.getRawX();
                yMove = (int) ev.getRawY();
                int dx = xMove - xDown;
                int dy = yMove - yDown;
                if (Math.sqrt(dx * dx + dy * dy) > mTouchSlop) {
                    return true;
                }
                xLastMove = xMove;
                yLastMove = yMove;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void getSelectView(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        //   Log.i("zgh","x="+x+" y="+y);
        //从顶层开始遍历
        View view = getChildAt(getChildCount() - 1);
        if (view != null) {
            int left = view.getLeft();
            int right = view.getRight();
            int top = view.getTop();
            int bottom = view.getBottom();
            if (x >= left && x <= right && y >= top && y <= bottom) {
                mSelectIndex = getChildCount() - 1;
                return;
            }
        }
        mSelectIndex = -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                xMove = (int) event.getRawX();
                yMove = (int) event.getRawY();
                int dx = xMove - xLastMove;
                int dy = yMove - yLastMove;
                updateView(dx, dy, xMove, yMove);
                xLastMove = xMove;
                yLastMove = yMove;
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                final float velocityX = clampMag(mVelocityTracker.getXVelocity(), mMinVelocity, mMaxVelocity);
                final float velocityY = clampMag(mVelocityTracker.getYVelocity(), mMinVelocity, mMaxVelocity);
                autoDismissOrRestore(velocityX, velocityY);
                releaseVelocityTracker();
                break;
        }
        return true;
    }

    private void autoDismissOrRestore(float velocityX, float velocityY) {
        if (mSelectIndex != -1) {
            boolean out = true;
            final View outView = getChildAt(mSelectIndex);
            int finalx = -1;
            int finaly = -1;
            int useTime = 0;
            int initLeft = 0;
            int initTop = 0;

            if (velocityX != 0 || velocityY != 0) {
                if (velocityX > 0) {
                    finalx = getWidth();
                } else {
                    finalx = -getWidth();
                }
                if (velocityY < 0) {
                    finaly = -getHeight();
                } else {
                    finaly = getHeight();
                }
                //计算移动距离
                int distanceX = Math.abs(outView.getLeft() - finalx);
                int distanceY = Math.abs(outView.getRight() - finaly);
                int xTime = Integer.MAX_VALUE;
                int yTime = Integer.MAX_VALUE;
                if (velocityX != 0) {
                    xTime = (int) (distanceX * 1000 / Math.abs(velocityX));
                } else {
                    yTime = (int) (distanceY * 1000 / Math.abs(velocityY));
                }
                //计算时间
                useTime = (int) Math.min(xTime, yTime);

            } else {
                //返回
                Point point = mViewPositionList.get(mSelectIndex);
                initLeft = point.x - outView.getWidth() / 2;
                initTop = point.y - outView.getHeight() / 2;
                finalx = initLeft - outView.getLeft();
                finaly = initTop - outView.getTop();
                useTime = 200;
                out = false;
            }

            if (finalx != -1 || finaly != -1) {
                final boolean finalOut = out;

                ValueAnimator animatorX = ObjectAnimator.ofInt(finalx).setDuration(Math.abs(useTime));
                animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    int lastOffset = 0;

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int offset = (int) animation.getAnimatedValue() - lastOffset;
                        outView.offsetLeftAndRight(offset);
                        lastOffset = (int) animation.getAnimatedValue();
                    }
                });
                ValueAnimator animatorY = ObjectAnimator.ofInt(finaly).setDuration(Math.abs(useTime));
                animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    int lastOffset = 0;

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int offset = (int) animation.getAnimatedValue() - lastOffset;
                        outView.offsetTopAndBottom(offset);
                        lastOffset = (int) animation.getAnimatedValue();
                    }
                });
                animatorY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (finalOut) {
                            //复用view
                            reuseView(outView);


                        } else {
                            updateViews(0);
                        }
                        mSelectIndex = -1;
                    }
                });
                AnimatorSet set = new AnimatorSet();
                set.playTogether(animatorX, animatorY);
                set.start();
            }
        }

    }

    private void reuseView(View outView) {
        //将移除的view插入到第一个，因为我们的layout是从最后开始显示的，所以第一个显示在最底层
        //此处不需要使用removeView和addView因为这两个方法会 调用 requestLayout()和invalidate(true);
        removeViewInLayout(outView);
        if (mNextPosition <= mAdapter.getCount() - 1) {
            View view=mAdapter.getView(outView,mNextPosition, StackLayout.this);
            addViewInLayout(view, 0, view.getLayoutParams(), true);
            mNextPosition++;
        }
        requestLayout();
    }

    /**
     * Clamp the magnitude of value for absMin and absMax.
     * If the value is below the minimum, it will be clamped to zero.
     * If the value is above the maximum, it will be clamped to the maximum.
     *
     * @param value  Value to clamp
     * @param absMin Absolute value of the minimum significant value to return
     * @param absMax Absolute value of the maximum value to return
     * @return The clamped value with the same sign as <code>value</code>
     */
    private float clampMag(float value, float absMin, float absMax) {
        final float absValue = Math.abs(value);
        if (absValue < absMin) return 0;
        if (absValue > absMax) return value > 0 ? absMax : -absMax;
        return value;
    }

    private void updateView(int dx, int dy, int xMove, int yMove) {
        if (mSelectIndex != -1) {
            View view = getChildAt(mSelectIndex);
            if (view == null) {
                return;
            }
            Point point = mViewPositionList.get(mSelectIndex);
            view.offsetTopAndBottom(dy);
            view.offsetLeftAndRight(dx);
            //计算新的中心的
            int centerx = view.getLeft() + view.getWidth() / 2;
            int centery = view.getTop() + view.getHeight() / 2;
            //根据位移设置alph
            //获取初始位置
            //获取中心点

            //计算偏移量
            int x = centerx - point.x;
            int y = centery - point.y;

            int distance = (int) Math.sqrt(x * x + y * y);
            //  Log.i("zgh", "distance=" + distance);
            float rate = (float) (distance * 2.0 / view.getWidth());
            updateViews(rate);
        }
    }

    private void updateViews(float rate) {
        if (rate > 1) {
            rate = 1;
        }
        //所以view向上移动
        int count = getChildCount();
        int j = 1;
        for (int i = count - 2; i >= 0; i--, j++) {
            View view = getChildAt(i);

            float scaleX = (float) (1 - mOffsetScale * j);
            float newScale = (float) (scaleX + mOffsetScale * rate);
            view.setScaleY(newScale);
            view.setScaleX(newScale);
            float translateY = (j - rate) * mOffsetY;
            float translateX = (j - rate) * mOffsetX;
            view.setTranslationY(translateY);
            view.setTranslationX(translateX);

        }
    }

    /**
     * @param event 向VelocityTracker添加MotionEvent
     * @see android.view.VelocityTracker#obtain()
     * @see android.view.VelocityTracker#addMovement(MotionEvent)
     */
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("zgh", "onDraw被调用");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.i("zgh", "dispatchDraw被调用");
    }

    /**
     * 释放VelocityTracker
     *
     * @see android.view.VelocityTracker#clear()
     * @see android.view.VelocityTracker#recycle()
     */
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }



    /***********
     * 以下代码主要是关于view复用的逻辑
     *********/
    public int mNextPosition=0;//下一个需要填充的View的position
    public BaseAdapter mAdapter;
    private int mVisibleSize = 0;

    public void setAdapter(BaseAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unRegisterObserver(this);
        }

        if (adapter == null) {
            throw new IllegalArgumentException("adapter is null");
        }

        mAdapter = adapter;
        //设置可见数量，可见数量不能比数据多
        mVisibleSize = mAdapter.getVisibleCount() > mAdapter.getCount() ? mAdapter.getCount() : mAdapter.getVisibleCount();
        adapter.registerObserver(this);
        adapter.notifyDataSetChange();
    }

    @Override
    public void update(Observable observable, Object data) {
        resetView();
    }


    /**
     * 重置状态
     */
    private void resetView() {
        //清除以后的view
        removeAllViews();
        //根据需要显示的数目创建view
        for (int i = 0; i < mVisibleSize; i++) {
            View view=mAdapter.getView(null,i,this);
            if(view!=null){
                addView(view,0);
                mNextPosition++;
            }

        }

    }


    public static abstract class BaseAdapter extends Observable {

        /**
         * 获取可见项的数量
         *
         * @return
         */
        public abstract int getVisibleCount();

        /**
         * 获取数据大小
         *
         * @return
         */
        public abstract int getCount();

        /**
         * 获取用于显示的view
         *
         * @param convertView       需要复用的view，如果第一次创建则为null
         * @param position    显示的位置
         * @param parent 父view
         * @return
         */
        public abstract View getView(View convertView, int position, StackLayout parent);
        /**
         * 发送更新
         */
        public void notifyDataSetChange() {
            setChanged();
            notifyObservers();
        }

        public void registerObserver(Observer observer) {
         addObserver(observer);
        }

        public void unRegisterObserver(Observer observer) {
            deleteObserver(observer);
        }

    }

    /*************以下代码关于飞出的逻辑**********************/

    /**
     * 从左边飞出
     * @param up 如果为true表示从坐上放飞出，否则从左下飞出
     */
    public void takeOff(boolean left,boolean up){
        if(getChildCount()!=0){
            mSelectIndex=getChildCount()-1;
            autoDismissOrRestore(left?-2000:2000,up?-2000:2000);
        }

    }


}
