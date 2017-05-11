package banco.conexao.com.marvel;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.app.ListActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HeroesActivity extends ListActivity {
    String address = "http://gateway.marvel.com:443/v1/public/characters?";
    String apikey = "782dd73132c3002fdb5189cb47ae4923";
    String prvKey = "4694bda546640a0bd12610e10d37875e6fe3dd95";
    ListView lv;
    SimpleAdapter adapter;

    InputStream is = null;
    String line = null;
    String result = null;
    String[] data;
    ArrayList<HashMap<String,String>> data2 = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heroes);

       // StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

        getData();

        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);

        String[] from = new String[]{"name", "thumbnail"};
        int[] to = new int[]{R.id.nomes, R.id.thumb};
        SimpleAdapter adapter = new SimpleAdapter(this, data2, R.layout.activity_list, from, to);
        setListAdapter(adapter);

    }


    private void getData() {
        String hashe = md5("1"+prvKey+apikey);
        String endereco = address+"ts=1&apikey="+apikey+"&hash="+hashe;
        Log.i("End",endereco);
        try {
            URL url = new URL(endereco);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            while((line = br.readLine())!= null){
                sb.append(line+"\n");
            }

            is.close();
            result = sb.toString();

        }catch (Exception e){

            e.printStackTrace();

        }
        try{
            JSONArray js = new JSONArray(result);
            JSONObject jo = null;

            data = new String[js.length()];

            for(int i = 0; i < js.length(); i++){

                jo = js.getJSONObject(i);
                String name = jo.getString("name");
                String thumb = jo.getString("path")+jo.getString("extension");

                //data[i] = jo.getString("Name");
                //data[i] = new String[]{jo.getString("Name"), jo.getString("Team")}

                HashMap<String, String> item = new HashMap<String, String>();
                item.put("name", name);
                item.put("thumbnail", thumb);
                data2.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
