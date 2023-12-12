import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class Monitor_background {

    public Monitor_background() throws IOException, InterruptedException {

    }


    void run() throws IOException {
        while (true){
            List<Device> list_device = new ArrayList<>();

            URL url = new URL("https://server.uitprojects.com/device/monitor");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(120000);
            int status = con.getResponseCode();
            System.out.println(status);
            InputStream reader = con.getInputStream();
            int count;
            BufferedReader char_reader = new BufferedReader(new InputStreamReader(reader));

            String[] device_name_list = char_reader.readLine().split(",");

            count = device_name_list.length;

            for (int i = 0; i < device_name_list.length; i++)
                list_device.add(new Device(device_name_list[i]));


            for (int i = 0; i < count; i++) {
                byte[] buffer = new byte[4];
                reader.read(buffer);
                list_device.get(i).is_online = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
            }

            for (int i = 0; i < count; i++) {
                System.out.println(list_device.get(i).device_name);
                System.out.println(list_device.get(i).is_online);

            }

        }
    }
}


