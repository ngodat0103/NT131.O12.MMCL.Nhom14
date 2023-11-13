package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class Register extends AppCompatActivity {
    Spinner spinner;
    EditText username_edt,password_edt,email_edt,confirm_password_edt;

    public static final String[] languages = {"Choose Language", "English", "Vietnamese"};
    Button signup_button;
    WebView signup_Webview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        spinner = findViewById(R.id.spinner);
        username_edt = findViewById(R.id.edt_username);
        password_edt = findViewById(R.id.edt_password);
        email_edt = findViewById(R.id.edt_email);
        confirm_password_edt = findViewById(R.id.edt_confirmpassword);
        signup_Webview = findViewById(R.id.Webview_signup);
        signup_Webview.getSettings().setJavaScriptEnabled(true);
        signup_Webview.clearCache(true);


        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        }
        else {
            cookieManager.removeAllCookie();
        }


        signup_Webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d("webview",request.getUrl().toString());
                view.loadUrl(request.getUrl().toString());
                return super.shouldOverrideUrlLoading(view, request);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("webview", "Finished loading URL: " + url);

                if (url.contains("auth?")) {
                    view.evaluateJavascript(
                                    "let elements = document.getElementsByTagName(\"*\");\n" +
                                    "elements[33].click()",
                            null);
                }
                else if(url.contains("registration?client_id")){


                    String string_placeholder = "let %s =document.getElementById(\"%s\");";
                    String auto_fill_placeholder = "%s.value=\"%s\";";


                    Log.d("registration","email: " +email_edt.getText().toString());
                    view.evaluateJavascript(


                            String.format(string_placeholder,"username_input","username")+
                                    String.format(string_placeholder,"password_input","password")+
                                    String.format(string_placeholder,"email_input","email")+
                                    String.format(string_placeholder,"confirm_password_input","password-confirm")+


                                    String.format(auto_fill_placeholder,"email_input",email_edt.getText().toString())+
                                    String.format(auto_fill_placeholder,"username_input",username_edt.getText().toString())+
                                    String.format(auto_fill_placeholder,"password_input",password_edt.getText().toString())+
                                    String.format(auto_fill_placeholder,"confirm_password_input",confirm_password_edt.getText().toString())+
                                    "let elements = document.getElementsByTagName(\"*\");"+
                                    "elements[39].click();"



                            ,null
                    );

                }else if(url.contains("registration?session_code")){
                    signup_Webview.clearCache(true);
                    signup_Webview.clearHistory();
                    Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else if(url.contains("registration?execution")){
                    view.evaluateJavascript(
                            "let elements = document.getElementsByTagName(\"span\");"+
                                    "let my_list = [];"+
                                    "for(let i = 0 ; i<elements.length;i++){\n" +
                                    " my_list.push(elements[i].getAttribute(\"data-error\")); \n" +
                                    "};"+
                                    "(function() {\n" +
                                    "  return my_list.toString();\n" +
                                    "})();"


                            , new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {

                                    value = value.trim();
                                    value = value.replace("\"","");
                                    String[] value_array = value.split(",");


                                    Log.d("error_registation",value);
                                    for (int i = 0 ; i< value_array.length;i++) {
                                        Toast.makeText(getApplicationContext(), value_array[i], Toast.LENGTH_SHORT).show();
                                        try {
                                            Thread.sleep(300);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            });
                }
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                if(selectedLang.equals("English")){
                    setLocal(Register.this, "en");
                    finish();
                    startActivity(getIntent());
                }else if (selectedLang.equals("Vietnamese")){
                    setLocal(Register.this, "hi");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        signup_button = findViewById(R.id.btn_signup);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup_Webview.clearHistory();
                signup_Webview.clearCache(true);
                signup_Webview.loadUrl("https://uiot.ixxc.dev/manager/");
            }
        });

    }

    public void setLocal(Activity activity, String langcode){
        Locale locale = new Locale(langcode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

    }
}