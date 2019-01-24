# cordova-plugin-firebase-authentication<br>[![NPM version][npm-version]][npm-url] [![NPM downloads][npm-downloads]][npm-url]
> Cordova plugin for [Firebase Authentication](https://firebase.google.com/docs/auth/)

## Installation

    cordova plugin add cordova-plugin-firebase-authentication --save

Use variable `FIREBASE_AUTH_VERSION` to override dependency version on Android.

To use phone number authentication on iOS, your app must be able to receive silent APNs notifications from Firebase. For iOS 8.0 and above silent notifications do not require explicit user consent and is therefore unaffected by a user declining to receive APNs notifications in the app. Thus, the app does not need to request user permission to receive push notifications when implementing Firebase phone number auth.

## Supported Platforms

- iOS
- Android

## Methods
Every method call returns a promise which is optionally fulfilled with an appropriate value.

### getIdToken(_forceRefresh_)
Returns a JWT token used to identify the user to a Firebase service.
```js
cordova.plugins.firebase.auth.getIdToken().then(function(idToken) {
    // send token to server
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
cordova.plugins.firebase.auth.signInWithEmailAndPassword("my@mail.com", "pa55w0rd").then(function(userInfo) {
    // user is signed in
});
```

### verifyPhoneNumber(_phoneNumber_, _timeout_, _success_, _failure_)

**NOTE: this function does not return a promise. This is because the _success_ callback needs to return multiple times, which is not possible with promises.**

Handles the phone number verification process for the given phone number.

NOTE: Android supports auto-verify and instant device verification. Therefore, the callbacks for Android and iOS differ significantly. Also, Android allows forcing the resending of the verification SMS by simply recalling this function.

_timeout_ [milliseconds] is the maximum amount of time you are willing to wait for SMS auto-retrieval to be completed by the library. Maximum allowed value is 2 minutes (120000 milliseconds). Use 0 to disable SMS-auto-retrieval. If you specify a positive value less than 30 seconds, library will default to 30 seconds.

_success_ is the success callback. 

- iOS: The verificationId is passed to the success callback.
- Android: Supports multiple types of success callbacks:
    1. `onCodeSent` The SMS verification code has been sent to the provided phone number, we now need to ask the user to enter the code and then construct a credential by combining the code with a verification ID. This callback will always be called, and will always come first regardless of whether auto-verify or instant device verification takes place.
    2. `onVerificationCompleted` This callback will be invoked in two situations. Namely, Instant verification (In some cases the phone number can be instantly verified without needing to send or enter a verification code) and Auto-retrieval (On some devices Google Play services can automatically detect the incoming verification SMS and perform verification without user action).

The format of the object passed to the success callback on Android is shown in the example.

_failure_ is the failure callback.

- iOS: The failure message is passed to the failure callback.
- Android: Supports multiple types of failure callbacks (formatting shown in example): `verifyPhoneNumberError`, `invalidCredential`, `firebaseAuth`, `quotaExceeded`, `apiNotAvailable`.

```js
cordova.plugins.firebase.auth.verifyPhoneNumber("+123456789", 30000, (success) {
    if (platform === 'iOS') {
        // succeess = verificationId
    } else {
        if (success.type === 'onCodeSent') {
            // succeess.verificationId = verificationId
            // succeess.SMS = 'undefined'
        } else if (success.type === 'onVerificationCompleted') {
            // succeess.verificationId = undefined
            if (succeess.SMS !== null) {
                // succeess.SMS = '732748' Login using SMS code.
            } else {
                // Instant verification took place. To login please call signInWithPhoneAutoVerification
                cordova.plugins.firebase.auth.signInWithPhoneAutoVerification() // This is the only acceptable place to call this function.
            }
        }
    }
}, (failure) {
   if (platform === 'iOS') {
        // failure = error message
    } else {
        // failure.code = error code
        // failure.message = error message
    }
});
```

### signInWithPhoneAutoVerification()
Signs user in after instant verification. Refer to the example in `verifyPhoneNumber` for usage instructions. 

### signInWithVerificationId(_verificationId_, _smsCode_)
Asynchronously signs in using verificationId and 6-digit SMS code.
```js
cordova.plugins.firebase.auth.signInWithVerificationId("djgfioerjg34", "123456").then(function(userInfo) {
    // user is signed in
});
```

### signInAnonymously()
Create and use temporary anonymous account to authenticate with Firebase. 
```js
cordova.plugins.firebase.auth.signInAnonymously().then(function(userInfo) {
    // user is signed in
});
```

### signInWithGoogle(_idToken_, _accessToken_)
Uses Google's _idToken_ and _accessToken_ to sign-in into firebase account. In order to retriave those tokens follow instructions for [Android](https://firebase.google.com/docs/auth/android/google-signin) and [iOS](https://firebase.google.com/docs/auth/ios/google-signin).

### signInWithFacebook(_accessToken_)
Uses Facebook's _accessToken_ to sign-in into firebase account. In order to retriave those tokens follow instructions for [Android](https://firebase.google.com/docs/auth/android/facebook-login) and [iOS](https://firebase.google.com/docs/auth/ios/facebook-login).

### signInWithTwitter(_token_, _secret_)
Uses Twitter's _token_ and _secret_ to sign-in into firebase account. In order to retriave those tokens follow instructions for [Android](https://firebase.google.com/docs/auth/android/twitter-login) and [iOS](https://firebase.google.com/docs/auth/ios/twitter-login).  

### onAuthStateChanged(_callback_)
Registers a block as an auth state did change listener. To be invoked when:
* The block is registered as a listener,
* A user with a different UID from the current user has signed in, or
* The current user has signed out.

```js
cordova.plugins.firebase.auth.onAuthStateChanged(function(userInfo) {
    if (userInfo && userInfo.uid) {
        // user was signed in
    } else {
        // user was signed out
    }
});
```

### setLanguageCode(_languageCode_)
Set's the current user language code. The string used to set this property must be a language code that follows BCP 47.

### useAppLanguage()
Sets languageCode to the app’s current language.

### signOut()
Signs out the current user and clears it from the disk cache.
```js
cordova.plugins.firebase.auth.signOut().then(function() {
    // user was signed out
});
```

[npm-url]: https://www.npmjs.com/package/cordova-plugin-firebase-authentication
[npm-version]: https://img.shields.io/npm/v/cordova-plugin-firebase-authentication.svg
[npm-downloads]: https://img.shields.io/npm/dm/cordova-plugin-firebase-authentication.svg

