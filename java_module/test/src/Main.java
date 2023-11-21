
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class Main {
    public static void main(String[] args) throws IOException {
        URL url = new URL("https://server.uitprojects.com/live-data");
       // URL url = new URL("http://localhost/live-data");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setReadTimeout(30000);
        int status = con.getResponseCode();
        System.out.println(status);
        InputStream reader = con.getInputStream();
        byte[] temperature_bytes = new byte[4];
        byte[] humidity_bytes = new byte[4];
        byte[] time_bytes = new byte[4];
        float temperature_float;
        float humidity_float;
        int time_int;
        while (true) {
            try {
                reader.read(temperature_bytes, 0, 4);
                reader.read(humidity_bytes, 0, 4);
                reader.read(time_bytes,0,4);
            }
            catch (IOException e){
                reader.close();
                con.disconnect();
                System.out.println("Close connection because timeout ");
                return;
            }
            temperature_float = ByteBuffer.wrap(temperature_bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            humidity_float = ByteBuffer.wrap(humidity_bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            time_int = ByteBuffer.wrap(time_bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
            System.out.printf("Temperature: %s, Humidity: %s, Time: %s%n",temperature_float,humidity_float,time_int);


        }
    }

}