package com.read.dream.readboybox.widget.timeSelect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.read.dream.readboybox.R;
import com.read.dream.readboybox.bean.SelectBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fan.feng on 2018/10/17.
 */

public class TimeSelectorView extends View {

    private static final int NONE = -3;
    private static final int AM = -2;
    private static final int PM = -1;

    private Paint mPaint;
    private Paint mTextPaint;

    //选中颜色
    private int selectedBgColor;
    //未选中颜色
    private int unSelectedBgColor;
    //边框颜色
    private int borderColor;
    //边框宽度
    private float borderWidth;

    //文本颜色
    private int selectedTextColor;

    private SelectBean selectBean;
    private int unSelectedTextColor;
    private float textSize;

    private int width;
    private int height;

    private int row = 4;
    private int rol = 7;

    private boolean isSelect = false;

    private List<Integer> selectedList;
    private float childerWidth;
    private float childerHeight;

    //最后触摸位置
    private int lastTouchPosition = NONE;

    public TimeSelectorView(Context context) {
        this(context, null);
    }

    public TimeSelectorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeSelectorView);
        //获取自定义属性和默认值

        //文本相关
        selectedTextColor = mTypedArray.getColor(R.styleable.TimeSelectorView_TSTextSelectedColor, 0xFFFFFFFF);
        unSelectedTextColor = mTypedArray.getColor(R.styleable.TimeSelectorView_TSTextUnSelectedColor, 0xFF333333);
        textSize = mTypedArray.getDimension(R.styleable.TimeSelectorView_TSTextSize, 16);

        //背景相关
        selectedBgColor = mTypedArray.getColor(R.styleable.TimeSelectorView_TSSelectedBackgroundColor, 0xFF3AA6E8);
        unSelectedBgColor = mTypedArray.getColor(R.styleable.TimeSelectorView_TSUnSelectedBackgroundColor, 0xFFECECEC);

        //边框相关
        borderColor = mTypedArray.getColor(R.styleable.TimeSelectorView_TSBorderColor, 0xFFDDDDDD);
        borderWidth = mTypedArray.getDimension(R.styleable.TimeSelectorView_TSBorderWidth, 1);

        mTypedArray.recycle();

        mPaint = new Paint();
        mTextPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        childerWidth = (float)width / rol;
        childerHeight = (float)height / row;

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < rol; j++) {

                int drawPosition = NONE;

                //在这里固定出AM / PM的位置
                if(j == 0){
                    if(i < 2){
                        drawPosition = AM;
                    }else{
                        drawPosition = PM;
                    }

                }else{
                    //todo 这里是计算出时间点，比如第一行从0：00开始  第二行从12：00开始
                    if(i == 0 || i == 1 ){
                        drawPosition = j - 1 + i *6;
                    } else {
                        drawPosition = j + 11 + 6*(i-2);
                    }
                }

                if(drawPosition >= 0){

                    boolean isContains = false;

                    if(selectedList != null && selectedList.contains(new Integer(drawPosition))){
                        isContains = true;
                    }

                    //如果当前色块选中的
                    if(isContains){
                        //设置未选中背景
                        mPaint.setColor(selectedBgColor);
                        mTextPaint.setColor(selectedTextColor);
                    }else{
                        //设置未选中背景
                        mPaint.setColor(unSelectedBgColor);
                        mTextPaint.setColor(unSelectedTextColor);
                    }
                }else{
                    //设置未选中背景
                    mPaint.setColor(unSelectedBgColor);
                    mTextPaint.setColor(unSelectedTextColor);
                }

                @SuppressLint("DrawAllocation")
                RectF rectF = new RectF(j * childerWidth, i * childerHeight, (j + 1)* childerWidth , (i + 1) * childerHeight);

                //画背景块i
                canvas.drawRect(rectF, mPaint);

                if(null != selectBean && i== selectBean.getRow() && j == selectBean.getRol()){
                    RectF rectF1 = new RectF(j* childerWidth, i* childerHeight,
                            (j + selectBean.getPercent())* childerWidth ,  (i + 1) * childerHeight);
                    mPaint.setColor(selectedBgColor);
                    canvas.drawRect(rectF1, mPaint);
                    selectBean = null;
                }

                //画文字
                mTextPaint.setTextSize(textSize);

                //计算baseline
                Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
                float baseline = rectF.centerY() + distance;
                String drawText = getTimeForNum(drawPosition);
                canvas.drawText(drawText, rectF.centerX() - mTextPaint.measureText(drawText) / 2 , baseline, mTextPaint);
            }
        }

        mPaint.setColor(borderColor);

        //画竖线
        for (int j = 1; j < rol; j++) {
            canvas.drawRect(j * childerWidth - borderWidth / 2, 0, j * childerWidth + borderWidth / 2, height, mPaint);
        }

        //画横线
        canvas.drawRect(0, height / 2 - borderWidth / 2, width, height / 2 + borderWidth / 2, mPaint);

    }

    private String getTimeForNum(int num){

        if(num == AM){
            return "上午";
        }else if(num == PM){
            return "下午";
        }else{
            return num + ":00";
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:

                float downX = event.getX();
                float downY = event.getY();

                int downPosition = getCoordinateToPosition(downX, downY);

                //如果按下的区域不是“上午”“下午”
                if(downPosition >= 0){
                    checkToJoinList(downPosition);
                }

                break;
            case MotionEvent.ACTION_MOVE:

                float moveX = event.getX();
                float moveY = event.getY();

                int nowPosition = getCoordinateToPosition(moveX, moveY );

                //如果当前滑动到view之外或上午、下午区域，则拦截
                if(moveX < 0 || moveY < 0 || moveX > width || moveY > height || nowPosition == lastTouchPosition || nowPosition < 0){
                    return false;
                }

                checkToJoinList(nowPosition);

                break;
            case MotionEvent.ACTION_UP:

                lastTouchPosition = NONE;
                break;
        }

        return true;
    }

    /**
     * 检查是否加入集合
     * @param position 当前时间
     */
    private void checkToJoinList(int position){
        if(selectedList == null){
            selectedList = new ArrayList<>();
        }

        //如果当前点击、滑动的区域不是 0:00 -- 23:00 则拦截
        if(position < 0){
            return;
        }

        Integer nowPosition = new Integer(position);
        //判断是否包含在集合里（是否已经选中）
        boolean listIsContains = selectedList.contains(nowPosition);

        //如果是当前按下/滑动 首次到事件区域
        if(lastTouchPosition < 0){
            //如果集合包含,则当前滑动是取消选中
            if(listIsContains){
                //记录当前是取消选中
                isSelect = false;
                selectedList.remove(nowPosition);

            }else{
                //不包含则当前滑动是选中
                isSelect = true;
                selectedList.add(nowPosition);
                Collections.sort(selectedList);
            }

            postInvalidate();
            if(listener != null){
                listener.onChangeTime(isSelect, selectedList);
            }

        }else{
            //选中,并且集合不包含
            if(isSelect && !listIsContains){
                selectedList.add(nowPosition);
                Collections.sort(selectedList);
                postInvalidate();

                if(listener != null){
                    listener.onChangeTime(isSelect, selectedList);
                }

            }else if(!isSelect && listIsContains){  //取消选中，并且集合包含，则移除
                selectedList.remove(nowPosition);
                postInvalidate();

                if(listener != null){
                    listener.onChangeTime(isSelect, selectedList);
                }

            }
        }

        lastTouchPosition = position;
    }

    /**
     * 获取坐标转换的position
     * @param x x坐标
     * @return 返回当前时间的position（0:00->0   11:00 -> 11   23:00 -> 23   上午-> -2  下午 -> -1）
     */
    private int getCoordinateToPosition(float x, float y){

        //判断是否第一列
        if(x / childerWidth < 1){
            //第一列第一行是上午
            if(y/ childerHeight < 2){
                return AM;
            }else{  //第一列第二行是下午
                return PM;
            }
        }else{

            //计算其他的position位置（0:00->0   11:00 -> 11   23:00 -> 23）
            int tempPosition = 0;
            if(y / childerHeight < 1){
                tempPosition = (int) (x / childerWidth) - 1;
            }else if(1 < y / childerHeight &&  y / childerHeight < 2  ){
                tempPosition = (int) (x / childerWidth) - 1 + 6;
            }else if(2 < y / childerHeight &&  y / childerHeight < 3){
                tempPosition = (int) (x / childerWidth) - 1 + 12;
            }else {
                tempPosition = (int) (x / childerWidth) - 1 + 18;

            }
            return tempPosition;
        }
    }

    /**
     * 设置选中的集合
     * @param selectedList
     */
    public void setSelectedList(List<Integer> selectedList){
        this.selectedList = selectedList;
        postInvalidate();
    }

    public List<Integer> getSelectedList(){
        return selectedList;
    }

    private OnChangeTimeListener listener;

    public void setOnChangeTimeListener(OnChangeTimeListener listener){
        this.listener = listener;
    }

    public interface OnChangeTimeListener{
        void onChangeTime(boolean isSelect, List<Integer> seletedList);
    }

    public SelectBean getSelectBean() {
        return selectBean;
    }

    public void setSelectBean(SelectBean selectBean) {
        this.selectBean = selectBean;
        postInvalidate();
    }
}
