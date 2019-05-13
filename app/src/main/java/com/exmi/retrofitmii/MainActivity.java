package com.exmi.retrofitmii;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.nio.charset.Charset;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences sharedpreferences;
    private TextView textViewResult;
    private JsonPlaceholderApi jsonPlaceholderApi;
//    private Handler tHandler = new Handler();
    private String payload="";
    byte statusByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences( getPackageName() + "_preferences", MODE_PRIVATE);

        textViewResult = findViewById(R.id.text_view_result);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://env-zhol.jcloud.kz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceholderApi = retrofit.create(JsonPlaceholderApi.class);
//        getPosts();
//        getComments();
//        createPost();
//        create();
       // openSignInActivity();
        Long amildiff = Calendar.getInstance().getTimeInMillis() - PreferenceHelper.get_amillis();
        Toast.makeText(getApplicationContext(), "amildif = "+ amildiff, Toast.LENGTH_SHORT).show();
        if (amildiff>5*60*1000){
            openSignInActivity();
        }

        Button btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceHelper.set_amillis((long) 100);
                openSignInActivity();
            }
        });

//        Button btnSignInAct = findViewById(R.id.btnSignInAct);
//        btnSignInAct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openSignInActivity();
//            }
//        });

        Button btnCreateTransAct = (Button) findViewById(R.id.btnCreateTransAct);
        btnCreateTransAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateTransActivity();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    void processIntent(Intent intent) {

        NdefMessage[] messages = getNdefMessages(getIntent());
        for(int i=0;i<messages.length;i++){
            for(int j=0;j<messages[0].getRecords().length;j++){
                NdefRecord record = messages[i].getRecords()[j];
                statusByte=record.getPayload()[0];
                int languageCodeLength= statusByte & 0x3F; //mask value in order to find language code length
                int isUTF8=statusByte-languageCodeLength;
                if(isUTF8==0x00){
                    payload=new String(record.getPayload(),1+languageCodeLength,record.getPayload().length-1-languageCodeLength,Charset.forName("UTF-8"));
                }
                else if (isUTF8==-0x80){
                    payload=new String(record.getPayload(),1+languageCodeLength,record.getPayload().length-1-languageCodeLength,Charset.forName("UTF-16"));
                }
                //messageText.setText("Text received: "+ payload);
                Intent receiveIntent = new Intent(this, ReceiveTransActivity.class);
                receiveIntent.putExtra("Trans", payload);
                startActivity(receiveIntent);
            }
        }
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        NdefMessage[] msgs = null;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        }else {
            Log.d("Peer to Peer 2", "Unknown intent.");
            finish();
        }

        return msgs;
    }

    public void openSignInActivity(){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
    public void openCreateTransActivity(){
        Intent intent = new Intent(this, CreateTransActivity.class);
        startActivity(intent);
    }

    /*private void getPosts() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userId", "1");
        parameters.put("_sort", "id");
        parameters.put("order", "desc");

        Call<List<Post>> call = jsonPlaceholderApi.getPosts(parameters);
        //new Integer[]{2,3,6}, "id", "desc");

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Post> Posts = response.body();

                for (Post post : Posts) {
                    String content = "";
                    content += "ID: " + post.getId() + "\n";
                    content += "UserID: " + post.getUserId() + "\n";
                    content += "Title: " + post.getTitle() + "\n";
                    content += "Text: " + post.getText() + "\n\n";

                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void getComments() {
        Call<List<Comment>> call = jsonPlaceholderApi.getComments("post/3/comments");

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Comment> Comments = response.body();

                for (Comment comment : Comments) {
                    String content = "";
                    content += "ID: " + comment.getId() + "\n";
                    content += "PostID: " + comment.getPostId() + "\n";
                    content += "Name: " + comment.getName() + "\n";
                    content += "Email: " + comment.getEmail() + "\n";
                     content += "Text: " + comment.getText() + "\n\n";

                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }*/

    private void create() {
        JsonObject post = new JsonObject();
        post.addProperty("user",12312);
        post.addProperty("amount",500);

        Call<ResponseBody> call = jsonPlaceholderApi.create("ewfweafawe",post);

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

                //NFC code
//                if (postResponse.getStatus()==1){//if NFC response
//                    startCheck();
//                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void check() {
        JsonObject post = new JsonObject();
        post.addProperty("trans",12312);
        post.addProperty("user",1);

        Call<ResponseBody> call = jsonPlaceholderApi.check("fewaf",post);

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
    private void accept(String token) {
        JsonObject post = new JsonObject();
        post.addProperty("trans",12312);
        post.addProperty("user",1);

        Call<ResponseBody> call = jsonPlaceholderApi.accept(token,post);

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
//    private void createPost() {
//        JsonObject post = new JsonObject();
//        post.addProperty("user",12312);
//        post.addProperty("amount",500);
//
//        Call<ResponseBody> call = jsonPlaceholderApi.create(post);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (!response.isSuccessful()) {
//                    textViewResult.setText("Code: " + response.code());
//                    return;
//                }
//
//                Post postResponse = response.body();
//
//                String content = "";
//                content += "Code: " + response.code() + "\n";
//                content += "trans: " + postResponse.getTrans() + "\n";
//                /* content += "UserID: " + postResponse.getUserId() + "\n";
//                content += "Title: " + postResponse.getTitle() + "\n";
//                content += "Text: " + postResponse.getText() + "\n\n";
//*/
//                textViewResult.append(content);
//
//
//            }
//
//            @Override
//            public void onFailure(Call<Post> call, Throwable t) {
//                textViewResult.setText(t.getMessage());
//            }
//        });
//    }

    //Handler
//    public void startCheck(){
//        tCheckRunnable.run();
//    }
//
//    private Runnable tCheckRunnable = new Runnable() {
//        @Override
//        public void run() {
//            check();
//            Toast.makeText(MainActivity.this,"Handler running", Toast.LENGTH_SHORT).show();
//            tHandler.postDelayed(this, 5000);
//        }
//    };
}
