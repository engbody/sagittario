package edu.illinois.cs125.sagittario.sagittario;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

public class MinesweeperActivity extends AppCompatActivity implements Runnable {
    private SagittarioApplication app;
    // views
    private ImageProvider provider;
    private CanvasView view;
    private ProgressBar loading;
    //
    private boolean loaded = false;
    //
    private MineSweeper sweeper;
    private Bitmap background;
    private Drawable tile;
    private Bitmap flag;
    private Bitmap bomb;

    @Override
    public void run() {
        loading.setVisibility(View.INVISIBLE);
        background = provider.getBackground();
        loaded = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper);
        // init settings and minesweeper
        int fieldSize = getIntent().getIntExtra("fieldSize", 8);
        int nBombs = getIntent().getIntExtra("nBombs", 10);
        String searchText = getIntent().getStringExtra("searchText");
        if (searchText == null) {
            searchText = "minesweeper";
        }
        sweeper = new MineSweeper(fieldSize, nBombs);
        // find views
        loading = (ProgressBar) findViewById(R.id.loadingBar);
        view = (CanvasView) findViewById(R.id.canvasView);
        view.activity = this;
        // get the bitmaps
        flag = BitmapFactory.decodeResource(this.getResources(), R.drawable.flag);
        bomb = BitmapFactory.decodeResource(this.getResources(), R.drawable.bomb);
        tile = this.getDrawable(R.drawable.ic_tile);
        // start the image search and load the background
        app = (SagittarioApplication) this.getApplication();
        provider = app.createImageProvider(searchText, this);
        loading.setVisibility(View.VISIBLE);
    }


    public static class CanvasView extends  View{
        public MinesweeperActivity activity;
        /**
         * Simple constructor to use when creating a view from code.
         *
         * @param context The Context the view is running in, through which it can
         *                access the current theme, resources, etc.
         */
        public CanvasView(Context context) {
            super(context);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (activity == null || !activity.loaded){
                canvas.drawColor(0xFFCCFF);
                return;
            }
            if (!activity.loaded){
                return;
            }
            // todo draw the field
            int xSize = this.getWidth();
            int ySize = this.getHeight();
        }
    }
}
