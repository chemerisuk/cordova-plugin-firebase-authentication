/**
 * Registers a block as an auth state did change listener. To be invoked when:
 * - The block is registered as a listener,
 * - A user with a different UID from the current user has signed in, or
 * - The current user has signed out.
 * @param {(userDetails: UserDetails | null) => void} callback Callback function
 * @param {(error: string) => void} [errorCallback] Error callback function
 */
export function onAuthStateChanged(callback: (userDetails: UserDetails | null) => void, errorCallback?: (error: string) => void): () => void;
/**
 * Returns the current user in the Firebase instance.
 * @returns {Promise<UserDetails>} Fulfills promise with user details
 */
export function getCurrentUser(): Promise<UserDetails>;
/**
 * Returns a JWT token used to identify the user to a Firebase service.
 * @param {boolean} forceRefresh When <code>true</code> cached value is ignored
 * @returns {Promise<string>} Fulfills promis with id token string value
 *
 * @example
 * cordova.plugins.firebase.auth.getIdToken().then(function(idToken) {
 *     // send token to server
 * });
 */
export function getIdToken(forceRefresh: boolean): Promise<string>;
/**
 * Creates a new user account with the given email address and password.
 * @param {string} email User account email
 * @param {string} password User accound password
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.auth.createUserWithEmailAndPassword("my@mail.com", "pa55w0rd");
 */
export function createUserWithEmailAndPassword(email: string, password: string): Promise<void>;
/**
 * Initiates email verification for the current user.
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.auth.sendEmailVerification();
 */
export function sendEmailVerification(): Promise<void>;
/**
 * Triggers the Firebase Authentication backend to send a password-reset email
 * to the given email address, which must correspond to an existing user of your app.
 * @param {string} email User account email
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.auth.sendPasswordResetEmail("my@mail.com");
 */
export function sendPasswordResetEmail(email: string): Promise<void>;
/**
 * Triggers the Firebase Authentication backend to send a password-reset email
 * to the given email address, which must correspond to an existing user of your app.
 * @param {string} email User account email
 * @param {string} password User accound password
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.auth.signInWithEmailAndPassword("my@mail.com", "pa55w0rd");
 */
export function signInWithEmailAndPassword(email: string, password: string): Promise<void>;
/**
 * Create and use temporary anonymous account to authenticate with Firebase.
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.auth.signInAnonymously();
 */
export function signInAnonymously(): Promise<void>;
/**
 * Uses Google's <code>idToken</code> and <code>accessToken</code> to sign-in into firebase account.
 * @param {string} idToken Google ID token
 * @param {string} accessToken Google Access token
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @see https://firebase.google.com/docs/auth/android/google-signin
 * @see https://firebase.google.com/docs/auth/ios/google-signin
 *
 * @example
 * // Below we use cordova-plugin-googleplus to trigger Google Login UI
 * window.plugins.googleplus.login({
 *     scopes: '... ',
 *     webClientId: '1234...',
 *     offline: true
 * }, function(res) {
 *     cordova.plugins.firebase.auth.signInWithGoogle(res.idToken, res.accessToken).then(function() {
 *         console.log("Firebase logged in with Google");
 *     }, function(err) {
 *         console.error("Firebase login failed", err);
 *     });
 * }, function(err) {
 *     console.error("Google login failed", err);
 * });
 */
export function signInWithGoogle(idToken: string, accessToken: string): Promise<void>;
/**
 * Uses Facebook's <code>accessToken</code> to sign-in into firebase account. In order to
 * retrieve those tokens follow instructions for iOS and Android from Firebase docs.
 * @param {string} accessToken Facebook's access token string
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @see https://firebase.google.com/docs/auth/android/facebook-login
 * @see https://firebase.google.com/docs/auth/ios/facebook-login
 */
export function signInWithFacebook(accessToken: string): Promise<void>;
/**
 * Uses Twitter's <code>token</code> and <code>secret</code> to sign-in into firebase account.
 * In order to retrieve those tokens follow instructions for iOS and Android from Firebase docs.
 * @param {string} token Twitter's token string
 * @param {string} secret Twitter's secret string
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @see https://firebase.google.com/docs/auth/android/twitter-login
 * @see https://firebase.google.com/docs/auth/ios/twitter-login
 */
export function signInWithTwitter(token: string, secret: string): Promise<void>;
/**
 * Uses Apples's <code>idToken</code> and <code>rawNonce</code> to sign-in into firebase account. For getting _idToken_ (_rawNonce_ is optional) you can use `cordova-plugin-sign-in-with-apple` (or any other cordova plugin for Apple Sign-In).
 * @param {string} idToken Apple's ID token string
 * @param {string} rawNonce Apple's raw token string
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @see https://firebase.google.com/docs/auth/android/apple
 * @see https://firebase.google.com/docs/auth/ios/apple
 *
 * @example
 * // below we use cordova-plugin-sign-in-with-apple to trigger Apple Login UI
 * cordova.plugins.SignInWithApple.signin({
 *     requestedScopes: [0, 1]
 * }, function(res) {
 *     cordova.plugins.firebase.auth.signInWithApple(res.identityToken).then(function() {
 *         console.log("Firebase logged in with Apple");
 *     }, function(err) {
 *         console.error("Firebase login failed", err);
 *     });
 * }, function(err) {
 *     console.error("Apple signin failed", err);
 * });
 */
export function signInWithApple(idToken: string, rawNonce: string): Promise<void>;
/**
 * You can integrate Firebase Authentication with a custom authentication system
 * by modifying your authentication server to produce custom signed tokens when
 * a user successfully signs in. Your app receives this token and uses it to
 * authenticate with Firebase.
 * @param {string} authToken Custom auth token
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @see https://firebase.google.com/docs/auth/android/custom-auth
 * @see https://firebase.google.com/docs/auth/ios/custom-auth
 */
export function signInWithCustomToken(authToken: string): Promise<void>;
/**
 * Signs out the current user and clears it from the disk cache.
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.auth.signOut();
 */
export function signOut(): Promise<void>;
/**
 * Starts the phone number verification process for the given phone number.
 *
 * Android supports auto-verify and instant device verification.
 * <b>You must register `onAuthStateChanged` to get callback on instant verification.</b>
 *
 * Maximum allowed value for timeout is 2 minutes. Use 0 to disable SMS-auto-retrieval.
 * If you specify a positive value less than 30 seconds, library will default to 30 seconds.
 * @param {string} phoneNumber Phone number in international format
 * @param {number} [timeoutMillis] Maximum amount of time you are willing to wait for SMS auto-retrieval to be completed by the library.
 * @returns {Promise<string>} Fulfills promise with <code>verificationId</code> to use later for signing in
 */
export function verifyPhoneNumber(phoneNumber: string, timeoutMillis?: number): Promise<string>;
/**
 * Completes phone number verification process and use it to sign in.
 * @param {string} verificationId [description]
 * @param {string} code 6-digit SMS code
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.auth.verifyPhoneNumber("+123456789").then(function(verificationId) {
 *     var code = prompt("Enter verification code");
 *     if (code) {
 *         return cordova.plugins.firebase.auth.signInWithVerificationId(verificationId, code);
 *     }
 * }).catch(function(err) {
 *     console.error("Phone number verification failed", err);
 * });
 */
export function signInWithVerificationId(verificationId: string, code: string): Promise<void>;
/**
 * Sets languageCode to the app’s current language.
 * @param {string} host Emulator host name
 * @param {number} port Emulator port
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.auth.useEmulator('localhost', 8000);
 */
export function useEmulator(host: string, port: number): Promise<void>;
/**
 * Updates the current user's profile data.
 * Passing a `null` value will delete the current attribute's value, but not
 * passing a property won't change the current attribute's value.
 * @param {{displayName: string; photoURL: string}} profileDetails User attributes.
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.auth.updateProfile({
 *     displayName: "Jane Q. User",
 *     photoURL: "https://example.com/jane-q-user/profile.jpg",
 * });
 */
export function updateProfile(profileDetails: {
    displayName: string;
    photoURL: string;
}): Promise<void>;
/**
 * Sets languageCode to the app’s current language.
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.auth.useAppLanguage();
 */
export function useAppLanguage(): Promise<void>;
/**
 * Represents a user's profile information in your Firebase project's user database.
 */
export type UserDetails = {
    /**
     * String used to uniquely identify your user in your Firebase project's user database
     */
    uid: string;
    /**
     * Main display name of this user from the Firebase project's user database
     */
    displayName: string;
    /**
     * Main email address of the user, as stored in the Firebase project's user database.
     */
    email: string;
    /**
     * <code>true</code> if the user's email is verified.
     */
    emailVerified: boolean;
    /**
     * Phone number of the user, as stored in the Firebase project's user database, or null if none exists.
     */
    phoneNumber: string | null;
    /**
     * URL of this user's main profile picture, as stored in the Firebase project's user database.
     */
    photoURL: string;
    providerId: string;
};
