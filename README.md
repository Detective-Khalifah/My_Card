# My_Card

### Resume displaying app built as the second task of HNG9 internship program.

## Description
- The app's developed using Native Android technologies.
- The app comprises 2 [Activities](https://developer.android.com/guide/components/activities/intro-activities). Click here for more on [Activity](https://developer.android.com/reference/android/app/Activity) and its implementation

## Features
- Light/Day & Dark/Night Mode
- Light-weight support for large screens & landscape orientation
- Multiple language support (currently set automatically by device language -- accessible in settings)

## Libraries
- [Navigation](https://developer.android.com/guide/navigation/navigation-getting-started) -- a 1st party libary for adding seemless navigation to apps.

## Future plans
This app was simply built as a deliverable to the task at hand. Future plans include:
- Developing a richer interface.
- Adding more résumé details
- Integrating [WebView](https://developer.android.com/develop/ui/views/layout/webapps/webview) into the app, to keep user experience 'consistent'? on the app
- Hyperlinking of résumé sections to open on app browser
- Localise into more languages, and add menu to change between them.

## Challenges
- I used [ImageButton](https://developer.android.com/reference/android/widget/ImageButton)s for the Social Media profile buttons at first; this presented the challenge of inscrollability in the parent [LinearLayout](https://developer.android.com/develop/ui/views/layout/linear), even though it was inside a [ScrollView](https://developer.android.com/reference/android/widget/ScrollView); I wasted time trying to figure it out, until I decided to replace it with a LinearLayout of CardViews
- I did not start the project in time due to examination schedule, and whatnot, but whatever. {Note to self: Time management needs to be upped}


[Appetize Link](https://appetize.io/app/gcf5cegsarcfdqdj4k2etoum3u)
