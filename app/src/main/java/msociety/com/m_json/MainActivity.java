package msociety.com.m_json;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Listview variable.
    ListView m_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Listview initialize.
        m_list = (ListView) findViewById(R.id.m_listview);

        // Methodu tetikledim.
        get();
    }

    public void get() {

        // Volley kütüphanesi artık android'in native kütüphanesidir. Bunu build.gradle altına ekledim oraya da yorum yazdım.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Url bilgisi.
        String url = "http://www.yakinbul.com/_ws/veri.php?data=kategori";

        // Volley istek çağrısı oluşturdum.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // Linkten gelen response yazıdır. Buradan sonra nereye paslamak istersen oraya paslayabilirsin.
                // Ben parse_json denilen methoda pasladım.
                parse_json(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Herhangi bir hata dönemsi durumunda.
                Log.i("JJ", "Hata!");
            }
        });

        // İsteğin başlamadı için sorguyu kuyruğa ekledik ve bununla başladı.
        queue.add(stringRequest);

    }

    public void parse_json(String response) {

        // Liste üzerine döşenecek denildi. Herhalde sadece kategori isimleri döşenir diye düşündüm.
        List<String> m_array_list_name = new ArrayList<>();

        // try içi bildiğin gibi ama sadece isimleri aldım yoksa listenin adaptörüne set edemem.
        try {
            JSONArray tokens = new JSONArray(response);

            for (int i = 0; i < tokens.length(); i++) {
                JSONObject jo = tokens.getJSONObject(i);
                int id = jo.getInt("ID");
                int katid = jo.getInt("IDKategori");
                String katbas = jo.getString("KategoriBaslik");
                String katres = jo.getString("KategoriResim");
                int altkat = jo.getInt("AltKategoriSayi");
                Kategori kategori = new Kategori(id, katid, katbas, katres, altkat);

                // Kategoriler değil, kategori isimleri.
                m_array_list_name.add(katbas);
            }

            // Basit custom olmayan bir liste adaptörü oluşturdum. Bu malesef şart. Adaptörsü liste çalışmaz.
            // Yalnız bir trick var. m_listview_row'u kendim layout adı altında oluşturdum. Res altında görürsün.
            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.m_listview_row, m_array_list_name);

            // En tepede oluşturduğumuz initialize ettiğimiz listeye adaptörü verdiğimde artık görmüş oluyoruz.
            m_list.setAdapter(itemsAdapter);

            // Eğer ki tıklanan sıraları ya da görselleri almak istersen diye bir adet de listener ekledim.
            m_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Bunu da toast olarak ekrana bastırdım.
                    Toast.makeText(MainActivity.this, String.valueOf(position + 1) + ". pozisyona tıkladınız.", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception ex) {
            // Hata alması durumunda.
            Log.i("JJ", "Hata! Catch block");
        }
    }
}

/* SON NOT:
* Manifest dosyasına internet permission'ı vermeyi unutmamamk gerek.
*/