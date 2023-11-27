package com.uit.sensordht.API;

import android.util.Log;

import com.uit.sensordht.Interface.APIInterface;
import com.uit.sensordht.Interface.CreateUserCallback;
import com.uit.sensordht.Model.User;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIManager {
    static final APIClient apiClient = new APIClient();
    static final APIInterface createAccount = apiClient.getRetrofitInstance().create(APIInterface.class);

    public static void fnCreateAccount(String email, String password, String username, CreateUserCallback callback)
    {
        Call<String> call = createAccount.createAccount(email, password, username);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful())
                {
                    Log.d("CALL API", "Create Account Success");
                    callback.onSuccess("Create Account Success");
                }
                else
                {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("CALL API", "Error response: " + errorBody);
                        callback.onFailure(errorBody);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("CALL API", "Network error: " + t.getMessage());
            }
        });
    }
}
