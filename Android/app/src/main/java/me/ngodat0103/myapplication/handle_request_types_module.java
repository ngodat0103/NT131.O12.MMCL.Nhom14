package me.ngodat0103.myapplication;
import android.os.Build;
import android.util.Log;
import com.google.gson.Gson;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class handle_request_types_module {
    static Map<String,String> request_message_Map;
    static void send_message_to_client(Map<String,String> request_message_Map) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        String request_message_json_format_String = new Gson().toJson(request_message_Map);
        byte[] encrypt_request_message_bytes = cipher_module.encrypt(request_message_json_format_String);
        client_connection_module.send(encrypt_request_message_bytes);
    }
    public static Map authentication(String username_primary,String password,String refresh_token) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        request_message_Map = new HashMap<>();
        request_message_Map.put("type","authentication");
        request_message_Map.put("username_primary",username_primary);
        request_message_Map.put("password",password);
        request_message_Map.put("device_name", Build.MODEL);
        request_message_Map.put("refresh_token",refresh_token);
        send_message_to_client(request_message_Map);
        Map response_from_server = client_connection_module.listen_response_from_server();
        return response_from_server;
    }
    public static void forgot_password(String username_primary,String email) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        request_message_Map = new HashMap<>();
        request_message_Map.put("type","forgot_password");
        request_message_Map.put("username_primary",username_primary);
        request_message_Map.put("email",email);
        request_message_Map.put("otp_valid","none");
        send_message_to_client(request_message_Map);
        Map response_from_server = client_connection_module.listen_response_from_server();
        System.out.println(response_from_server);
        Scanner otp_input = new Scanner(System.in);
        System.out.println(response_from_server.get("status"));
        if (response_from_server.get("status").equals("otp_sent")){
            System.out.println("Nhap OTP da gui ve tu email: ");
            request_message_Map.put("otp",otp_input.nextLine());
            send_message_to_client(request_message_Map);
            response_from_server = client_connection_module.listen_response_from_server();
            System.out.println(response_from_server);
            if (response_from_server.get("otp_valid").equals("valid")) {
                System.out.println("Nhap mat khau moi");
                request_message_Map.put("new_password", new Scanner(System.in).nextLine());
                request_message_Map.put("otp_valid","valid");
                send_message_to_client(request_message_Map);
                response_from_server = client_connection_module.listen_response_from_server();
                System.out.println(response_from_server);
            }
        }

    }

    public static Map get_weather() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        request_message_Map = new HashMap<>();
        request_message_Map.put("type","get_weather_data");
       send_message_to_client(request_message_Map);
       Map response_from_server =  client_connection_module.listen_response_from_server();
       String weather_data_String = response_from_server.get("weather_data").toString();
       Map weather_data_Map = new Gson().fromJson(weather_data_String,Map.class);
        return weather_data_Map;
    }
//    public static void upload_image_profile(String username_primary) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
//        JFileChooser fileChooser = new JFileChooser();
//        String image_file_path = "";
//        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        File new_file = new File("C:\\Users\\ngovu\\OneDrive\\mobile project");
//        fileChooser.setCurrentDirectory(new_file);
//        int result = fileChooser.showOpenDialog(null);
//        if (result == JFileChooser.APPROVE_OPTION){
//            image_file_path = fileChooser.getSelectedFile().getAbsolutePath();
//        }
//        request_message_Map = new HashMap<>();
//        request_message_Map.put("type","upload_image_profile");
//        request_message_Map.put("username_primary",username_primary);
//        FileInputStream image_open_file = new FileInputStream(image_file_path);
//        byte[] image_bytes = image_open_file.readAllBytes();
//        String image_encoded_base64_String = Base64.getEncoder().encodeToString(image_bytes);
//        request_message_Map.put("image_encoded_base64_string",image_encoded_base64_String);
//        handle_request_types_module.send_message_to_client(request_message_Map);
//        System.out.println(client_connection_module.listen_response_from_server());
//
//    }
    public static byte[] load_profile_image(String refresh_token) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        request_message_Map = new HashMap<>();
        request_message_Map.put("type","load_profile_image");
        request_message_Map.put("refresh_token",refresh_token);
        handle_request_types_module.send_message_to_client(request_message_Map);
       Map response_from_server_map =  client_connection_module.listen_response_from_server();
       byte[] image_bytes = client_connection_module.listen_response_from_server_large_file(response_from_server_map);
       return image_bytes;

    }
}
