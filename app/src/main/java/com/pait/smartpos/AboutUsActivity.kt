package com.pait.smartpos

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        if(ConnectivityTest.getNetStat(applicationContext)) {
            Toast.makeText(applicationContext,"Please Wait...",Toast.LENGTH_LONG).show();
            loadSite()
        }else{
            Toast.makeText(applicationContext,"You Are Offline",Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadSite(){
        val webView = findViewById<WebView>(R.id.webView)
        webView.loadUrl("http://paitsystems.com")
        webView.settings.javaScriptEnabled = true
    }
}
