# Module ThunderSDK
## Overview

**Thunder SDK** is a framework for your in-app audio advertisement preparation. The framework provides automatically generated, personalized advertisements URLs where VAST resources can be found. Use it to prepare downloadable advertisement content.

# Tutorial
Below is a tutorial on how to add the library to your project and how to use it. More information on specific methods can be found in the documentation.

## Minimum requirements

* minSdkVersion is set to 24
* targetSdkVersion must be set at least to 32
* multiDexEnabled shall be set to true

## Installation
### Choose SDK versions
You have to decide which version of SDK you want to use. Two available:

**ThunderSDK** - contains SDK functionality + extra user targeting provided by [NumberEight](https://numbereight.ai/); this version will ask user for extra permissions (i.e. location)

> [NumberEight](https://numbereight.ai/) is an AI software, which predicts the live context of a user (e.g. running, commuting) from sensors present in the device, and then packages them neatly into ID-less behavioural audiences (e.g. joggers, frequent shoppers).
 
**ThunderLiteSDK** - contains SDK functionality without extra user targeting; this version won't ask user for extra permissions

The method of installation depends on how the library was given. If an aar file was provided then adding to the project is by manual means.
Regardless of how you obtained the library, you must add the following code to the application manifest between the application tags:

```xml
<activity android:name="com.siroccomobile.adtonos.thundersdk.ui.ThunderActivity"
android:theme="@style/AppTheme.Transparent" />
```

### Manual installation

Copy the aar file to the "libs" directory of your project.

#### Add Repository

In the base project gradle file, find the "repositories" section and add the following entry to it, if you want to use full version of library:

```groovy
allprojects {
    repositories {
        ... other repositiories
        maven {
            // Required for full version with additional targeting
            url 'https://repo.numbereight.ai/artifactory/gradle-release-local'
        }
    }
}
```

#### Add dependencies

In the application gradle file, find the "dependencies" section and add the following entries to it.

```groovy
// Required for lite version
implementation('com.siroccomobile.adtonos:thunder-lite-sdk:1.0@aar')

// Required for full version with additional targeting
implementation('com.siroccomobile.adtonos:thunder-sdk:1.0@aar')
implementation('ai.numbereight.sdk:nesdk:3.4.0@aar') { transitive = true }
implementation('ai.numbereight.sdk:audiences:3.4.0')

// The following libraries can be updated according to project needs. 
// The versions listed are recommended.
implementation('org.jetbrains.kotlin:kotlin-stdlib:1.7.0')
implementation('androidx.core:core-ktx:1.8.0')
implementation('androidx.appcompat:appcompat:1.4.2')
implementation('org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2')
implementation('androidx.multidex:multidex:2.0.1')
implementation('com.google.guava:guava:31.1-android')
implementation('com.google.android.gms:play-services-ads-identifier:18.0.1')
```

## System permissions

During the first startup, the library will ask the user to grant system permissions. The process is automatic.

The following system permissions are used by the library. They will be merged during build, so you do not need to declare them again in the application manifest.

```xml
<!-- Required for full and lite version -->
<uses-permission android:name="com.google.android.gms.permission.AD_ID"/>

<!-- Required for full version -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true" />
<uses-feature android:name="android.hardware.location" android:required="false" />
<uses-feature android:name="android.hardware.sensor.gyroscope" android:required="false" />
<uses-feature android:name="android.hardware.sensor.barometer" android:required="false" />
<uses-feature android:name="android.hardware.sensor.compass" android:required="false" />
<uses-feature android:name="android.hardware.sensor.light" android:required="false" />
<uses-feature android:name="android.hardware.sensor.proximity" android:required="false" />
```

## Consents

To work properly, the SDK needs consents. Depending on the region, please ask the user if they agree to the following.
In some regions, such as the EU, consent is required to allow third-parties to store data on users devices for example.

### AdTonosConsent.AllowAll

Below is a list of what is included in this option:

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

### AdTonosConsent.None

It is possible to start adTonos with option without accepted consents, but it will not collect data about the user then, so the advertisement will not be personalized appropriately to the user.

## Sequence of steps

* Call ATThunderSDK.initialize method at initialization time of the app.
* Call ATThunderSDK.addCallback to add callback for sdk state changes
* Call ATThunderSDK.start method once the user has agreed to the privacy policy by providing the type of consent.
* Call ATThunderSDK.createBuilder method and fill AdTonos key with ThunderVastUrlBuilder.setAdTonosKey
* On the instance of ThunderVastUrlBuilder call ThunderVastUrlBuilder.build method to get URL
* Repeat last step every time VAST link is required
* When application is being destroyed call ATThunderSDK.dispose method to free all resources

## Start SDK and create builder

### Initialization

The first necessary step in the project is to call the ATThunderSDK.Initialize method. It should be called at the start of the application, when the other components are created. The method is safe to be called multiple times. The Initialize method must be called every time the application starts. Below is an example of how this should be done:

```kotlin
ATThunderSDK.initialize(context = applicationContext)
```

### Number eight key

**This chapter is required for full version with additional targeting.**
Before calling start method please invoke setNumberEightKey method with your obtained key to ensure proper work of sdk.

```kotlin
ATSandstormSDK.setNumberEightKey("KEY")
```

### Start

The library requires certain permissions and user consents for data collection.
Therefore, one of the initial screens should show the user the terms and conditions containing
the necessary information about the use and processing of personal data. This is done when
the user first interacts with the application, so this is a good time to call the Start method.
The method takes the consent flag as a parameter and then asks the user for the system permissions
necessary for the application to work. The start method must be called every time the application starts.
Additionally, the LoadLatestConsents method can be used, which returns the most recently granted consents.
Below is an example of how this can be done:

Pass one of available targeting types as a function parameter:
- AdTonosConsent.None
- AdTonosConsent.AllowAll

```kotlin
ATThunderSDK.start(context = applicationContext, consent = AdTonosConsent.None)
```

To check if SandstormSDK is started it is possible to use the isStarted variable.

```kotlin
ATThunderSDK.isStarted()
```

#### Save consents

In case you want to change the consents, you can use the method save.

```kotlin
ATThunderSDK.saveConsents(context, AdTonosConsent.None)
```

#### Load consents

You can also get the last set consents using the method loadLatestConsent.

```kotlin
ATThunderSDK.loadLatestConsents()
```

### Generate VAST link

To generate a VAST link, create a builder and then set the AdTonos key. Optionally, if you want to use a language other than preferred in the system, you can set it with ``.setLanguage(lang:String)``. Language must be provided in **ISO-639-1** or **ISO-639-2** format. 

```kotlin
val builder = ATThunderSDK.createBuilder()
    .setAdTonosKey("XXXXX")          // Sets developer key.
    .setLanguage("en")               // Sets user language, if different than a system defined.
    .setAdType(VastAdType.bannerAd)  // Sets the type of ad. By default VastAdType.regular is set.
    .build()
```
* **AdTonos key** - Where to find AdTonosKey? 
It's provided by AdTonos on the portal and can be extracted from the link:

 > [https://play.adtonos.com/xml/XXXXX/vast.xml]()
 
 where XXXXX is the AdTonos key. AdTonos usually provides two links, one for testing purposes and one for release. During development testing key shall be used.

## Sample usage

```kotlin
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
        .setAdTonosKey("")               // Sets developer key.
        .setLanguage("en")               // Sets user language, if different than a system defined.
        .setAdType(VastAdType.bannerAd)  // Sets the type of ad. By default VastAdType.regular is set.
        .build()

    Log.d("ADTONOS", "YOUR VAST LINK $adTonosLink")
}

override fun onDestroy() {
    ATThunderSDK.dispose()
    super.onDestroy()
}
```

### Disposing

To release the memory used by the library completely, call the dispose method. This is usually done when exiting the application. Note that after the dispose method is executed, the only way to restore ad functionality is to call initialize and start again.

```kotlin
// Invoked from main activity
override fun onDestroy() {
    ATThunderSDK.dispose()

    super.onDestroy()
}
```

## Documentation

Please see extended documentation from repository to get information about possible errors and other methods that can be invoked.

# Package com.siroccomobile.adtonos.thundersdk.api

