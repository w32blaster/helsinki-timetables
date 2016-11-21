# The Android application

Helsinki Timetables is an application for AndroidTM which provides the actual timetables for all kind of public transport within Helsinki area. 

[Official web site.][1]

![image alt][2]

# How to sign the application

To sign:

	jarsigner -verbose -keystore w32blaster.keystore HelsinkiTimetables-u.apk w32blaster

To zip:

	~/Programs/adt-bundle-linux-x86_64-20131030/sdk/tools/zipalign -v 4 HelsinkiTimetables-u.apk HelsinkiTimetables.apk



  [1]: http://hsl.2rooms.net/
  [2]: http://hsl.2rooms.net/images/phone.png
