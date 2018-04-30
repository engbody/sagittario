package edu.illinois.cs125.sagittario.sagittario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

public class CanvasView extends View {
    public MinesweeperActivity activity;
    private Rect boundingRect, rect2;
    private Paint paint;


    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public CanvasView(Context context, AttributeSet set) {
        super(context, set);
        int xSize = this.getWidth();
        int ySize = this.getHeight();

        boundingRect = new Rect(0, 0, xSize, ySize);
        rect2 = new Rect(0, 0, 0, 0);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        paint.setTextSize(32 * getResources().getDisplayMetrics().density);
        setWillNotDraw(false);
        setFocusable(true);
        setMinimumHeight(800);
        setMinimumWidth(640);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int xSize = this.getWidth();
        int ySize = this.getHeight();
        boundingRect = new Rect(0, 0, xSize, ySize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("CanvasView", "motion event!" + event.getAction());

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            return true;
        }

//        android.app.
        if (event.getAction() == MotionEvent.ACTION_UP && activity.loaded){
            this.playSoundEffect(SoundEffectConstants.CLICK);
            final int deltaX = this.getWidth() / activity.sweeper.fieldSize;
            final int deltaY = this.getHeight() / activity.sweeper.fieldSize;
            int x = (int) event.getX() / deltaX;
            int y = (int) event.getY() / deltaY;
            long timeDown = event.getEventTime() - event.getDownTime();
            if(timeDown < 300) {
                Log.d("CanvasView", "Revealing at " + x + ", " + y + ".");
                activity.sweeper.choose(x, y);
                // handle win/lose conditions
                if (activity.sweeper.gameOver) {
                    this.invalidate();
                    if (activity.sweeper.gameWon) {
                        // WIN ACTIVITY
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("You WON!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Menu", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(activity, MainActivity.class);
                                activity.startActivity(intent);
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        // LOSE ACTIVITY
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("You LOST!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Menu", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(activity, MainActivity.class);
                                activity.startActivity(intent);
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            } else {
                Log.d("CanvasView", "Flagging at " + x + ", " + y + ".");
                activity.sweeper.flag(x, y);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getUnicodeChar() == 'a'){
            Log.d("CanvasView", "Recycling Background");
            activity.background.recycle();

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthMeasureSpec == 0 || heightMeasureSpec == 0) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);
            heightMeasureSpec = displayMetrics.heightPixels;
            widthMeasureSpec = displayMetrics.widthPixels;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (activity == null || !activity.loaded) {
            canvas.drawColor(0xFFCCFF);
            return;
        }

        if (activity.background.isRecycled()){
            activity.createBackgroundFromInfo(activity.info);
        }

        if (activity.flag.isRecycled() || activity.bomb.isRecycled()){
            activity.loadBitmaps();
        }

//        Log.v("Canvas View", "Drawing");
        // todo draw the field
        final int deltaX = this.getWidth() / activity.sweeper.fieldSize;
        final int deltaY = this.getHeight() / activity.sweeper.fieldSize;

        final int offsetX = 0;
        final int offsetY = 0;

        boundingRect.left = getLeft();
        boundingRect.top = getTop();
        boundingRect.right = getRight();
        boundingRect.bottom = getBottom();

        canvas.drawBitmap(activity.background, null, boundingRect, null);

        for (int i = 0; i < activity.sweeper.fieldSize; i++) {
            for (int j = 0; j < activity.sweeper.fieldSize; j++) {
                // draw tile
                rect2.left = offsetX + deltaX * i;
                rect2.right = offsetX + deltaX * (i + 1);
                rect2.top = offsetY + deltaY * j;
                rect2.bottom = offsetY + deltaY * (j + 1);
                // draw covered tiles
                if (activity.sweeper.displayGrid[i][j] != MineSweeper.UNCOVERED) {
                    activity.uncovered.setBounds(rect2);
                    activity.uncovered.draw(canvas);
                }
                // draw flag on top
                if (activity.sweeper.displayGrid[i][j] == MineSweeper.FLAGGED) {
                    canvas.drawBitmap(activity.flag, null, rect2, null);
                }
                if (activity.sweeper.displayGrid[i][j] == MineSweeper.UNCOVERED) {
                    final int count = activity.sweeper.neighborGrid[i][j];
                    if (count == 9) {
                        canvas.drawBitmap(activity.bomb, null, rect2, null);
                    } else if (count > 0) {
                        float x = (rect2.left) + deltaX / 2.0f;
                        float y = (rect2.bottom) - deltaY / 2.0f;
                        paint.setColor(Color.BLACK);
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawText(Integer.toString(count), x, y, paint);
                        paint.setColor(Color.WHITE);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawText(Integer.toString(count), x, y, paint);
                    }
                }
            }
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            canvas.drawLine(deltaX * i, 0, deltaX * i, getHeight(), paint);
        }
        for(int i = 0; i < activity.sweeper.fieldSize; i++){
            float y = deltaY * i;
            paint.setColor(Color.BLACK);
            canvas.drawLine(0, y, getWidth(), y, paint);
        }
    }
}
