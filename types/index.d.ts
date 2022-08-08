interface CordovaPlugins {
    firebase: FirebasePlugins;
}

interface FirebasePlugins {
    auth: typeof import("./FirebaseAuthentication");
}
