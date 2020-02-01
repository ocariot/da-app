# OCARIoT Data Acquisition
[![License][license-image]][license-url] [![Vulnerabilities][known-vulnerabilities-image]][known-vulnerabilities-url] [![Commit][last-commit-image]][last-commit-url] [![Releases][releases-image]][releases-url] [![Contributors][contributors-image]][contributors-url] 

[![Download][apk-download]][apk-download-url]

<p align="center"><img width="120" src="https://i.imgur.com/Z31yxK2.png"/></p>

Native Android application responsible for OCARIoT platform data acquisition. The application acts as an external services access token manager. Currently, only Fitbit service is available.

**MAIN FEATURES:**
  - **Fitbit access token management:**
    - The Educator and the Health Professional can grant the OCARIoT platform permission to collect Fitbit data from children who have privileges to manage their data according to the consent of those responsible;
    - Support to request collection of Fitbit data from the child at any time, automatically saving to the OCARIoT platform;
    - Revocation of permission to collect Fitbit data.
  - **Display of data saved on the OCARIoT platform:**
    - Physical activity;
    - Sleep;
    - Weight.
  - **Display of heart rate data collected in real time. Supported devices:**
    - Polar OH1;
    - H10.

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
[apk-download]: https://img.shields.io/badge/download%20apk-BR/EU-blue.svg?style=for-the-badge&logo=android
[apk-download-url]: https://play.google.com/store/apps/details?id=br.edu.uepb.nutes.ocariot

--- 

### Screenshots

<img align="left" src="https://i.imgur.com/ttlvzao.png" width="200" />
<img align="left" src="https://i.imgur.com/5WLaJlq.png" width="200" />
<img align="left" src="https://i.imgur.com/c5WjiZn.png" width="200" />
<img src="https://i.imgur.com/gxOEdZq.png" width="200" />

<img align="left" src="https://i.imgur.com/4xOngef.png" width="200" />
<img align="left" src="https://i.imgur.com/wvlyHPs.png" width="200" />
<img align="left" src="https://i.imgur.com/aoqYNzg.png" width="200" />
<img src="https://i.imgur.com/MKTfM9e.png" width="200" />

#####

<img align="left" src="https://i.imgur.com/5j3YeNy.png" width="200" />
<img align="left" src="https://i.imgur.com/GugwblV.png" width="200" />
<img align="left" src="https://i.imgur.com/VSiuJNT.png" width="200" />
<img src="https://i.imgur.com/VzM9jQU.png" width="200" />
