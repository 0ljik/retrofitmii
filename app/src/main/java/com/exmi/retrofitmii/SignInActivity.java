package com.exmi.retrofitmii;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignInActivity extends AppCompatActivity {
    private JsonPlaceholderApi api;
    private TextView txRes;
    private Button btnSignIn;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        txRes = (TextView) findViewById(R.id.txResult);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        etUsername.setText(PreferenceHelper.get_username());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://env-zhol.jcloud.kz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(JsonPlaceholderApi.class);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignIn.setEnabled(false);

                login(etUsername.getText().toString(),etPassword.getText().toString());
            }
        });
        Button btnSignUpAct = (Button) findViewById(R.id.btnSignUpAct);
        btnSignUpAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpActivity();
            }
        });
    }


    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void openSignUpActivity(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void login(final String username, final String password){
        final Login login = new Login(username, password);

        //Toast.makeText(getApplicationContext(), username+password, Toast.LENGTH_SHORT).show();

        Call<Token> call = api.signin(login);
        call.enqueue(new Callback<Token>() {

            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()){

                    if (response.body() != null) {

                        String token = response.body().getToken();
                        Long amillis = Calendar.getInstance().getTimeInMillis();
//                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

//                        sharedpreferences = getSharedPreferences( getPackageName() + "_preferences", MODE_PRIVATE);
//
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//
//                        editor.putString("Username", login.getUsername());
//                        editor.putString("Password", login.getPassword());
//                        editor.putString("Token", token);
//                        editor.putBoolean("LoggedIn",true);
//                        editor.commit();

                        PreferenceHelper.set_username(username);
                        PreferenceHelper.set_pass(password);
                        PreferenceHelper.set_isloggedin(true);
                        PreferenceHelper.set_amillis(amillis);
                        PreferenceHelper.set_token(token);

                        txRes.setText(amillis+"");

                        openMainActivity();
                    }

                }else {
                    Toast.makeText(SignInActivity.this,response.code(), Toast.LENGTH_SHORT).show();
                    btnSignIn.setEnabled(true);
//                    Log.d("fail","fail");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(SignInActivity.this,"failfail", Toast.LENGTH_SHORT).show();
                //Log.d("fail","fail");
            }
        });
    }
}
