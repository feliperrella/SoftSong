package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Search  extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        search = (EditText) findViewById(R.id.search);
        people = (ListView) findViewById(R.id.people);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    new GetMyPeople().execute();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    public class GetMyPeople extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            try {
                HttpHandler sh = new HttpHandler();

                        System.out.println(search.getText());
                        String jsonStr = sh.makeServiceCall("http://" + HttpHandler.IP + "/Search.php?user=" + search.getText());
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        String user = "";
                        String foto = "";
                        String name = "";
                        for(int i = 0; i < jsonArray.length(); i++) {
                            JSONObject rs = jsonArray.getJSONObject(i);
                            user = user + rs.getString("username") + ",";
                            foto = foto + rs.getString("caminho_imagem") + ",";
                            name = name + rs.getString("nome") + ",";
                        }
                        user = user.substring(0, user.length() - 1);
                        foto = foto.substring(0, foto.length() - 1);
                        name = name.substring(0, name.length() - 1);
                        pe = user.split(",");
                        ft = foto.split(",");
                        nm = name.split(",");


                        isSuccess = true;
            } catch (Exception ex) {
                isSuccess = false;
                message = "Algo deu errado :(";
            }
            return message;
        }


        @Override
        protected void onPostExecute(String r) {
            //Toast.makeText(Perfil.this, r, Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Search.customAdapter customAdapter = new customAdapter();
                    try {
                        people.setAdapter(customAdapter);
                    }
                    catch (Exception e)
                    {
                        people.setAdapter(null);
                    }

                }
            });
            if (isSuccess = false) {
                Toast.makeText(Search.this, r, Toast.LENGTH_SHORT).show();
            }

        }
    }


    class customAdapter extends BaseAdapter
    {


        @Override
        public int getCount() {
                return pe.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.searchlist_layout, null);
            TextView name = (TextView) view.findViewById(R.id.searchname);
            final TextView usu = (TextView) view.findViewById(R.id.searchusu);
            final ImageView per = (ImageView) view.findViewById(R.id.seachperfil);
            RelativeLayout rel = (RelativeLayout) view.findViewById(R.id.relativeSearch);
            rel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user = (String) usu.getText();
                    try {
                        cami = "http://" + HttpHandler.IP + "/pictures/" + ft[i];
                    }
                    catch (Exception e){}
                    //Toast.makeText(getApplicationContext(), user, Toast.LENGTH_LONG).show();
                    Intent my = new Intent(Search.this, Public_Perfil.class);
                    startActivity(my);
                }
            });
            try {
                name.setText(nm[i]);
                usu.setText(pe[i]);
                Glide.with(getApplicationContext()).load("http://" + HttpHandler.IP + "/pictures/" + ft[i]).into(per);
            }
            catch (Exception e){}
            return view;
        }
    }

    public static String user;
    public static Bitmap caminho;
    public static String cami;
    String[] pe, ft, nm;
    EditText search;
    ListView people;
}
