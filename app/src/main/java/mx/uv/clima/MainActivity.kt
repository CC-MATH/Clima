package mx.uv.clima

import WeatherByCity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import mx.uv.tftstatistics.GsonRequest
import mx.uv.tftstatistics.VolleySingleton


class MainActivity : AppCompatActivity() {

    lateinit var description : TextView
    lateinit var temp : TextView
    lateinit var wind : TextView
    lateinit var cloudiness : TextView
    lateinit var pressure : TextView
    lateinit var humidity : TextView
    lateinit var rain : TextView
    lateinit var sunrise : TextView
    lateinit var sunset : TextView
    lateinit var coords : TextView
    lateinit var iconImage: ImageView

    val ACTIVITY_TAG = "MainActivity"

    val API_KEY = "&appid=156d7086924563e0b3958c649f7f0393"
    val URL_BYCITY = "https://api.openweathermap.org/data/2.5/weather?q="
    val URL_PARAMS = "&units=metric&lang=sp"
    var ciudad = "Veracruz"
    val mapHeaders : MutableMap<String, String> = mutableMapOf<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        supportActionBar?.subtitle = ciudad

        description = findViewById<TextView>(R.id.description)
        temp = findViewById<TextView>(R.id.temp)
        wind = findViewById<TextView>(R.id.txt_wind)
        cloudiness = findViewById<TextView>(R.id.txt_cloudiness)
        pressure = findViewById<TextView>(R.id.txt_preassure)
        rain = findViewById<TextView>(R.id.txt_rain)
        sunrise = findViewById<TextView>(R.id.txt_sunrise)
        sunset = findViewById<TextView>(R.id.txt_sunset)
        coords = findViewById<TextView>(R.id.txt_coords)
        iconImage = findViewById<ImageView>(R.id.icon)

        BuscarClima(ciudad)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflateMenu = menuInflater
        inflateMenu.inflate(R.menu.mytoolbar, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener( object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Toast like print
                Toast.makeText(applicationContext, "SEARCH: $query", Toast.LENGTH_LONG).show()
                ciudad = query
                BuscarClima(ciudad)
                searchItem.collapseActionView()
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                //Text Changed
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.action_favorite -> {
            // User chose the "Favorite" action, mark the current item as a favorite...
            Toast.makeText(this, "Moved to favorite list", Toast.LENGTH_LONG).show()
             true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }




    private fun BuscarClima(ciudad : String){
        Log.d(ACTIVITY_TAG,URL_BYCITY + ciudad + URL_PARAMS + API_KEY)
        val requestSummoner: GsonRequest<WeatherByCity> = GsonRequest<WeatherByCity>(
            URL_BYCITY + ciudad + URL_PARAMS + API_KEY,
            WeatherByCity::class.java,
            mapHeaders,
            myWeatherResponse(),
            myRequestErrorListener()
        )

        VolleySingleton.getInstance(this).addToRequestQueue(requestSummoner)
    }

    private fun myWeatherResponse() : Response.Listener<WeatherByCity> {
        return Response.Listener<WeatherByCity> { response ->
            Log.d("CLIMA", "C: ${response.name}")
            supportActionBar?.subtitle = ciudad
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy  HH:mm:ss ")
            var sunriseDate = java.util.Date(response.sys.sunrise * 1000)
            var sunsetDate = java.util.Date(response.sys.sunset * 1000)

            description.text = response.weather.get(0).description
            temp.text = response.main.temp.toString()//(round((response.main.temp - 273.15 )* 100) / 100).toString() + "°C"
            wind.text = response.wind.speed.toString() + "m/s | " + response.wind.deg.toString() + "°"
            cloudiness.text = response.clouds.all.toString() + "%"
            pressure.text = response.main.pressure.toString() + " hPa"
            //rain.text = response.rain.h.toString()
            sunrise.text = sdf.format(sunriseDate)
            sunset.text = sdf.format(sunsetDate)


            val url_image = "https://openweathermap.org/img/wn/"+ response.weather.get(0).icon +"@2x.png"
            //val url_image = "https://raw.githubusercontent.com/RafaelAndrade17/SPOTIFEI-MOBILE/master/album1.jpg"
            //Loading image from below url into imageView
            Glide.with(this)
                .load(url_image)
                .apply(RequestOptions().override(200, 200))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)

                .into(iconImage)
        }
    }



    private fun myRequestErrorListener(): Response.ErrorListener {
        return Response.ErrorListener { error ->
            Log.e("ERROR", error.toString())
            Toast.makeText(this,"Verifique el nombre de la ciudad",Toast.LENGTH_LONG).show()
        }
    }




}
