# PackageTracker
An Android application that allows a user to enter in the tracking information for any package and outputs package information such as the courier, current status, and origin country using the TrackingMore API. The app also supports English, Spanish, French, and German languages for localization purposes using the IBM Watson Language Translator API

The purpose of this app is to allow users to input a tracking number from various shipping companies such as USPS and UPS.  In the following screenshot, the user enters a tracking number for a package being delivered. The number is entered above the green line and then the user will press the search button. The app will then display carrier and status information for the tracking number. The only small bug existing in the application currently is that the user will likely have to click the search button at least 3 times before the information is displayed.

![Picture1](https://user-images.githubusercontent.com/74629823/116757627-6e198e80-a9dc-11eb-82e5-9111de1279f6.png)

The application also includes settings for dark mode and different languages to increase the accessibility, allowing the app to reach more users. As can be seen here, the app has options for a dark themed interface and 4 languages that information can be translated to.
 
![Picture2](https://user-images.githubusercontent.com/74629823/116757630-6eb22500-a9dc-11eb-9ba9-899391561939.png)


Dark Mode on:  
 
![Picture3](https://user-images.githubusercontent.com/74629823/116757631-6eb22500-a9dc-11eb-991e-9e21c04bf32d.png)


Translated into French, Dark mode off: 
 
![Picture4](https://user-images.githubusercontent.com/74629823/116757633-6eb22500-a9dc-11eb-9863-82835c92a607.png)

Translated into Spanish, dark mode on:
 
![Picture5](https://user-images.githubusercontent.com/74629823/116757635-6eb22500-a9dc-11eb-8391-d16b3a6dabcd.png)

Below is a short list of tracking numbers for testing purposes of the application:

61292700076323908785

1ZA0T5860393120163

9405511899560929759324

NOTE: No API keys have been added to these program files, if you wish to run this program yourself, you must make an account at www.trackingmore.com and https://www.ibm.com/watson/services/language-translator/.
Both of which are free to create and implement into any project
