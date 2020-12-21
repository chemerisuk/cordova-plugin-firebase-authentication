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
    updateProfile: function(profile) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "updateProfile", [profile || {}]);
        });
    },
    useEmulator: function(host, port) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "useEmulator", [host, port]);
        });
    }
};
