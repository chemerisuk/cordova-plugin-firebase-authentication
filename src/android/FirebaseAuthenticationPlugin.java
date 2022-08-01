package by.chemerisuk.cordova.firebase;

import static com.google.android.gms.tasks.Tasks.await;

import android.net.Uri;
import android.util.Log;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.PluginResult;

import org.json.JSONException;
import org.json.JSONObject;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import androidx.annotation.NonNull;


public class FirebaseAuthenticationPlugin extends ReflectiveCordovaPlugin implements AuthStateListener {
    private static final String TAG = "FirebaseAuthentication";

    private FirebaseAuth firebaseAuth;
    private CallbackContext authStateCallback;

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "Starting Firebase Authentication plugin");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @CordovaMethod
    private void setAuthStateChanged(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        boolean disable = args.getBoolean(0);
        if (authStateCallback != null) {
            authStateCallback = null;
            firebaseAuth.removeAuthStateListener(this);
        }
        if (!disable) {
            authStateCallback = callbackContext;
            firebaseAuth.addAuthStateListener(this);
        }
    }

    @CordovaMethod
    private void getCurrentUser(CallbackContext callbackContext) {
        PluginResult pluginResult = getProfileResult(firebaseAuth.getCurrentUser());
        callbackContext.sendPluginResult(pluginResult);
    }

    @CordovaMethod
    private void getIdToken(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        boolean forceRefresh = args.getBoolean(0);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            GetTokenResult result = await(user.getIdToken(forceRefresh));
            callbackContext.success(result.getToken());
        }
    }

    @CordovaMethod
    private void createUserWithEmailAndPassword(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        String email = args.getString(0);
        String password = args.getString(1);
        await(firebaseAuth.createUserWithEmailAndPassword(email, password));
        callbackContext.success();
    }

    @CordovaMethod
    private void sendEmailVerification(CallbackContext callbackContext) throws Exception {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            await(user.sendEmailVerification());
            callbackContext.success();
        }
    }

    @CordovaMethod
    private void sendPasswordResetEmail(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        String email = args.getString(0);
        await(firebaseAuth.sendPasswordResetEmail(email));
        callbackContext.success();
    }

    @CordovaMethod
    private void signInAnonymously(CallbackContext callbackContext) throws Exception {
        await(firebaseAuth.signInAnonymously());
        callbackContext.success();
    }

    @CordovaMethod
    private void signInWithEmailAndPassword(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        String email = args.getString(0);
        String password = args.getString(1);
        await(firebaseAuth.signInWithEmailAndPassword(email, password));
        callbackContext.success();
    }

    @CordovaMethod
    private void signInWithGoogle(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        String idToken = args.getString(0);
        String accessToken = args.getString(1);
        signInWithCredential(GoogleAuthProvider.getCredential(idToken, accessToken), callbackContext);
    }

    @CordovaMethod
    private void signInWithFacebook(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        String accessToken = args.getString(0);
        signInWithCredential(FacebookAuthProvider.getCredential(accessToken), callbackContext);
    }

    @CordovaMethod
    private void signInWithTwitter(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        String token = args.getString(0);
        String secret = args.getString(1);
        signInWithCredential(TwitterAuthProvider.getCredential(token, secret), callbackContext);
    }

    @CordovaMethod
    private void signInWithVerificationId(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        String verificationId = args.getString(0);
        String code = args.getString(1);
        await(firebaseAuth.signInWithCredential(PhoneAuthProvider.getCredential(verificationId, code)));
        callbackContext.success();
    }

    @CordovaMethod
    private void verifyPhoneNumber(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String phoneNumber = args.getString(0);
        long timeoutMillis = args.optLong(1);
        PhoneAuthOptions.Builder options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(cordova.getActivity())
                .setPhoneNumber(phoneNumber)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            user.updatePhoneNumber(credential);
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        callbackContext.success(verificationId);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        callbackContext.error(e.getMessage());
                    }
                });

        if (timeoutMillis > 0) {
            options.setTimeout(timeoutMillis, MILLISECONDS);
        }
        PhoneAuthProvider.verifyPhoneNumber(options.build());
    }

    @CordovaMethod
    private void signInWithCustomToken(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        String idToken = args.getString(0);
        await(firebaseAuth.signInWithCustomToken(idToken));
        callbackContext.success();
    }

    @CordovaMethod
    private void signOut(CallbackContext callbackContext) {
        firebaseAuth.signOut();
        callbackContext.success();
    }

    @CordovaMethod
    private void setLanguageCode(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String languageCode = args.getString(0);
        if (languageCode == null) {
            firebaseAuth.useAppLanguage();
        } else {
            firebaseAuth.setLanguageCode(languageCode);
        }
        callbackContext.success();
    }

    @CordovaMethod
    private void updateProfile(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            JSONObject params = args.getJSONObject(0);
            UserProfileChangeRequest request = createProfileChangeRequest(params);
            await(user.updateProfile(request));
            callbackContext.success();
        }
    }

    @CordovaMethod
    private void useEmulator(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String host = args.getString(0);
        int port = args.getInt(1);
        firebaseAuth.useEmulator(host, port);
        callbackContext.success();
    }

    private void signInWithCredential(AuthCredential credential, CallbackContext callbackContext) throws Exception {
        await(firebaseAuth.signInWithCredential(credential));
        callbackContext.success();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        if (authStateCallback != null) {
            PluginResult pluginResult = getProfileResult(firebaseAuth.getCurrentUser());
            pluginResult.setKeepCallback(true);
            authStateCallback.sendPluginResult(pluginResult);
        }
    }

    private static PluginResult getProfileResult(FirebaseUser user) {
        if (user == null) {
            return new PluginResult(PluginResult.Status.OK, (String)null);
        }

        JSONObject result = new JSONObject();

        try {
            result.put("uid", user.getUid());
            result.put("displayName", user.getDisplayName());
            result.put("email", user.getEmail());
            result.put("phoneNumber", user.getPhoneNumber());
            result.put("photoURL", user.getPhotoUrl());
            result.put("providerId", user.getProviderId());
            result.put("emailVerified", user.isEmailVerified());

            return new PluginResult(PluginResult.Status.OK, result);
        } catch (JSONException e) {
            Log.e(TAG, "Fail to process getProfileData", e);

            return new PluginResult(PluginResult.Status.ERROR, e.getMessage());
        }
    }

    private static UserProfileChangeRequest createProfileChangeRequest(JSONObject jsonObject) throws JSONException {
        UserProfileChangeRequest.Builder requestBuilder = new UserProfileChangeRequest.Builder();
        if (jsonObject.has("displayName")) {
            String displayName = jsonObject.getString("displayName");
            requestBuilder = requestBuilder.setDisplayName(displayName);
        }
        if (jsonObject.has("photoURL")) {
            String photoURL = jsonObject.getString("photoURL");
            requestBuilder = requestBuilder.setPhotoUri(Uri.parse(photoURL));
        }
        return requestBuilder.build();
    }
}
