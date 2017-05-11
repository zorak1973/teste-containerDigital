package banco.conexao.com.marvel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bira on 11/05/2017.
 */



public class Conector {

    public static HttpURLConnection conectar(String enderecoWeb){

        try{
            URL url = new URL(enderecoWeb);
            HttpURLConnection cn = (HttpURLConnection) url.openConnection();
            cn.setRequestMethod("POST");
            cn.setConnectTimeout(30000);
            cn.setReadTimeout(30000);
            cn.setDoInput(true);
            cn.setDoOutput(true);

            return cn;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}