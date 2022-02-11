package com.siroccomobile.adtonos.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.siroccomobile.linkgenerator.adtonos.api.AdTonosConsent
import com.siroccomobile.linkgenerator.adtonos.api.AdTonosSDK

class ExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        //it can be context from application or activity
        AdTonosSDK.initialize(context = applicationContext)

        /*
        we need to ask user about consents for following options:
            - AllowStorage
            - AllowPreciseGeolocation
            - AllowUseForPersonalisedContent
            - AllowProcessing
        when user accepted consents -> use AdTonosConsent.AllowAll
        when user declined consents -> use AdTonosConsent.None

        After first invoke start() method, consents are saved in application and we can get it by
        invoke loadLatestConsents method.
        We can also save consents manually by invoke saveConsents method.

        Below example when user accept all consents:
        */

        var consents = AdTonosSDK.loadLatestConsents()
        if (consents == null) {
            consents = AdTonosConsent.AllowAll
        }

        //it can be context from application or activity
        AdTonosSDK.start(applicationContext, consents)
        waitIsStarted() //simulate flow of asking permissions

    }

    private fun waitIsStarted() {
        Thread {
            do
            {
                /*
                The isStarted method returns true when system permissions are obtained from the user.
                Therefore, the first startup may increase the time to wait for the confirmation flag.
                If the permissions are granted (or denied), the isStarted method returns true
                almost immediately after the start method
                 */
                Thread.sleep(1000)
            } while (!AdTonosSDK.isStarted())
            buildUrl()
        }.start()
    }

    private fun buildUrl() {
        val builder = AdTonosSDK.createBuilder()

        val adTonosLink = builder
            .setAdTonosKey("")  //PASS HERE YOUR ADTONOS KEY
        //  .setLanguage("en") // optional param
            .build()

        Log.d("ADTONOS", "YOUR VAST LINK $adTonosLink")
    }

    override fun onDestroy() {
        AdTonosSDK.dispose()
        super.onDestroy()
    }

}