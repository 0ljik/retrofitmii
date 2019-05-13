package com.exmi.retrofitmii;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.nio.charset.Charset;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransCreatedActivity extends AppCompatActivity {
    private TextView textViewResult;
    private JsonPlaceholderApi jsonPlaceholderApi;
    private Handler tHandler = new Handler();
    private NfcAdapter mNfcAdapter;
    private String amount;
    private Integer trans;
    private String username;
    private String token;
    private Runnable tCheckRunnable = new Runnable() {
        @Override
        public void run() {
            check();
            Toast.makeText(TransCreatedActivity.this, "Handler running", Toast.LENGTH_SHORT).show();
//            tHandler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_created);

        Intent intent = getIntent();
        amount = intent.getStringExtra("Amount");

        textViewResult = findViewById(R.id.textView2);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            textViewResult.setText("NFC apdater  is not available");
            finish();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://env-zhol.jcloud.kz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceholderApi = retrofit.create(JsonPlaceholderApi.class);
        create();
    }

    public void startCheck() {
        tCheckRunnable.run();
    }

    private void create() {
        final JsonObject post = new JsonObject();
        post.addProperty("user", username);
        post.addProperty("amount", amount);

        Call<ResponseBody> call = jsonPlaceholderApi.create(PreferenceHelper.get_token(), post);

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
                trans = postResponse.getTrans();

                textViewResult.append(content);

                //NFC code
                if (postResponse.getStatus() == 1) {//if NFC response
                    String inputText = String.valueOf(trans);//inputEditText.getText().toString();
                    NdefMessage message = create_RTD_TEXT_NdefMessage(inputText);
                    mNfcAdapter.setNdefPushMessage(message, TransCreatedActivity.this);
                    Toast.makeText(TransCreatedActivity.this, "Touch another mobile to share the message "+inputText, Toast.LENGTH_SHORT).show();
                    tHandler.postDelayed(tCheckRunnable,2000);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void check() {
        JsonObject post = new JsonObject();
        post.addProperty("trans", trans);
        post.addProperty("user", 1);

        Call<ResponseBody> call = jsonPlaceholderApi.check(PreferenceHelper.get_token(), post);

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

                textViewResult.append(content);

                if (postResponse.getStatus()>1){
//                    tHandler.removeCallbacks(tCheckRunnable);
                    textViewResult.setText("Transaction :" +trans + "was successful\n" +
                            "Amount :"+ amount);
                }else{
                    tHandler.postDelayed(tCheckRunnable, 2000);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    NdefMessage create_RTD_TEXT_NdefMessage(String inputText) {

        Locale locale = new Locale("en", "US");
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        boolean encodeInUtf8 = false;
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        byte status = (byte) (utfBit + langBytes.length);

        byte[] textBytes = inputText.getBytes(utfEncoding);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        NdefMessage message = new NdefMessage(new NdefRecord[]{textRecord});
        return message;

    }
}




