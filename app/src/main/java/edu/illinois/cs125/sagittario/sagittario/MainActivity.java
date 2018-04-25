package edu.illinois.cs125.sagittario.sagittario;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public EditText nBombs, fieldSize;

    public EditText searchText;

    public Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nBombs = (EditText) findViewById(R.id.editNBombs);
        fieldSize = (EditText) findViewById(R.id.editFieldSize);
        searchText = (EditText) findViewById(R.id.editSearch);
        startButton = (Button) findViewById(R.id.btnStart);
        startButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MinesweeperActivity.class);
        int fieldSize= Integer.parseInt(this.fieldSize.getText().toString());
        int nBombs = Integer.parseInt(this.nBombs.getText().toString());
        intent.putExtra("fieldSize", fieldSize);
        intent.putExtra("nBombs", nBombs);
        intent.putExtra("searchText", searchText.getText().toString());
        startActivity(intent);
    }
}
