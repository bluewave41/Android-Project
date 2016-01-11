package com.example.matthew.androidproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class CanvasView extends View {
    static boolean drag = false;
    static boolean move = false;
    Context context;
    int dragging = 0;
    ArrayList<Line> lines = new ArrayList();
    private Paint mPaint;
    ArrayList<Pair> points = new ArrayList();
    private CanvasView twin;

    public CanvasView(Context context, AttributeSet set) {
        super(context, set);
        this.context = context;
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(Color.BLACK);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void clear() {
        this.lines.clear();
        this.points.clear();
    }

    protected void onDraw(Canvas canvas) {
        for(Line l: lines) {
            canvas.drawLine(l.startX, l.startY, l.stopX, l.stopY, mPaint);
        }
        for(Pair p: points) {
            RectF start = p.getStart();
            RectF middle = p.getMiddle();
            RectF end = p.getEnd();
            canvas.drawArc(start.left, start.top, start.right, start.bottom, 0, 360, true, mPaint);
            canvas.drawArc(middle.left, middle.top, middle.right, middle.bottom, 0, 360, true, mPaint);
            canvas.drawArc(end.left, end.top, end.right, end.bottom, 0, 360, true, mPaint);
        }
        this.twin.setLines(thing());
        this.twin.invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for(int i=0;i<points.size();i++) {
                Pair p = points.get(i);
                if(p.getStart().contains(event.getX(), event.getY()) || p.getEnd().contains(event.getX(), event.getY())) {
                    points.remove(i);
                    invalidate();
                    drag = true;
                    dragging = i;
                    break;
                }
                else if(p.getMiddle().contains(event.getX(), event.getY())) {
                    points.remove(i);
                    invalidate();
                    move = true;
                    dragging = i;
                    break;
                }
            }
            if (!drag && !move) {
                lines.add(new Line(event.getX(), event.getY()));
                dragging = lines.size()-1;
                System.out.println(dragging);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Line current = lines.get(dragging);
            current.stopX = event.getX();
            current.stopY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Line current = lines.get(dragging);
            RectF start = new RectF(current.startX - 30, current.startY - 30, current.startX + 30, current.startY + 30);
            RectF middle = new RectF((current.startX + current.stopX) / 2 - 30, (current.startY + current.stopY) / 2 - 30, (current.startX + current.stopX) / 2 + 30, (current.startY + current.stopY) / 2 + 30);
            RectF end = new RectF(current.stopX - 30, current.stopY - 30, current.stopX + 30, current.stopY + 30);
            points.add(dragging, new Pair(start, middle, end));
            dragging = 0;
            drag = false;
            move = false;
        }
        return false;
    }

    public void setLines(ArrayList<Line> list) {
        this.lines = list;
    }

    public void setTwin(CanvasView view) {
        this.twin = view;
    }

    public ArrayList<Line> thing() {
        return this.lines;
    }
}
