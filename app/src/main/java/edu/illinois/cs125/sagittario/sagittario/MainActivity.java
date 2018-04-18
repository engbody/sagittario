package edu.illinois.cs125.sagittario.sagittario;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        SagittarioApplication app = (SagittarioApplication)getApplication();
        app.fieldSize = Integer.parseInt(fieldSize.getText().toString());
        app.nbombs = Integer.parseInt(nBombs.getText().toString());
        app.createImageProvider(searchText.toString());
    }
}
