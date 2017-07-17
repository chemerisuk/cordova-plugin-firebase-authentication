var exec = require("cordova/exec");
var PLUGIN_NAME = "FirebaseAuthentication";

module.exports = {
    getIdToken: function(forceRefresh, success, error) {
        exec(success, error, PLUGIN_NAME, "getIdToken", [forceRefresh]);
    },
    signInWithEmailAndPassword: function(email, password, success, error) {
        exec(success, error, PLUGIN_NAME, "signInWithEmailAndPassword", [email, password]);
    }
};
