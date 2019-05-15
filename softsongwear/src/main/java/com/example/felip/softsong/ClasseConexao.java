package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.util.Log;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class ClasseConexao {
//public static String ip = "191.193.25.113";
public static String ip = "192.168.15.17";

    @SuppressLint("NewApi")
    public Connection CONN() throws ClassNotFoundException {
        Connection conn = null;
        String ConnURL;
        Class.forName("com.mysql.jdbc.Driver");
        try {
            //ip = ip + ":3306";
            String db = "RedeSocialMusica";
            String un = "server2";
            String password = "root";
            ConnURL = "jdbc:mysql://" + ip + "/" + db;

            conn = getConnection(ConnURL, un, password);

        }catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }

        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }

        return conn;
    }


}