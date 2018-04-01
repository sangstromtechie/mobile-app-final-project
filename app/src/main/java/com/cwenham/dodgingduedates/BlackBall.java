package com.cwenham.dodgingduedates;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BlackBall {

    private Paint paint = new Paint();
    private int xPos;
    private int yPos;

    public BlackBall(int x, int y) {
        xPos = x;
        yPos = y;
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(false);
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void move(int speed) {
        xPos -= speed;
    }

    public void draw(Canvas canvas, int radius) {
        canvas.drawCircle(xPos, yPos, radius, paint);
    }
}
