package com.shenzhe.answerquestion.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class RoundImageView extends AppCompatImageView {

    private Paint paint = null;
    // 设置画布抗锯齿(毛边过滤)
    private PaintFlagsDrawFilter pfdf = null;
    private Path path = null;

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    private void init(Context context, AttributeSet attrs) {
//        paint = new Paint();
//        // 透明度: 00%=FF（不透明） 100%=00（透明）
//        paint.setColor(Color.WHITE);
//        // paint.setColor(Color.parseColor("ffffffff"));
//        paint.setStyle(Paint.Style.STROKE);
//        // 解决图片拉伸后出现锯齿的两种办法: 1.画笔上设置抗锯齿 2.画布上设置抗锯齿
//        // http://labs.easymobi.cn/?p=3819
//        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        paint.setAntiAlias(true);
//        int clearBits = 0;
//        int setBits = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;
//        pfdf = new PaintFlagsDrawFilter(clearBits, setBits)
//        setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        // CCW: CounterClockwise(逆时针)
        // CW: Clockwise(顺时针)
        if (path == null) {
            path = new Path();
            path.addCircle(width / 2f, height / 2f, Math.min(width / 2f, height / 2f), Path.Direction.CCW);
            path.close();
        }
//      canvas.drawCircle(width / 2f, height / 2f, Math.min(width / 2f, height / 2f), paint);
//      super.onDraw里面也可能有多个canvas.save
        int saveCount = canvas.save();
        canvas.setDrawFilter(pfdf);
//      Region.Op.REPLACE 是显示第二次的
//      canvas.clipPath(path, Region.Op.REPLACE);
        canvas.clipPath(path, Region.Op.INTERSECT);
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }
}

