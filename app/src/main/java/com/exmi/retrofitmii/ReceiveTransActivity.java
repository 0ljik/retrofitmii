package com.exmi.retrofitmii;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReceiveTransActivity extends AppCompatActivity {
    private TextView textViewResult;
    private JsonPlaceholderApi jsonPlaceholderApi;
    private String trans;
    private String username;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_trans);
        SharedPreferences sharedpreferences;
        textViewResult = findViewById(R.id.textView);

        Intent intent = getIntent();
        trans = intent.getStringExtra("Trans");
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("Username", "");
        token = sharedpreferences.getString("Token", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://env-zhol.jcloud.kz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceholderApi = retrofit.create(JsonPlaceholderApi.class);
    }

    private void accept(String token) {
        JsonObject post = new JsonObject();
        post.addProperty("trans", trans);
        post.addProperty("user", username);

        Call<ResponseBody> call = jsonPlaceholderApi.accept(token, post);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                ResponseBody postResponse = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n";
                content += "trans: " + postResponse.getTrans() + "\n";
                content += "amount: " + postResponse.getAmount() + "\n";
                content += "status: " + postResponse.getStatus() + "\n";
                /* content += "UserID: " + postResponse.getUserId() + "\n";
                content += "Title: " + postResponse.getTitle() + "\n";
                content += "Text: " + postResponse.getText() + "\n\n";
*/
                textViewResult.append(content);

//                if (postResponse.getStatus()>1){
//                    tHandler.removeCallbacks(tCheckRunnable);
//                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

}