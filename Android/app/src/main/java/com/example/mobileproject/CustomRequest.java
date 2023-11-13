package com.example.mobileproject;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomRequest {
   URL url;
    HttpURLConnection con;
    Map<String, String> parameters = new HashMap<>();
    String body_url_encoded;
    public CustomRequest(String url,
                         String method,
                         Map<String,String> header,
                         Map<String,String> parameters) throws IOException {
        this.url = new URL(url);
        this.parameters  = parameters;
        this.con  = (HttpURLConnection) this.url.openConnection();
        con.setRequestMethod(method);
        if (Objects.equals(method, "POST")) {
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);
        }
        else
            con.setRequestProperty("Content-Type","application/json");
        if (header!=null)
            for(Map.Entry<String,String> entry : header.entrySet())
                con.setRequestProperty(entry.getKey(),entry.getValue());

        if (Objects.equals(method, "POST")) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                builder.append("&");
            }
            body_url_encoded = builder.toString();
            body_url_encoded = body_url_encoded.substring(0, body_url_encoded.length() - 1);
        }
    }

    Map<String,String> stringToMap(String body_url_encoded){
        body_url_encoded = body_url_encoded.trim();
        Map<String,String> new_map = new HashMap<>();
        String[] elements;
        elements = body_url_encoded.split(",");
        for(String element: elements){
            String key = element.split(":")[0].replace("{","").replace("}","").replace("\"","").trim();
            String value = element.split(":")[1].replace("{","").replace("}","").replace("\"","").trim();
            new_map.put(key,value);
        }
        return  new_map;
    }

    public Map<String, String>  sendRequest() throws IOException {
        if (Objects.equals(con.getRequestMethod(), "POST")) {
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(body_url_encoded);
            out.flush();
            out.close();
        }
        BufferedReader in ;

        int status = con.getResponseCode();
        if (status==200)
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        else
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while((line=in.readLine())!=null){
            builder.append(line);
        }
        in.close();
        return stringToMap(builder.toString());
    }

    public boolean sendPutRequest(Map<String,String> body_String) throws IOException {
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(new Gson().toJson(body_String));
        return con.getResponseCode() == 204;
    }

    public String sendRequest(boolean string) throws IOException {
        BufferedReader in ;
        int status = con.getResponseCode();
        if (status==200)
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        else
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while((line=in.readLine())!=null){
            builder.append(line);
        }
        in.close();
        return builder.toString();
    }

}
