package edu.illinois.cs125.sagittario.sagittario;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Runnable{

    public EditText nBombs, fieldSize;

    public EditText searchText;

    public Button startButton;

    public ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nBombs = (EditText) findViewById(R.id.editNBombs);
        fieldSize = (EditText) findViewById(R.id.editFieldSize);
        searchText = (EditText) findViewById(R.id.editSearch);
        startButton = (Button) findViewById(R.id.btnStart);
        startButton.setOnClickListener(this);
        bar = (ProgressBar)findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        bar.setVisibility(View.VISIBLE);
        SagittarioApplication app = (SagittarioApplication)getApplication();
        app.fieldSize = Integer.parseInt(fieldSize.getText().toString());
        app.nbombs = Integer.parseInt(nBombs.getText().toString());
        app.createImageProvider(searchText.toString(), this);
    }


    @Override
    public void run() {
        // Will be called when finished loading
        bar.setVisibility(View.INVISIBLE);
        setContentView(R.layout.activity_minesweeper);
    }
}
