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

import java.io.Serializable;
import java.nio.ByteBuffer;

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

    public static class BitmapInfo implements Serializable{
        public byte[] pixels;
        public Bitmap.Config cfg;
        public int width;
        public int height;
    }

    protected BitmapInfo info = new BitmapInfo();
    protected Drawable tile, uncovered;
    protected Bitmap flag;
    protected Bitmap bomb;
    protected GameState state;
    private Handler mHandler;


    public enum GameState{
        PLAYING, WON, LOST;
    }

    private void setupDrawCallback(){
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
    public void run() {
        loading.setVisibility(View.INVISIBLE);
        Log.e("MineSweeper", "Re-entered Activity!");
        // create bitmap info
        background = provider.getBackground();
        int size = background.getRowBytes() * background.getHeight();
        ByteBuffer imageBuffer = ByteBuffer.allocate(size);
        background.copyPixelsToBuffer(imageBuffer);
        imageBuffer.rewind();
        info.pixels = imageBuffer.array();
        info.cfg = background.getConfig();
        info.width = background.getWidth();
        info.height = background.getHeight();
        // setup draw callback
        setupDrawCallback();
    }

    private void loadFromBundle(Bundle savedInstanceState){
        sweeper = (MineSweeper) savedInstanceState.getSerializable("sweeper");
        info = (BitmapInfo) savedInstanceState.getSerializable("bufferInfo");
        createBackgroundFromInfo(info);
    }

    public void createBackgroundFromInfo(BitmapInfo info){
        background = Bitmap.createBitmap(info.width, info.height, info.cfg);
        ByteBuffer buff = ByteBuffer.wrap(info.pixels);
        background.copyPixelsFromBuffer(buff);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = GameState.PLAYING;
        setContentView(R.layout.activity_minesweeper);
        // find views
        loading = (ProgressBar) findViewById(R.id.loadingBar);
        view = (CanvasView) findViewById(R.id.canvasView);
        view.activity = this;
        // get the bitmaps
        loadBitmaps();
        tile = this.getDrawable(R.drawable.ic_tile);
        uncovered = this.getDrawable(R.drawable.ic_uncovered);
        // setup Handler
        mHandler = new Handler();
        // create or load instance state!
        if(savedInstanceState != null){
            this.loadFromBundle(savedInstanceState);
            this.setupDrawCallback();
            return;
        } else {
            // init settings and minesweeper
            int fieldSize = getIntent().getIntExtra("fieldSize", 8);
            int nBombs = getIntent().getIntExtra("nBombs", 10);
            String searchText = getIntent().getStringExtra("searchText");
            if (searchText == null) {
                searchText = "green";
            }
            sweeper = new MineSweeper(fieldSize, nBombs);
            // start the image search and load the background
            app = (SagittarioApplication) this.getApplication();
            provider = app.createImageProvider(searchText, this);
            loading.setVisibility(View.VISIBLE);
        }
        return;
    }

    /**
     * Load flag and bomb from resources.
     */
    public void loadBitmaps(){
        flag = BitmapFactory.decodeResource(this.getResources(), R.drawable.flag);
        bomb = BitmapFactory.decodeResource(this.getResources(), R.drawable.bomb);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("bufferInfo", info);
        outState.putSerializable("sweeper", sweeper);
    }
}
