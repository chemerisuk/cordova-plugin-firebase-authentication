var exec = require("cordova/exec");
var PLUGIN_NAME = "FirebaseAuthentication";

module.exports = {
    getIdToken: function(forceRefresh) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "getIdToken", [forceRefresh]);
        });
    },
    signInWithEmailAndPassword: function(email, password) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "signInWithEmailAndPassword", [email, password]);
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
    onAuthStateChanged: function(callback) {
        exec(callback, null, PLUGIN_NAME, "onAuthStateChanged", [false]);

        return function() {
            exec(null, null, PLUGIN_NAME, "onAuthStateChanged", [true]);
        };
    }
};
