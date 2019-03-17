# The Android application

**Helsinki Timetables** is an application for Android smartphones which provides the actual timetables for all kind of public transport within Helsinki area.

[Official web site.][1]

The application uses [Reittiopas API][3] to download the full data and generate the database to be used offline. The full API documentation is [here][4].

# How to release

run the command:

```
gradle assembleRelease
```

result APK file will be found in `build/outputs/apk/release/`

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
     alt="Get it on Google Play"
     height="80">](https://play.google.com/store/apps/details?id=com.redblaster.hsl.main)
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/com.redblaster.hsl.main/)

  [1]: http://hsl.2rooms.net/
  [2]: http://hsl.2rooms.net/images/phone.png
  [3]: http://developer.reittiopas.fi
  [4]: http://developer.reittiopas.fi/pages/en/kalkati.net-xml-database-dump.php
