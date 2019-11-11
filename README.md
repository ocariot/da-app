# OCARIoT Data Acquisition
[![License][license-image]][license-url] [![Vulnerabilities][known-vulnerabilities-image]][known-vulnerabilities-url] [![Commit][last-commit-image]][last-commit-url] [![Releases][releases-image]][releases-url] [![Contributors][contributors-image]][contributors-url] 

[![DownloadBrazil][apk-brazil]][apk-brazil-url] [![DownloadEurope][apk-europe]][apk-europe-url] 


Native Android application responsible for OCARIoT platform data acquisition. The application acts as an external services access token manager. Currently, only Fitbit service is available.

**Main Features:**
- Fitbit access token management:
  - The **Child** may grant access to the OCARIoT platform to collect data from their Fitbit account;
  - **Family**, **Educator** or **Health Professional** provider can grant access to the OCARIoT platform to collect data from children who have privileges to manage their data.
- Listing of data saved on OCARIoT platform:
  - Physical Activity;
  - Sleep;
  - Weight.
- Display of Heart Rate data collected in real time from Polar OH1 device.
- Support for Fitbit data synchronization with OCARIoT platform.
- Fitbit access revocation.

## Prerequisites
- Android SDK v28
- Latest Android Studio 3.5+

## Getting Started

1. **Clone this repository and import into Android Studio**

   ```console
   git clone https://github.com/ocariot/da-app.git
   
   ```
2. **Set up the environment:**
   - Make a copy of the `gradle.properties.example` file in the `/app` directory named `gradle.properties` _(This file will not be tracked by git because it is in .gitignore)_.
   - Change the values of the variables you find necessary according to your development and production environment.
3. **Open project in Android Studio:**
   - From the Android Studio menu, click File > Open.
     - Alternatively, on the "Welcome" screen, click > Open an existing Android Studio project.
   - Select the project folder and click OK.

## Generating signed APK
From Android Studio:
1. ***Build*** menu
2. ***Generate Signed APK...***
3. Fill in the keystore information *(you only need to do this once manually and then let Android Studio remember it)*

----

## Contributing

1. Fork it
2. Create your feature branch `git checkout -b my-new-feature`
3. Commit your changes `git commit -m 'Add some feature`
4. Push your branch `git push origin my-new-feature`
5. Create a new Pull Request

[//]: # (These are reference links used in the body of this note.)
[license-image]: https://img.shields.io/badge/license-Apache%202-blue.svg
[license-url]: https://github.com/ocariot/da-app/blob/master/LICENSE
[known-vulnerabilities-image]: https://snyk.io/test/github/ocariot/da-app/badge.svg
[known-vulnerabilities-url]: https://snyk.io/test/github/ocariot/da-app
[last-commit-image]: https://img.shields.io/github/last-commit/ocariot/da-app.svg
[last-commit-url]: https://github.com/ocariot/da-app/commits
[releases-image]: https://img.shields.io/github/release-date/ocariot/da-app.svg
[releases-url]: https://github.com/ocariot/da-app/releases
[contributors-image]: https://img.shields.io/github/contributors/ocariot/da-app.svg
[contributors-url]: https://github.com/ocariot/da-app/graphs/contributors
[apk-brazil]: https://img.shields.io/badge/download%20apk-BR-green.svg?style=for-the-badge&logo=android
[apk-brazil-url]: https://github.com/ocariot/da-app/releases/download/1.7.0/da-BR_v1.7.0.apk
[apk-europe]: https://img.shields.io/badge/download%20apk-EU-blue.svg?style=for-the-badge&logo=android
[apk-europe-url]: https://github.com/ocariot/da-app/releases/download/1.7.0/da-EU_v1.7.0.apk

--- 

### Screenshots

<img align="left" src="https://i.imgur.com/zc7UN5k.png" width="200" />
<img align="left" src="https://i.imgur.com/5WLaJlq.png" width="200" />
<img align="left" src="https://i.imgur.com/c5WjiZn.png" width="200" />
<img src="https://i.imgur.com/gxOEdZq.png" width="200" />

<img align="left" src="https://i.imgur.com/4xOngef.png" width="200" />
<img align="left" src="https://i.imgur.com/wvlyHPs.png" width="200" />
<img align="left" src="https://i.imgur.com/aoqYNzg.png" width="200" />
<img src="https://i.imgur.com/MKTfM9e.png" width="200" />

#####

<img align="left" src="https://i.imgur.com/GITTjts.png" width="200" />
<img align="left" src="https://i.imgur.com/GugwblV.png" width="200" />
<img align="left" src="https://i.imgur.com/VSiuJNT.png" width="200" />
<img src="https://i.imgur.com/VzM9jQU.png" width="200" />