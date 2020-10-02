var exec = require("cordova/exec");
var PLUGIN_NAME = "FirebaseAuthentication";

module.exports = {
    onAuthStateChanged: function(callback) {
        exec(callback, null, PLUGIN_NAME, "setAuthStateChanged", [false]);

        return function() {
            exec(null, null, PLUGIN_NAME, "setAuthStateChanged", [true]);
        };
    },
    getCurrentUser: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "getCurrentUser", []);
        });
    },
    getIdToken: function(forceRefresh) {
        return new Promise(function(resolve, reject) {
            if (forceRefresh == null) forceRefresh = false;

            exec(resolve, reject, PLUGIN_NAME, "getIdToken", [forceRefresh]);
        });
    },
    createUserWithEmailAndPassword: function(email, password) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "createUserWithEmailAndPassword", [email, password]);
        });
    },
    sendEmailVerification: function() {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "sendEmailVerification", []);
        });
    },
    sendPasswordResetEmail: function(email) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "sendPasswordResetEmail", [email]);
        });
    },
    signInWithEmailAndPassword: function(email, password) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "signInWithEmailAndPassword", [email, password]);
        });
    },
    signInAnonymously: function(email, password) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "signInAnonymously", []);
        });
    },
    signInWithGoogle: function(idToken, accessToken) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "signInWithGoogle", [idToken, accessToken]);
        });
    },
    signInWithFacebook: function(accessToken) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "signInWithFacebook", [accessToken]);
        });
    },
    signInWithTwitter: function(token, secret) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "signInWithTwitter", [token, secret]);
        });
    },
    signInWithApple: function(idToken, rawNonce) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "signInWithApple", [idToken, rawNonce || null]);
        });
    },
    verifyPhoneNumber: function(phoneNumber, timeoutMillis) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "verifyPhoneNumber", [phoneNumber, timeoutMillis]);
        });
    },
    signInWithVerificationId: function(verificationId, code) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "signInWithVerificationId", [verificationId, code]);
        });
    },
    signInWithCustomToken: function (idToken) {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "signInWithCustomToken", [idToken]);
        })
    },
    signOut: function() {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "signOut", []);
        });
    },
    setLanguageCode: function(languageCode) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "setLanguageCode", [languageCode]);
        });
    },
    useAppLanguage: function() {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "setLanguageCode", [null]);
        });
    },
     /**
     * Updates the current user's profile data.
     * 
     * Passing a `null` value will delete the current attribute's value, but not
     * passing a property won't change the current attribute's value:
     *
     * @example
     * ```javascript
     * updateProfile({
     *   displayName: "Jane Q. User",
     *   photoURL: "https://example.com/jane-q-user/profile.jpg"
     * }).then(function() {
     *   // Profile updated successfully!
     *   // "Jane Q. User"
     *   var displayName = user.displayName;
     *   // "https://example.com/jane-q-user/profile.jpg"
     *   var photoURL = user.photoURL;
     * }, function(error) {
     *   // An error happened.
     * });
     *
     * // Let's say we're using the same user than before, after the update.
     * updateProfile({photoURL: null}).then(function() {
     *   // Profile updated successfully!
     *   // "Jane Q. User", hasn't changed.
     *   var displayName = user.displayName;
     *   // Now, this is null.
     *   var photoURL = user.photoURL;
     * }, function(error) {
     *   // An error happened.
     * });
     * ```
     *
     * @param profile The profile's
     *     displayName and photoURL to update.
     */
    updateProfile: function(profile) {
        const displayName = profile['displayName'];
        const photoUrl = profile['photoURL'];

        if (displayName === undefined && photoUrl === undefined) {
            // No change, directly return.
            return Promise.resolve();
        }

        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "updateProfile", [profile]);
        });
    }
};
