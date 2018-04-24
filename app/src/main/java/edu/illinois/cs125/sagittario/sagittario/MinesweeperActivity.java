package edu.illinois.cs125.sagittario.sagittario;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MinesweeperActivity extends AppCompatActivity implements Runnable {
    private SagittarioApplication app;
    // views
    protected ImageProvider provider;
    protected CanvasView view;
    private ProgressBar loading;
    //
    protected boolean loaded = false;
    //
    protected MineSweeper sweeper;
    protected Bitmap background;
    protected Drawable tile, uncovered;
    protected Bitmap flag;
    protected Bitmap bomb;
    protected GameState state;

    private Handler mHandler;


    public enum GameState{
        PLAYING, WON, LOST;
    }

    @Override
    public void run() {
        loading.setVisibility(View.INVISIBLE);
        Log.e("MineSweeper", "Re-entered Activity!");
        background = provider.getBackground();
        loaded = true;
        Runnable next = new Runnable() {
            @Override
            public void run() {
                try{
                    view.invalidate();
                    Log.v("DrawRunner", "Invalidated View!");
                } catch (Throwable t) {
                    Log.e("DrawRunner", "Exception: ", t);
                } finally {
                    mHandler.postDelayed(this, 30);
                }
            }
        };
        mHandler.postDelayed(next, 10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = GameState.PLAYING;
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
        uncovered = this.getDrawable(R.drawable.ic_uncovered);
        // start the image search and load the background
        app = (SagittarioApplication) this.getApplication();
        provider = app.createImageProvider(searchText, this);
        loading.setVisibility(View.VISIBLE);
        mHandler = new Handler();
    }
}
