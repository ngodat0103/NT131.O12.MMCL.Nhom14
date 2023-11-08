package me.ngodat0103.myapplication;

import android.os.Build;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Api_request  {
    static String HOST = "http:server.uitprojects.com/";


    static String url_encoded_builder(Map<String,String> parameters){
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            result.append("=");
            try {
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            result.append("&");
        }
        String resultString = result.toString();
        resultString = resultString.substring(0, resultString.length() - 1);
        return resultString;
    }
    static Map<String,String> convert_to_map(String url_encoded_String){
        url_encoded_String = url_encoded_String.trim();
        Map<String,String> new_map = new HashMap<>();
        String[] elements;
        elements = url_encoded_String.split(",");
        for(String element: elements){
            String key = element.split(":")[0].replace("{","").replace("}","").replace("\"","").trim();
            String value = element.split(":")[1].replace("{","").replace("}","").replace("\"","").trim();
            new_map.put(key,value);
        }
        return  new_map;
    }


    static public Map<String,String> authentication(String username,String password) throws IOException {
        URL login_url = new URL(HOST.concat("authentication"));

        Map<String, String> parameters = new HashMap<>();
        parameters.put("username_primary", username);
        parameters.put("password", password);
        parameters.put("device_name", Build.MODEL);
        String url_encoded_data = Api_request.url_encoded_builder(parameters);


        HttpURLConnection con = (HttpURLConnection) login_url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());

        out.writeBytes(url_encoded_data);
        out.flush();
        out.close();

        StringBuffer content = new StringBuffer();
        int status = con.getResponseCode();
        if (status == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            content = new StringBuffer();
            String read_line;
            while ((read_line = in.readLine()) != null) {
                content.append(read_line);
            }
            in.close();
        }
        String form_url_String = content.toString();
        Map<String,String> authen_map = Api_request.convert_to_map(form_url_String);
        con.disconnect();
    return authen_map;
    }

    static Map<String,String> register(String username,String password,String email) throws IOException {
        URL login_url = new URL(HOST.concat("registration"));
        HttpURLConnection con = (HttpURLConnection) login_url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-type","application/x-www-form-urlencoded");

        Map<String,String> parameter = new HashMap<>();
        parameter.put("username_primary",username);
        parameter.put("password",password);
        parameter.put("email",email);
        String url_encoded_data = Api_request.url_encoded_builder(parameter);

        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(url_encoded_data);
        out.flush();
        out.close();
        if (con.getResponseCode()==200){
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder content = new StringBuilder();
            String readline;
            while ((readline=in.readLine())!=null){
                content.append(readline);
            }
            String form_url_String = content.toString();
            in.close();
            con.disconnect();
            return convert_to_map(form_url_String);
        }
        else
            return null;
    }
}
