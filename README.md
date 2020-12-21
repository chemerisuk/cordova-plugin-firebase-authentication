# Cordova plugin for [Firebase Authentication](https://firebase.google.com/docs/auth/)

[![NPM version][npm-version]][npm-url] [![NPM downloads][npm-downloads]][npm-url] [![NPM total downloads][npm-total-downloads]][npm-url] [![Twitter][twitter-follow]][twitter-url]

| [![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)][donate-url] | Your help is appreciated. Create a PR, submit a bug or just grab me :beer: |
|-|-|

## Index

<!-- MarkdownTOC levels="2" autolink="true" -->

- [Installation](#installation)
- [Supported Platforms](#supported-platforms)
- [User authorization](#user-authorization)
- [Get/set user state](#getset-user-state)

<!-- /MarkdownTOC -->

## Installation

    cordova plugin add cordova-plugin-firebase-authentication

Use variables `ANDROID_FIREBASE_AUTH_VERSION` or `IOS_FIREBASE_AUTH_VERSION` to override dependency versions for Firebase SDKs.

To use phone number authentication on iOS, your app must be able to receive silent APNs notifications from Firebase. For iOS 8.0 and above silent notifications do not require explicit user consent and is therefore unaffected by a user declining to receive APNs notifications in the app. Thus, the app does not need to request user permission to receive push notifications when implementing Firebase phone number auth.

## Supported Platforms

- iOS
- Android

## User authorization
Unlike v1 of the plugin in v2 you must register `onAuthStateChanged` callback to be notified when  `signIn*` or `signOut` methods are completed. 

### onAuthStateChanged(_callback_)
Registers a block as an auth state did change listener. To be invoked when:
* The block is registered as a listener,
* A user with a different UID from the current user has signed in, or
* The current user has signed out.

```js
cordova.plugins.firebase.auth.onAuthStateChanged(function(userInfo) {
    if (userInfo) {
        // user was signed in
    } else {
        // user was signed out
    }
});
```

### createUserWithEmailAndPassword(_email_, _password_)
Tries to create a new user account with the given email address and password.
```js
cordova.plugins.firebase.auth.createUserWithEmailAndPassword("my@mail.com", "pa55w0rd");
```

### sendEmailVerification()
Initiates email verification for the current user.
```js
cordova.plugins.firebase.auth.sendEmailVerification();
```

### sendPasswordResetEmail(_email_)
Triggers the Firebase Authentication backend to send a password-reset email to the given email address, which must correspond to an existing user of your app.
```js
cordova.plugins.firebase.auth.sendPasswordResetEmail("my@mail.com");
```

### signInWithEmailAndPassword(_email_, _password_)
Asynchronously signs in using an email and password.
```js
cordova.plugins.firebase.auth.signInWithEmailAndPassword("my@mail.com", "pa55w0rd");
```

### verifyPhoneNumber(_phoneNumber_, _timeout_)
Starts the phone number verification process for the given phone number.

NOTE: Android supports auto-verify and instant device verification. Therefore in that cases it doesn't make sense to ask for sms code. It's recommended to register `onAuthStateChanged` callback to be notified on auto sign-in. 

_timeout_ [milliseconds] is the maximum amount of time you are willing to wait for SMS auto-retrieval to be completed by the library. Maximum allowed value is 2 minutes. Use 0 to disable SMS-auto-retrieval. If you specify a positive value less than 30 seconds, library will default to 30 seconds.

```js
cordova.plugins.firebase.auth.verifyPhoneNumber("+123456789").then(function(verificationId) {
    // pass verificationId to signInWithVerificationId
}).catch(function(err) {
    console.error("phoner number verification failed", err);
});
```
### signInWithVerificationId(_verificationId_, _smsCode_)
Asynchronously signs in using verificationId and 6-digit SMS code.

```js
cordova.plugins.firebase.auth.signInWithVerificationId("djgfioerjg34", "123456");
```


### signInAnonymously()
Create and use temporary anonymous account to authenticate with Firebase. 
```js
cordova.plugins.firebase.auth.signInAnonymously();
```

### signInWithGoogle(_idToken_, _accessToken_)
Uses Google's _idToken_ and _accessToken_ to sign-in into firebase account. In order to retriave those tokens you can use `cordova-plugin-googleplus` (or any other cordova plugin for Google Sign-In).

    cordova plugin add cordova-plugin-firebase-authentication
    cordova plugin add cordova-plugin-googleplus --variable REVERSED_CLIENT_ID=myreversedclientid --variable WEB_APPLICATION_CLIENT_ID=mywebapplicationclientid

Now trigger signin dialog UI to popup:

```js
window.plugins.googleplus.login({
    'scopes': '... ', // optional, space-separated list of scopes, If not included or empty, defaults to `profile` and `email`.
    'webClientId': 'client id of the web app/server side', // optional clientId of your Web application from Credentials settings of your project - On Android, this MUST be included to get an idToken. On iOS, it is not required.
    'offline': true // optional, but requires the webClientId - if set to true the plugin will also return a serverAuthCode, which can be used to grant offline access to a non-Google server
}, function(res) {
    // signin into Firebase
    cordova.plugins.firebase.auth.signInWithGoogle(res.idToken, res.accessToken).then(function() {
        console.log("Firebase logged in with Google");
    });
}, function(err) {
    console.error("login failed", err);
});
```

### signInWithApple(_idToken_, _rawNonce_)
Uses Apples's _idToken_ and _rawNonce_ to sign-in into firebase account. For getting _idToken_ (_rawNonce_ is optional) you can use `cordova-plugin-sign-in-with-apple` (or any other cordova plugin for Apple Sign-In).

    cordova plugin add cordova-plugin-firebase-authentication
    cordova plugin add cordova-plugin-sign-in-with-apple

Then use code snippet below to signin into Firebase:

```js
cordova.plugins.SignInWithApple.signin({
    requestedScopes: [0, 1]
}, function(res) {
    cordova.plugins.firebase.auth.signInWithApple(res.identityToken).then(function() {
        console.log("Firebase logged in with Apple");
    });
}, function(err) {
    console.error("signin failed", err);
});
```

### signInWithFacebook(_accessToken_)
Uses Facebook's _accessToken_ to sign-in into firebase account. In order to retriave those tokens follow instructions for [Android](https://firebase.google.com/docs/auth/android/facebook-login) and [iOS](https://firebase.google.com/docs/auth/ios/facebook-login).

### signInWithTwitter(_token_, _secret_)
Uses Twitter's _token_ and _secret_ to sign-in into firebase account. In order to retriave those tokens follow instructions for [Android](https://firebase.google.com/docs/auth/android/twitter-login) and [iOS](https://firebase.google.com/docs/auth/ios/twitter-login). 

### signInWithCustomToken(_idToken_)
You can integrate Firebase Authentication with a custom authentication system by modifying your authentication server to produce custom signed tokens when a user successfully signs in. Your app receives this token and uses it to authenticate with Firebase. See [Android](https://firebase.google.com/docs/auth/android/custom-auth) and [iOS](https://firebase.google.com/docs/auth/ios/custom-auth) for more info. 

### signOut()
Signs out the current user and clears it from the disk cache.
```js
cordova.plugins.firebase.auth.signOut();
```

## Get/set user state
Every method call returns a promise which is optionally fulfilled with an appropriate value.

### getCurrentUser()
Returns the current user in the Firebase instance.
```js
cordova.plugins.firebase.auth.getCurrentUser().then(function(userInfo) {
    // user information or null if not logged in
})
```

### updateProfile()
Updates the current user's profile data.

Passing a `null` value will delete the current attribute's value, but not
passing a property won't change the current attribute's value.

```js
cordova.plugins.firebase.auth.updateProfile({
   displayName: "Jane Q. User",
   photoURL: "https://example.com/jane-q-user/profile.jpg"
}).then(function() {
    console.log("user profile updated");
});

// Let's say we continue updating the profile of the same user as before.
cordova.plugins.firebase.auth.updateProfile({
    photoURL: null
}).then(function() {
    // displayName is unchanged - "Jane Q. User"
    // photoURL is changed - null
    console.log("only photoURL is changed");
});
```

### getIdToken(_forceRefresh_)
Returns a JWT token used to identify the user to a Firebase service.
```js
cordova.plugins.firebase.auth.getIdToken().then(function(idToken) {
    // send token to server
});
```

### setLanguageCode(_languageCode_)
Set's the current user language code. The string used to set this property must be a language code that follows BCP 47.

### useAppLanguage()
Sets languageCode to the app’s current language.

[npm-url]: https://www.npmjs.com/package/cordova-plugin-firebase-authentication
[npm-version]: https://img.shields.io/npm/v/cordova-plugin-firebase-authentication.svg
[npm-downloads]: https://img.shields.io/npm/dm/cordova-plugin-firebase-authentication.svg
[npm-total-downloads]: https://img.shields.io/npm/dt/cordova-plugin-firebase-authentication.svg?label=total+downloads
[twitter-url]: https://twitter.com/chemerisuk
[twitter-follow]: https://img.shields.io/twitter/follow/chemerisuk.svg?style=social&label=Follow%20me
[donate-url]: https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=FYAALBP25DP2G&source=url