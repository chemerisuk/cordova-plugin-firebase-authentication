# Cordova plugin for [Firebase Authentication](https://firebase.google.com/docs/auth/)

[![NPM version][npm-version]][npm-url] [![NPM downloads][npm-downloads]][npm-url] [![NPM total downloads][npm-total-downloads]][npm-url] [![Twitter][twitter-follow]][twitter-url]

| [![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)][donate-url] | Your help is appreciated. Create a PR, submit a bug or just grab me :beer: |
|-|-|

[npm-url]: https://www.npmjs.com/package/cordova-plugin-firebase-authentication
[npm-version]: https://img.shields.io/npm/v/cordova-plugin-firebase-authentication.svg
[npm-downloads]: https://img.shields.io/npm/dm/cordova-plugin-firebase-authentication.svg
[npm-total-downloads]: https://img.shields.io/npm/dt/cordova-plugin-firebase-authentication.svg?label=total+downloads
[twitter-url]: https://twitter.com/chemerisuk
[twitter-follow]: https://img.shields.io/twitter/follow/chemerisuk.svg?style=social&label=Follow%20me
[donate-url]: https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=FYAALBP25DP2G&source=url

## Index

<!-- MarkdownTOC levels="2,3" autolink="true" -->

- [Supported Platforms](#supported-platforms)
- [Installation](#installation)
    - [Adding required configuration files](#adding-required-configuration-files)
- [Type Aliases](#type-aliases)
    - [UserDetails](#userdetails)
- [Functions](#functions)
    - [createUserWithEmailAndPassword](#createuserwithemailandpassword)
    - [getCurrentUser](#getcurrentuser)
    - [getIdToken](#getidtoken)
    - [onAuthStateChanged](#onauthstatechanged)
    - [sendEmailVerification](#sendemailverification)
    - [sendPasswordResetEmail](#sendpasswordresetemail)
    - [signInAnonymously](#signinanonymously)
    - [signInWithApple](#signinwithapple)
    - [signInWithCustomToken](#signinwithcustomtoken)
    - [signInWithEmailAndPassword](#signinwithemailandpassword)
    - [signInWithFacebook](#signinwithfacebook)
    - [signInWithGoogle](#signinwithgoogle)
    - [signInWithTwitter](#signinwithtwitter)
    - [signInWithVerificationId](#signinwithverificationid)
    - [signOut](#signout)
    - [updateProfile](#updateprofile)
    - [useAppLanguage](#useapplanguage)
    - [useEmulator](#useemulator)
    - [verifyPhoneNumber](#verifyphonenumber)

<!-- /MarkdownTOC -->

## Supported Platforms

- iOS
- Android

## Installation

    cordova plugin add cordova-plugin-firebase-authentication

Use variables `IOS_FIREBASE_POD_VERSION` and `ANDROID_FIREBASE_BOM_VERSION` to override dependency versions for Firebase SDKs:

    $ cordova plugin add cordova-plugin-firebase-authentication \
    --variable IOS_FIREBASE_POD_VERSION="9.3.0" \
    --variable ANDROID_FIREBASE_BOM_VERSION="30.3.1"

To use phone number authentication on iOS, your app must be able to receive silent APNs notifications from Firebase. For iOS 8.0 and above silent notifications do not require explicit user consent and is therefore unaffected by a user declining to receive APNs notifications in the app. Thus, the app does not need to request user permission to receive push notifications when implementing Firebase phone number auth.

### Adding required configuration files

Cordova supports `resource-file` tag for easy copying resources files. Firebase SDK requires `google-services.json` on Android and `GoogleService-Info.plist` on iOS platforms.

1. Put `google-services.json` and/or `GoogleService-Info.plist` into the root directory of your Cordova project
2. Add new tag for Android platform

```xml
<platform name="android">
    ...
    <resource-file src="google-services.json" target="app/google-services.json" />
</platform>
...
<platform name="ios">
    ...
    <resource-file src="GoogleService-Info.plist" />
</platform>
```

<!-- TypedocGenerated -->

## Type Aliases

### UserDetails

 **UserDetails**: `Object`

Represents a user's profile information in your Firebase project's user database.

#### Type declaration

| Name | Type | Description |
| :------ | :------ | :------ |
| `displayName` | `string` | Main display name of this user from the Firebase project's user database |
| `email` | `string` | Main email address of the user, as stored in the Firebase project's user database. |
| `emailVerified` | `boolean` | <code>true</code> if the user's email is verified. |
| `phoneNumber` | `string` \| ``null`` | Phone number of the user, as stored in the Firebase project's user database, or null if none exists. |
| `photoURL` | `string` | URL of this user's main profile picture, as stored in the Firebase project's user database. |
| `providerId` | `string` | - |
| `uid` | `string` | String used to uniquely identify your user in your Firebase project's user database |

## Functions

### createUserWithEmailAndPassword

**createUserWithEmailAndPassword**(`email`, `password`): `Promise`<`void`\>

Creates a new user account with the given email address and password.

**`Example`**

```ts
cordova.plugins.firebase.auth.createUserWithEmailAndPassword("my@mail.com", "pa55w0rd");
```

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `email` | `string` | User account email |
| `password` | `string` | User accound password |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### getCurrentUser

**getCurrentUser**(): `Promise`<[`UserDetails`](#userdetails)\>

Returns the current user in the Firebase instance.

#### Returns

`Promise`<[`UserDetails`](#userdetails)\>

Fulfills promise with user details

___

### getIdToken

**getIdToken**(`forceRefresh`): `Promise`<`string`\>

Returns a JWT token used to identify the user to a Firebase service.

**`Example`**

```ts
cordova.plugins.firebase.auth.getIdToken().then(function(idToken) {
    // send token to server
});
```

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `forceRefresh` | `boolean` | When <code>true</code> cached value is ignored |

#### Returns

`Promise`<`string`\>

Fulfills promis with id token string value

___

### onAuthStateChanged

**onAuthStateChanged**(`callback`, `errorCallback?`): () => `void`

Registers a block as an auth state did change listener. To be invoked when:
- The block is registered as a listener,
- A user with a different UID from the current user has signed in, or
- The current user has signed out.

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `callback` | (`userDetails`: [`UserDetails`](#userdetails)) => `void` | Callback function |
| `errorCallback?` | (`error`: `string`) => `void` | Error callback function |

#### Returns

`fn`

(): `void`

##### Returns

`void`

___

### sendEmailVerification

**sendEmailVerification**(): `Promise`<`void`\>

Initiates email verification for the current user.

**`Example`**

```ts
cordova.plugins.firebase.auth.sendEmailVerification();
```

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### sendPasswordResetEmail

**sendPasswordResetEmail**(`email`): `Promise`<`void`\>

Triggers the Firebase Authentication backend to send a password-reset email
to the given email address, which must correspond to an existing user of your app.

**`Example`**

```ts
cordova.plugins.firebase.auth.sendPasswordResetEmail("my@mail.com");
```

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `email` | `string` | User account email |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### signInAnonymously

**signInAnonymously**(): `Promise`<`void`\>

Create and use temporary anonymous account to authenticate with Firebase.

**`Example`**

```ts
cordova.plugins.firebase.auth.signInAnonymously();
```

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### signInWithApple

**signInWithApple**(`idToken`, `rawNonce`): `Promise`<`void`\>

Uses Apples's <code>idToken</code> and <code>rawNonce</code> to sign-in into firebase account. For getting _idToken_ (_rawNonce_ is optional) you can use `cordova-plugin-sign-in-with-apple` (or any other cordova plugin for Apple Sign-In).

**`See`**

 - https://firebase.google.com/docs/auth/android/apple
 - https://firebase.google.com/docs/auth/ios/apple

**`Example`**

```ts
// below we use cordova-plugin-sign-in-with-apple to trigger Apple Login UI
cordova.plugins.SignInWithApple.signin({
    requestedScopes: [0, 1]
}, function(res) {
    cordova.plugins.firebase.auth.signInWithApple(res.identityToken).then(function() {
        console.log("Firebase logged in with Apple");
    }, function(err) {
        console.error("Firebase login failed", err);
    });
}, function(err) {
    console.error("Apple signin failed", err);
});
```

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `idToken` | `string` | Apple's ID token string |
| `rawNonce` | `string` | Apple's raw token string |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### signInWithCustomToken

**signInWithCustomToken**(`authToken`): `Promise`<`void`\>

You can integrate Firebase Authentication with a custom authentication system
by modifying your authentication server to produce custom signed tokens when
a user successfully signs in. Your app receives this token and uses it to
authenticate with Firebase.

**`See`**

 - https://firebase.google.com/docs/auth/android/custom-auth
 - https://firebase.google.com/docs/auth/ios/custom-auth

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `authToken` | `string` | Custom auth token |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### signInWithEmailAndPassword

**signInWithEmailAndPassword**(`email`, `password`): `Promise`<`void`\>

Triggers the Firebase Authentication backend to send a password-reset email
to the given email address, which must correspond to an existing user of your app.

**`Example`**

```ts
cordova.plugins.firebase.auth.signInWithEmailAndPassword("my@mail.com", "pa55w0rd");
```

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `email` | `string` | User account email |
| `password` | `string` | User accound password |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### signInWithFacebook

**signInWithFacebook**(`accessToken`): `Promise`<`void`\>

Uses Facebook's <code>accessToken</code> to sign-in into firebase account. In order to
retrieve those tokens follow instructions for iOS and Android from Firebase docs.

**`See`**

 - https://firebase.google.com/docs/auth/android/facebook-login
 - https://firebase.google.com/docs/auth/ios/facebook-login

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `accessToken` | `string` | Facebook's access token string |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### signInWithGoogle

**signInWithGoogle**(`idToken`, `accessToken`): `Promise`<`void`\>

Uses Google's <code>idToken</code> and <code>accessToken</code> to sign-in into firebase account.

**`See`**

 - https://firebase.google.com/docs/auth/android/google-signin
 - https://firebase.google.com/docs/auth/ios/google-signin

**`Example`**

```ts
// Below we use cordova-plugin-googleplus to trigger Google Login UI
window.plugins.googleplus.login({
    scopes: '... ',
    webClientId: '1234...',
    offline: true
}, function(res) {
    cordova.plugins.firebase.auth.signInWithGoogle(res.idToken, res.accessToken).then(function() {
        console.log("Firebase logged in with Google");
    }, function(err) {
        console.error("Firebase login failed", err);
    });
}, function(err) {
    console.error("Google login failed", err);
});
```

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `idToken` | `string` | Google ID token |
| `accessToken` | `string` | Google Access token |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### signInWithTwitter

**signInWithTwitter**(`token`, `secret`): `Promise`<`void`\>

Uses Twitter's <code>token</code> and <code>secret</code> to sign-in into firebase account.
In order to retrieve those tokens follow instructions for iOS and Android from Firebase docs.

**`See`**

 - https://firebase.google.com/docs/auth/android/twitter-login
 - https://firebase.google.com/docs/auth/ios/twitter-login

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `token` | `string` | Twitter's token string |
| `secret` | `string` | Twitter's secret string |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### signInWithVerificationId

**signInWithVerificationId**(`verificationId`, `code`): `Promise`<`void`\>

Completes phone number verification process and use it to sign in.

**`Example`**

```ts
cordova.plugins.firebase.auth.verifyPhoneNumber("+123456789").then(function(verificationId) {
    var code = prompt("Enter verification code");
    if (code) {
        return cordova.plugins.firebase.auth.signInWithVerificationId(verificationId, code);
    }
}).catch(function(err) {
    console.error("Phone number verification failed", err);
});
```

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `verificationId` | `string` | [description] |
| `code` | `string` | 6-digit SMS code |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### signOut

**signOut**(): `Promise`<`void`\>

Signs out the current user and clears it from the disk cache.

**`Example`**

```ts
cordova.plugins.firebase.auth.signOut();
```

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### updateProfile

**updateProfile**(`profileDetails`): `Promise`<`void`\>

Updates the current user's profile data.
Passing a `null` value will delete the current attribute's value, but not
passing a property won't change the current attribute's value.

**`Example`**

```ts
cordova.plugins.firebase.auth.updateProfile({
    displayName: "Jane Q. User",
    photoURL: "https://example.com/jane-q-user/profile.jpg",
});
```

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `profileDetails` | `Object` | User attributes. |
| `profileDetails.displayName` | `string` | - |
| `profileDetails.photoURL` | `string` | - |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### useAppLanguage

**useAppLanguage**(): `Promise`<`void`\>

Sets languageCode to the app’s current language.

**`Example`**

```ts
cordova.plugins.firebase.auth.useAppLanguage();
```

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### useEmulator

**useEmulator**(`host`, `port`): `Promise`<`void`\>

Sets languageCode to the app’s current language.

**`Example`**

```ts
cordova.plugins.firebase.auth.useEmulator('localhost', 8000);
```

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `host` | `string` | Emulator host name |
| `port` | `number` | Emulator port |

#### Returns

`Promise`<`void`\>

Callback when operation is completed

___

### verifyPhoneNumber

**verifyPhoneNumber**(`phoneNumber`, `timeoutMillis?`): `Promise`<`string`\>

Starts the phone number verification process for the given phone number.

Android supports auto-verify and instant device verification.
<b>You must register `onAuthStateChanged` to get callback on instant verification.</b>

Maximum allowed value for timeout is 2 minutes. Use 0 to disable SMS-auto-retrieval.
If you specify a positive value less than 30 seconds, library will default to 30 seconds.

#### Parameters

| Name | Type | Description |
| :------ | :------ | :------ |
| `phoneNumber` | `string` | Phone number in international format |
| `timeoutMillis?` | `number` | Maximum amount of time you are willing to wait for SMS auto-retrieval to be completed by the library. |

#### Returns

`Promise`<`string`\>

Fulfills promise with <code>verificationId</code> to use later for signing in
