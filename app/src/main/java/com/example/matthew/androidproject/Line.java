package com.example.matthew.androidproject;

/**
 * Created by Matthew on 2016-01-10.
 */
class Line
{
    float startX;
    float startY;
    float stopX;
    float stopY;

    public Line(float paramFloat1, float paramFloat2)
    {
        this(paramFloat1, paramFloat2, paramFloat1, paramFloat2);
    }

    public Line(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
        this.startX = paramFloat1;
        this.startY = paramFloat2;
        this.stopX = paramFloat3;
        this.stopY = paramFloat4;
    }
}
