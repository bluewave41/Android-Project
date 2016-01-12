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
    float length;
    //store line length

    //weight = [line length(p)/(A+distance)]^B

    //a ~ = a != 0 prevents divide by 0 and smooths warp
    // 1 <= b <= 2 influences distance effect of point to line
    //0 <= p <= 1 influence how line length effects strength

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
