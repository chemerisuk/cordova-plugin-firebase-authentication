# cordova-plugin-firebase-authentication<br>[![NPM version][npm-version]][npm-url] [![NPM downloads][npm-downloads]][npm-url]
> Cordova plugin for [Firebase Authentication](https://firebase.google.com/docs/auth/)

## Installation

    cordova plugin add cordova-plugin-firebase-authentication --save

## Supported Platforms

- iOS
- Android

## Methods

### getIdToken(_forceRefresh_, _callback_, _errorCallback_)
Returns a JWT token used to identify the user to a Firebase service.
```js
window.cordova.plugins.firebase.auth.getIdToken(function(idToken) {
    // send token to server
});
```

### signInWithEmailAndPassword(_email_, _password_, _callback_, _errorCallback_)
Asynchronously signs in using an email and password.
```js
window.cordova.plugins.firebase.auth.signInWithEmailAndPassword("my@mail.com", "pa55w0rd", function(userInfo) {
    // user is signed in
});
```

[npm-url]: https://www.npmjs.com/package/cordova-plugin-firebase-authentication
[npm-version]: https://img.shields.io/npm/v/cordova-plugin-firebase-authentication.svg
[npm-downloads]: https://img.shields.io/npm/dt/cordova-plugin-firebase-authentication.svg

