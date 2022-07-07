package com.siroccomobile.adtonos.thundersdk.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.siroccomobile.adtonos.thundersdk.api.*

class ExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        //it can be context from application or activity
        ATThunderSDK.initialize(context = applicationContext)

        //after initialization we can add a callback to be notified about sdk setup
        waitForStartWithCallback()

        //or wait until flag isStarted is true
        //waitIsStarted()

        /*
        we can to ask user about consents for following options:
            - AllowStorage
            - AllowPreciseGeolocation
            - AllowUseForPersonalisedContent
            - AllowProcessing
        when user accepted consents -> use AdTonosConsent.AllowAll
        when user declined consents -> use AdTonosConsent.None
        or
        we can ask user later about permissions and override consents by saveConsents method

        After first invoke start() method, consents are saved in application and we can get it by
        invoke loadLatestConsents method.
        We can also save consents manually by invoke saveConsents method.

        Below example when user accept all consents:
        */

        var consents = ATThunderSDK.loadLatestConsents()
        if (consents == null) {
            consents = AdTonosConsent.AllowAll
        }

        //IGNORE IN LITE VERSION OF SDK
        //Invoke this method with your number eight key before calling start
        ATThunderSDK.setNumberEightKey("U71E94V86CT9ZXY98ABNMFLQ0Y9B")

        //it can be context from application or activity
        ATThunderSDK.start(applicationContext, consents)
    }

    private fun waitForStartWithCallback() {
        /*
        After initialization we can add a callback to be notified about sdk setup
        If the permissions are granted (or denied), the onStarted method will be called
        on main thread almost immediately after the start method
        */
        ATThunderSDK.addCallback(object : ThunderCallback {
            override fun onStarted() {
                Log.d("ADTONOS", "SDK Started")

                /*
                After sdk is started we can use builder to get vast url
                and pass it to request for ads on another thread
                 */
                buildUrl()
            }

            override fun onError(error: ThunderError) {
                Log.e("ADTONOS", error.errorMessage ?: error.toString())
            }
        })
    }

    private fun waitIsStarted() {
        Thread {
            do {
                /*
                The isStarted method returns true when system permissions are obtained from the user.
                Therefore, the first startup may increase the time to wait for the confirmation flag.
                If the permissions are granted (or denied), the isStarted method returns true
                almost immediately after the start method
                 */
                Thread.sleep(5000)
            } while (!ATThunderSDK.isStarted())

            //After sdk is started we can use builder to get vast url or pass it to request for ads
            buildUrl()
        }.start()
    }

    private fun buildUrl() {
        val builder = ATThunderSDK.createBuilder()

        val adTonosLink = builder
            .setAdTonosKey("KT267qyGPudAugiSt")  // Sets developer key.
            //.setLanguage("en")                 // Sets user language, if different than a system defined.
            //.setAdType(VastAdType.bannerAd)    // Sets the type of ad. By default VastAdType.regular is set.
            .build()

        Log.d("ADTONOS", "YOUR VAST LINK $adTonosLink")
    }

    override fun onDestroy() {
        ATThunderSDK.dispose()
        super.onDestroy()
    }
}