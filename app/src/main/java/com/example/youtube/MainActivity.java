package com.example.youtube;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Customer> list;
    private ArrayList<Customer> arraylist;
    private ListView listView;
    private ListViewAdapter_mod adapter;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://192.249.19.243:8980";
    private Object JSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview1);
        list = new ArrayList<Customer>();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        //////////연락처 하나만 가져오기/////////
        Call<JsonObject> call = retrofitInterface.executeInit();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("hey!");
                if(response.code() == 200){
                    System.out.println("class name: " + response.body().getClass());
                    JsonParser Parser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) Parser.parse(response.body().toString());
                    JsonArray memberArray = (JsonArray) jsonObj.get("users");
                    for (int i=0; i<memberArray.size(); i++){
                        JsonObject object = (JsonObject) memberArray.get(i);
                        String name = object.get("name").toString();
                        String email = object.get("email").toString();
                        String password = object.get("password").toString();

                        list.add(new Customer(name, email, password));
                    }

                    arraylist = new ArrayList<Customer>();
                    arraylist.addAll(list);

                    adapter = new ListViewAdapter_mod(list, MainActivity.this);

                    listView.setAdapter(adapter);
                }
                else if(response.code()==404){
                    Toast.makeText(MainActivity.this, "Wrong Credentials",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });



        findViewById(R.id.login).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                handleLoginDialog();
            }
        });

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                handleSignupDialog();
            }
        });
    }

    //////////////로그인 하는 버튼을 눌렀을때//////////////
    private void handleLoginDialog() {

        View view = getLayoutInflater().inflate(R.layout.login_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view).show();

        Button loginBtn = view.findViewById(R.id.login);
        final EditText emailEdit = view.findViewById(R.id.emailEdit);
        final EditText passwordEdit = view.findViewById(R.id.passwordEdit);

        //////////////실제 로그인 버튼을 눌렀을 때//////////
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();

                map.put("email", emailEdit.getText().toString());
                map.put("password", passwordEdit.getText().toString());

                Call<JsonObject> call = retrofitInterface.executeLogin(map);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.code() == 200){
                            System.out.println("class name: " + response.body().getClass());
                            JsonParser Parser = new JsonParser();
                            JsonObject jsonObj = (JsonObject) Parser.parse(response.body().toString());
                            JsonObject object = (JsonObject) jsonObj.get("result");
                            //LoginResult result = response.body();

                            //System.out.println(result);
                            //JsonObject object = (JsonObject) memberArray;
                            String name = object.get("name").toString();
                            String email = object.get("email").toString();
                            //String email = result.getEmail();

                            System.out.println(name);
                            System.out.println(email);

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setTitle(name);
                            builder1.setMessage(email);

                            builder1.show();
                        }
                        else if(response.code()==404){
                            System.out.println("You are wrong");
                            Toast.makeText(MainActivity.this, "Wrong Credentials",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void handleSignupDialog() {
        View view = getLayoutInflater().inflate(R.layout.signup_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).show();

        Button signupBtn = view.findViewById(R.id.signup);
        final EditText nameEdit = view.findViewById(R.id.nameEdit);
        final EditText emailEdit = view.findViewById(R.id.emailEdit);
        final EditText passwordEdit = view.findViewById(R.id.passwordEdit);

        signupBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //View mView = getLayoutInflater().inflate(R.layout.activity_main)
                list.add(new Customer(nameEdit.getText().toString(), emailEdit.getText().toString(), passwordEdit.getText().toString()));
                HashMap<String, String> map = new HashMap<>();

                map.put("name", nameEdit.getText().toString());
                map.put("email", emailEdit.getText().toString());
                map.put("password", passwordEdit.getText().toString());

                Call<Void> call = retrofitInterface.executeSignup(map);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 200){
                            Toast.makeText(MainActivity.this, "Signed up successfully",
                                    Toast.LENGTH_LONG).show();
                        }
                        else if(response.code()==400){
                            Toast.makeText(MainActivity.this, "Already registered",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}