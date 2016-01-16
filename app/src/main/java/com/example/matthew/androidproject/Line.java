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
    float xLength;
    float yLength;

    //weight = [line length(p)/(A+distance)]^B

    //a ~ = a != 0 prevents divide by 0 and smooths warp
    // 1 <= b <= 2 influences distance effect of point to line
    //0 <= p <= 1 influence how line length effects strength

    public Line(float startX, float startY, float stopX, float stopY) {
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
        this.xLength = stopX-startX;
        this.yLength = stopY-startY;
    }
    public Line(float startX, float startY) { // for convenience
        this(startX, startY, startX, startY);
    }
}
