package com.johnk.packagetracker

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.tabs.TabLayout
import com.ibm.cloud.sdk.core.security.IamAuthenticator
import com.ibm.watson.language_translator.v3.LanguageTranslator
import com.ibm.watson.language_translator.v3.model.TranslateOptions
import com.ibm.watson.language_translator.v3.model.TranslationResult
import com.ibm.watson.language_translator.v3.util.Language
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tab2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.math.log


var code:String = ""
class MainActivity : AppCompatActivity(),
        Tab1Fragment.OnFragmentInteractionListener,
        Tab2Fragment.OnFragmentInteractionListener {
    lateinit var requestQueue: RequestQueue
    var output: String = ""
    var name: String = ""
    var trackNum: String = ""
    var country :String = ""
    var status:String = ""
    var id:String = ""
    var urlDetect:String = "https://api.trackingmore.com/v2/carriers/detect"
    var url2 :String = "https://api.trackingmore.com/v2/trackings/realtime"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        configureTabLayout()



    }



    private fun configureTabLayout() {

        //tab_layout.addTab(tab_layout.newTab().setText("Tab 1 Item"))
        // tab_layout.addTab(tab_layout.newTab().setText("Tab 2 Item"))

        tab_layout.addTab(
                tab_layout.newTab().setIcon(
                        android.R.drawable.ic_menu_mylocation
                )
        )
        tab_layout.addTab(
                tab_layout.newTab().setIcon(
                        android.R.drawable.ic_menu_preferences
                )
        )

        val adapter = TabPagerAdapter(
                supportFragmentManager,
                tab_layout.tabCount
        )
        pager.adapter = adapter

        pager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(tab_layout)
        )
        tab_layout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    fun getData(view: View) {
        //WHERE ALL THE TRACKING MORE STUFF SHOULD GO
        var line = findViewById<EditText>(R.id.trackingText)
        var trackingNumber = line.text.toString()

        requestQueue = Volley.newRequestQueue(this)
        //create object request

        //POST for Detect
        val params = HashMap<String, String>()
        params["tracking_number"] = trackingNumber
        val jsonObject = JSONObject(params as Map<*, *>)


        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
                urlDetect,
                jsonObject,
                Response.Listener<JSONObject> { response ->
                    try {
                        var theList = response.getJSONArray("data")
                        var Data =
                                theList.getJSONObject(0)

                        name = Data.getString("name") 
                        code = Data.getString("code")
                        println("this is the code: "+ code)
                        //output = "Name: " + name + "\nCode: " + code

                    } catch (ex: JSONException) {
                        Log.e("JSON Error", ex.message!!)
                    }

                },
                Response.ErrorListener {}){
            override  fun getHeaders() : MutableMap<String, String> {
                val header = HashMap<String, String>()
                header["Content-Type"] = "application/json; charset=UTF-8"
                header["Trackingmore-Api-Key"] = /* INSERT API KEY HERE */
                return header
            }
        }

        requestQueue.add(jsonObjectRequest)

        //Get for Tracking Info
        val params3 = HashMap<String, String>()
        params3["tracking_number"] = trackingNumber
        params3["carrier_code"] = code
        val jsonObject3 = JSONObject(params3 as Map<*, *>)

        val jsonObjectRequest3 = object : JsonObjectRequest(Request.Method.POST, url2, jsonObject3,
                Response.Listener<JSONObject> { response ->
                    try {
                        println("Hello I am a PrintLn")
                        println("this is the code: "+ code)
                        //var info2 = response.getJSONObject("data").getJSONArray("items")
                        val arr: JSONArray = response.getJSONObject("data").getJSONArray("items")
                        for (i in 0 until arr.length()) {
                            val info: JSONObject = arr.getJSONObject(i)
                            status = info.getString("status")
                            if(status == "notfound")
                            {
                                status = "Delivered"
                            }
                            name = info.getString("carrier_code")
                            trackNum = info.getString("tracking_number")
                            country = info.getString("original_country")
                            if(country == "")
                            {
                                country = "United States"
                            }

                        }

                        output = "Tracking Number: "+trackNum+"\nName: " + name + "\nStatus: " + status + "\nCountry: " + country

                    } catch (ex: JSONException) {
                        Log.e("JSON Error", ex.message!!)
                    }

                },
                Response.ErrorListener {}){
            override  fun getHeaders() : MutableMap<String, String> {
                val header = HashMap<String, String>()
                header["Content-Type"] = "application/json; charset=UTF-8"
                header["Trackingmore-Api-Key"] = /* INSERT API KEY HERE */
                return header
            }
        }

        //end of JSON object request
        requestQueue.add(jsonObjectRequest3)
        translateData(output)
    }
    fun translateData(output: String){
        val thread = Thread(Runnable {
            try {
                val authenticator = IamAuthenticator(/* INSERT API KEY HERE */)
                val languageTranslator = LanguageTranslator("2018-05-01", authenticator)
                languageTranslator.serviceUrl =
                        "https://api.us-east.language-translator.watson.cloud.ibm.com/instances/26cd5dbf-6db3-4150-a212-39a087339629"
                var text = findViewById<TextView>(R.id.displayView)
                if (spanishRadio.isChecked) {
                    val translateOptions = TranslateOptions.Builder().addText(output).source(
                            Language.ENGLISH
                    ).target(Language.SPANISH).build()
                    val finish: TranslationResult =
                            languageTranslator.translate(translateOptions).execute().result
                    text.setText(finish.translations[0].translation)
                } else if (frenchRadio.isChecked) {
                    val translateOptions = TranslateOptions.Builder().addText(output).source(
                            Language.ENGLISH
                    ).target(Language.FRENCH).build()
                    val finish: TranslationResult =
                            languageTranslator.translate(translateOptions).execute().result
                    text.setText(finish.translations[0].translation)
                } else if (germanRadio.isChecked) {
                    val translateOptions = TranslateOptions.Builder().addText(output).source(
                            Language.ENGLISH
                    ).target(Language.GERMAN).build()
                    val finish: TranslationResult =
                            languageTranslator.translate(translateOptions).execute().result
                    text.setText(finish.translations[0].translation)
                } else {
                    text.setText(output)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        thread.start()

    }

    fun OnRadioClick(view: View) {
        if(englishRadio.isChecked){
            setLocale("en")
            recreate()
        }
        if(spanishRadio.isChecked){
            setLocale("es")
            recreate()
        }
        if(frenchRadio.isChecked)
        {
            setLocale("fr")
            recreate()
        }
        if(germanRadio.isChecked)
        {
            setLocale("de")
            recreate()
        }

    }

    fun setLocale(languageCode: String){
        var locale: Locale = Locale(languageCode)
        Locale.setDefault(locale)
        var config: Configuration = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        var editor: SharedPreferences.Editor = getSharedPreferences(
                "Settings",
                Context.MODE_PRIVATE
        ).edit()
        editor.putString("My_Lang", languageCode)
        editor.apply()
    }

    fun loadLocale(){
        var prefs:SharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        var language: String = prefs.getString("My_Lang", "").toString()
        setLocale(language)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }




}
