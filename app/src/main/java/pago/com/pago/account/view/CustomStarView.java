package pago.com.pago.account.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import pago.com.pago.R;

/**
 * Created by nIsHiTh on 10/9/2014.
 */
public class CustomStarView extends View {
    public CustomStarView(Context context) {
        super(context);
    }

    public CustomStarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomStarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int x = (measuredWidth/2)  ;
        int y = (measuredHeight/2) ;
        int outerRadius = Math.min(x,y) ;
        int innerRadius = outerRadius/2 ;

        Path polyPath = new Path();
        int points = 5;
        if (points < 3) return;

        float a = (float) (Math.PI * 2) / (points*2);
        int workingRadius = outerRadius;
        polyPath.reset();

        float fillPercent = (float) 100.0;

        canvas.save();
        canvas.translate(x, y);
        for(int j = 0; j < ((fillPercent < 1) ? 2 : 1) ; j++){
            polyPath.moveTo(workingRadius,0);
            for (int i = 1; i < points*2; i++) {
                workingRadius = (workingRadius == outerRadius) ? innerRadius : outerRadius;
                float xPt = (float) (workingRadius * Math.cos(a*i));
                float yPt = (float) (workingRadius * Math.sin(a*i));
                polyPath.lineTo(xPt, yPt);
            }
            polyPath.close();
            outerRadius -= outerRadius * fillPercent;
            innerRadius = outerRadius/2 ;
            a = -a;
        }


        float startAngle = (float) 125.0;
        canvas.rotate(startAngle);

        Paint fillPaint = new Paint();

        fillPaint.setColor(getResources().getColor(R.color.background_blue_color));
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);

        canvas.drawPath(polyPath, fillPaint);

        canvas.restore();
        canvas.save();
        canvas.translate(x, y);

        Paint textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.background_green_color));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(x/2);
        textPaint.setStyle(Paint.Style.FILL);

        canvas.drawText("03",-x/4,y/4,textPaint);
        canvas.restore();
        super.onDraw(canvas);
    }

    public static int dpToPixel(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pixelTodp(int pixel)
    {
        return (int) (pixel / Resources.getSystem().getDisplayMetrics().density);
    }
}
