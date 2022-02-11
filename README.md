# Thunder SDK by AdTonos

Url generator for personalized VAST audio advertisement.
Developers should only use the AdTonosSDK class to get a personalised link to VAST.
Below is a tutorial on how to add the library to your project and how to use it. More information on specific methods can be found in the documentation.

## Minimum requirements

* minSdkVersion is set to 24
* targetSdkVersion must be set at least to 31
* multiDexEnabled shall be set to true

## Installation

The method of installation depends on how the library was given. If an aar file was provided then adding to the project is by manual means.
Regardless of how you obtained the library, you must add the following code to the application manifest between the application tags:

```xml
<activity android:name="com.siroccomobile.linkgenerator.adtonos.ui.AdTonosActivity"
android:theme="@style/AppTheme.Transparent" />
```

### Manual installation

Copy the aar file to the "libs" directory of your project.

#### Add Repository

In the base project gradle file, find the "repositories" section and add the following entry to it:

```groovy
allprojects {
    repositories {
        ... other repositiories
        maven {
            url 'https://repo.numbereight.ai/artifactory/gradle-release-local'
        }
    }
}
```

#### Add dependencies

In the application gradle file, find the "dependencies" section and add the following entries to it.

```groovy
implementation 'com.siroccomobile.adtonos:adtonos-linkgenerator:1.0@aar'
implementation('ai.numbereight.sdk:nesdk:3.0.2@aar') {
    transitive = true
}
implementation 'ai.numbereight.sdk:audiences:3.0.2'
// The following libraries can be updated according to project needs. 
// The versions listed are recommended.
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9'
implementation 'com.google.android.gms:play-services-ads-identifier:17.1.0'
implementation 'com.google.guava:guava:28.0-android'
implementation "org.jetbrains.kotlin:kotlin-stdlib:1.5.31"
implementation 'androidx.core:core-ktx:1.7.0'
implementation 'androidx.appcompat:appcompat:1.4.0'
```

## Usage

* Call AdTonosSDK.initialize method at initialization time of the app.
* Call AdTonosSDK.start method once the user has agreed to the privacy policy by providing the type of consent.
* Call AdTonosSDK.createBuilder method and fill AdTonos key with AdTonosVastUrlBuilder.setAdTonosKey
* On the instance of AdTonosVastUrlBuilder call AdTonosVastUrlBuilder.build method to get URL
* Repeat last step every time VAST link is required
* When application is being destroyed call AdTonosSDK.dispose method to free all resources

### Remarks

Where to find AdTonosKey? It's provided by AdTonos on the portal and can be extracted from
the link:

> <https://play.adtonos.com/xml/XXXXX/vast.xml>

where XXXXX is the AdTonos key. AdTonos usually provides two links, one for testing purposes and one for release. During development testing key shall be used.

### Sample

```kotlin
 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        AdTonosSDK.initialize(context = applicationContext)

        var consents = AdTonosSDK.loadLatestConsents()
        if (consents == null) {
            consents = AdTonosConsent.AllowAll
        }

        AdTonosSDK.start(applicationContext, consents)
        waitIsStarted() //simulate flow of asking permissions

    }

    private fun waitIsStarted() { //this is simulation of the flow. 
        Thread {
            do
            {
                Thread.sleep(1000)
            } while (!AdTonosSDK.isStarted())
            buildUrl()
        }.start()
    }

    private fun buildUrl() {
        val builder = AdTonosSDK.createBuilder()

        val adTonosLink = builder
            .setAdTonosKey("")  //PASS HERE YOUR ADTONOS KEY
            .build()

        Log.d("ADTONOS", "YOUR VAST LINK $adTonosLink")
    }

    override fun onDestroy() {
        AdTonosSDK.dispose()
        super.onDestroy()
    }
```

## System permissions

During the first startup, the library will ask the user to grant system permissions. The process is automatic.

The following system permissions are used by the library. They will be merged during build, so you do not need to declare them again in the application manifest.

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true" />
<uses-feature android:name="android.hardware.location" android:required="false" />
<uses-feature android:name="android.hardware.sensor.gyroscope" android:required="false" />
<uses-feature android:name="android.hardware.sensor.barometer" android:required="false" />
<uses-feature android:name="android.hardware.sensor.compass" android:required="false" />
<uses-feature android:name="android.hardware.sensor.light" android:required="false" />
<uses-feature android:name="android.hardware.sensor.proximity" android:required="false" />
```

## Consents

Below is a list of what is included in the AdTonosConsent.AllowAll option

* `PROCESSING` - Allow processing of data.
* `SENSOR_ACCESS` - Allow use of the device's sensor data.
* `STORAGE` - Allow storing and accessing information on the device.
* `USE_FOR_AD_PROFILES` - Allow use of technology for personalised ads.
* `USE_FOR_PERSONALISED_CONTENT` - Allow use of technology for personalised content.
* `USE_FOR_REPORTING` - Allow use of technology for market research and audience insights.
* `USE_FOR_IMPROVEMENT` - Allow use of technology for improving  products.
* `LINKING_DEVICES` - Allow linking different devices to the user through deterministic or probabilistic means.
* `USE_OF_DEVICE_INFO` - Allow use of automatically provided device information such as manufacturer, model, IP addresses and MAC addresses.
* `USE_FOR_SECURITY` - Allow use of independent identifiers to ensure the secure operation of systems.
* `USE_FOR_DIAGNOSTICS` - Allow processing of diagnostic information using an independent identifier to ensure the correct operation of systems.
* `PRECISE_GEOLOCATION` - Allow use of precise geolocation data (within 500 metres accuracy).

## Documentation

Please see documentation to get information about possible errors and other methods that can be invoked.

# Package com.siroccomobile.linkgenerator.adtonos.api

