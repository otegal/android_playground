package com.example.asyncsample

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lvCityList = findViewById<ListView>(R.id.lvCityList)
        val cityList: MutableList<MutableMap<String, String>> = mutableListOf()

        var city = mutableMapOf("name" to "大阪","id" to "270000")
        cityList.add(city)
        city = mutableMapOf("name" to "神戸", "id" to "280010")
        cityList.add(city)
        city = mutableMapOf("name" to "豊岡", "id" to "280020")
        cityList.add(city)
        city = mutableMapOf("name" to "京都", "id" to "260010")
        cityList.add(city)
        city = mutableMapOf("name" to "舞鶴", "id" to "260020")
        cityList.add(city)
        city = mutableMapOf("name" to "奈良", "id" to "290010")
        cityList.add(city)
        city = mutableMapOf("name" to "風屋", "id" to "290020")
        cityList.add(city)
        city = mutableMapOf("name" to "和歌山", "id" to "300010")
        cityList.add(city)
        city = mutableMapOf("name" to "潮岬", "id" to "300020")
        cityList.add(city)

        val from = arrayOf("name")
        val to = intArrayOf(android.R.id.text1)

        val adapter = SimpleAdapter(applicationContext, cityList, android.R.layout.simple_list_item_1, from, to)
        lvCityList.adapter = adapter
        lvCityList.onItemClickListener = ListItemClickListener()
    }

    private inner class ListItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val item = parent.getItemAtPosition(position) as Map<String, String>
            val cityName = item["name"]
            val cityId = item["id"]

            val tvCityName = findViewById<TextView>(R.id.tvCityName)
            tvCityName.text = cityName + "の天気："

            val receiver = WeatherInfoReceiver()
            receiver.execute(cityId)
        }
    }

    private inner class WeatherInfoReceiver() : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String): String {
            val id = params[0]
            val urlStr = "http://weather.livedoor.com/forecast/webservice/json/v1?city=${id}"

            val url = URL(urlStr)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.connect()

            val stream = con.inputStream
            val result = is2String(stream)
            con.disconnect()
            stream.close()

            return result
        }

        override fun onPostExecute(result: String) {
            val rootJSON = JSONObject(result)
            val descriptionJSON = rootJSON.getJSONObject("description")
            val desc = descriptionJSON.getString("text")
            val forecasts = rootJSON.getJSONArray("forecasts")
            val forecastNow = forecasts.getJSONObject(0)
            val telop = forecastNow.getString("telop")

            val tvWeatherTelop = findViewById<TextView>(R.id.tvWeatherTelop)
            val tvWeatherDesc = findViewById<TextView>(R.id.tvWeatherDesc)
            tvWeatherTelop.text = telop
            tvWeatherDesc.text = desc
        }

        private fun is2String(stream: InputStream): String {
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var line = reader.readLine()
            while (line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            return sb.toString()
        }
    }
}