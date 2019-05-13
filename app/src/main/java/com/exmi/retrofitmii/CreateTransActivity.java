package com.exmi.retrofitmii;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class CreateTransActivity extends AppCompatActivity {

    private JsonPlaceholderApi jsonPlaceholderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trans);

        Button btnCreateTrans = (Button) findViewById(R.id.btnCreateTrans);
        btnCreateTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long amildiff = PreferenceHelper.get_amillis()- Calendar.getInstance().getTimeInMillis();

                if (amildiff<5*60*60) {
                    openTransCreatedActivity();
                }else{
                    Toast.makeText(CreateTransActivity.this,"Session Expired", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openTransCreatedActivity(){
//        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        String token = sharedpreferences.getString("Token","");
        EditText etAmount = (EditText) findViewById(R.id.etAmount);

        Intent intent = new Intent(this, TransCreatedActivity.class);
        intent.putExtra("Amount",etAmount.getText().toString());
        startActivity(intent);
    }
}
