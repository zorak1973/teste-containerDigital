package banco.conexao.com.marvel;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by bira on 11/05/2017.
 */


public class Request extends AsyncTask<Void,Void,String> {

    Context c;
    Conector cr;
    GridView gv;
    String url = null;
    ProgressDialog pd;
    HttpURLConnection con;
    String inicial;

    public Request(Context c, String url, GridView gv, String ini){

        this.c = c;
        this.url = url;
        this.gv = gv;
        this.inicial = ini;

    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();

        pd=new ProgressDialog(c);

        pd.setMessage("Carregando dados...");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        String pst = "u";
        HttpURLConnection con = cr.conectar(url);
        if (con == null) {
            return "Erro ao conectar!";
        }

        InputStream is = null;

        try {
            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            pst = this.packData();

            writer.write(pst);
            writer.flush();
            writer.close();
            os.close();
            con.connect();

        } catch (IOException e1) {

            e1.printStackTrace();
            return e1.toString();

        } finally {

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        try {

            int response_code = con.getResponseCode();

            // Check if successful connection made
            if (response_code == con.HTTP_OK) {
                try{
                    // Read data sent from server
                    InputStream input = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("RESULTADO", "caiu no catch do response code");
                    return e.toString();
                } finally {
                    con.disconnect();
                }

            } else {
                Log.i("RESULTADO", "caiu no else do http.ok");
                return("Erro ao conectar!");
            }

        } catch (IOException e) {
            Log.i("RESULTADO", "caiu no catch da resposta da query PHP");
            e.printStackTrace();
            return e.toString();

        } finally {
            Log.i("Estado","Fim do POST");
        }

    }

    @Override
    protected void onPostExecute(String i){
        /*pd.dismiss();
        Log.i("RES", i);
        DataParser prs = new DataParser(c, gv, i);
        prs.execute();*/

    }

    public String packData()
    {

        JSONObject jo=new JSONObject();
        StringBuffer packedData=new StringBuffer();

        try
        {

            jo.put("st2",inicial);
            Iterator it=jo.keys();

            do {
                String key=it.next().toString();
                String value=jo.get(key).toString();
                packedData.append(URLEncoder.encode(key,"UTF-8"));
                //packedData.append(key);
                packedData.append("=");
                packedData.append(URLEncoder.encode(value,"UTF-8"));
                //packedData.append(value);

            }while (it.hasNext());

            return packedData.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
