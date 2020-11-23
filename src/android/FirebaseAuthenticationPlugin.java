package by.chemerisuk.cordova.firebase;

import android.net.Uri;
import android.util.Log;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONException;
import org.json.JSONObject;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


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
    private void setAuthStateChanged(boolean disable, CallbackContext callbackContext) {
        if (this.authStateCallback != null) {
            this.authStateCallback = null;
            firebaseAuth.removeAuthStateListener(this);
        }

        if (!disable) {
            this.authStateCallback = callbackContext;
            firebaseAuth.addAuthStateListener(this);
        }
    }

    @CordovaMethod
    private void getCurrentUser(CallbackContext callbackContext) {
        PluginResult pluginResult = getProfileResult(firebaseAuth.getCurrentUser());
        callbackContext.sendPluginResult(pluginResult);
    }

    @CordovaMethod
    private void getIdToken(boolean forceRefresh, final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            user.getIdToken(forceRefresh).addOnCompleteListener(cordova.getActivity(), task -> {
                if (task.isSuccessful()) {
                    callbackContext.success(task.getResult().getToken());
                } else {
                    callbackContext.error(task.getException().getMessage());
                }
            });
        }
    }

    @CordovaMethod
    private void createUserWithEmailAndPassword(String email, String password, CallbackContext callbackContext) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(cordova.getActivity(), createCompleteListener(callbackContext));
    }

    @CordovaMethod
    private void sendEmailVerification(CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            user.sendEmailVerification()
                    .addOnCompleteListener(cordova.getActivity(), createCompleteListener(callbackContext));
        }
    }

    @CordovaMethod
    private void sendPasswordResetEmail(String email, CallbackContext callbackContext) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(cordova.getActivity(), createCompleteListener(callbackContext));
    }

    @CordovaMethod
    private void signInAnonymously(final CallbackContext callbackContext) {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(cordova.getActivity(), createCompleteListener(callbackContext));
    }

    @CordovaMethod
    private void signInWithEmailAndPassword(String email, String password, CallbackContext callbackContext) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(cordova.getActivity(), createCompleteListener(callbackContext));
    }

    @CordovaMethod
    private void signInWithGoogle(String idToken, String accessToken, CallbackContext callbackContext) {
        signInWithCredential(GoogleAuthProvider.getCredential(idToken, accessToken), callbackContext);
    }

    @CordovaMethod
    private void signInWithFacebook(String accessToken, CallbackContext callbackContext) {
        signInWithCredential(FacebookAuthProvider.getCredential(accessToken), callbackContext);
    }

    @CordovaMethod
    private void signInWithTwitter(String token, String secret, CallbackContext callbackContext) {
        signInWithCredential(TwitterAuthProvider.getCredential(token, secret), callbackContext);
    }

    private void signInWithCredential(AuthCredential credential, CallbackContext callbackContext) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(cordova.getActivity(), createCompleteListener(callbackContext));
    }

    private Task<?> signInWithPhoneCredential(PhoneAuthCredential credential) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            return user.updatePhoneNumber(credential);
        } else {
            return firebaseAuth.signInWithCredential(credential);
        }
    }

    @CordovaMethod
    private void signInWithVerificationId(String verificationId, String code, CallbackContext callbackContext) {
        signInWithPhoneCredential(PhoneAuthProvider.getCredential(verificationId, code))
                .addOnCompleteListener(cordova.getActivity(), createCompleteListener(callbackContext));
    }

    @CordovaMethod
    private void verifyPhoneNumber(String phoneNumber, long timeoutMillis, final CallbackContext callbackContext) {
        PhoneAuthOptions.Builder options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(cordova.getActivity())
                .setPhoneNumber(phoneNumber)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithPhoneCredential(credential);
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        callbackContext.success(verificationId);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        callbackContext.error(e.getMessage());
                    }
                });

        if (timeoutMillis > 0) {
            options.setTimeout(timeoutMillis, MILLISECONDS);
        }

        PhoneAuthProvider.verifyPhoneNumber(options.build());
    }

    @CordovaMethod
    private void signInWithCustomToken(String idToken, CallbackContext callbackContext) {
        firebaseAuth.signInWithCustomToken(idToken)
                .addOnCompleteListener(cordova.getActivity(),createCompleteListener(callbackContext));
    }

    @CordovaMethod
    private void signOut(CallbackContext callbackContext) {
        firebaseAuth.signOut();

        callbackContext.success();
    }

    @CordovaMethod
    private void setLanguageCode(String languageCode, CallbackContext callbackContext) {
        if (languageCode == null) {
            firebaseAuth.useAppLanguage();
        } else {
            firebaseAuth.setLanguageCode(languageCode);
        }

        callbackContext.success();
    }

    @CordovaMethod
    private void updateProfile(JSONObject params, CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            callbackContext.error("User is not authorized");
        }
        else {
            UserProfileChangeRequest request = createProfileChangeRequest(params);
            user.updateProfile(request)
                    .addOnCompleteListener(cordova.getActivity(), createCompleteListener(callbackContext));
        }
    }

    @CordovaMethod
    private void useEmulator(String host, int port, CallbackContext callbackContext) {
        firebaseAuth.useEmulator(host, port);

        callbackContext.success();
    }

    @Override
    public void onAuthStateChanged(FirebaseAuth auth) {
        if (this.authStateCallback != null) {
            PluginResult pluginResult = getProfileResult(firebaseAuth.getCurrentUser());
            pluginResult.setKeepCallback(true);
            this.authStateCallback.sendPluginResult(pluginResult);
        }
    }

    private static <T> OnCompleteListener<T> createCompleteListener(final CallbackContext callbackContext) {
        return new OnCompleteListener<T>() {
            @Override
            public void onComplete(Task task) {
                if (task.isSuccessful()) {
                    callbackContext.success();
                } else {
                    callbackContext.error(task.getException().getMessage());
                }
            }
        };
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

    private static UserProfileChangeRequest createProfileChangeRequest(JSONObject jsonObject) {
        UserProfileChangeRequest.Builder requestBuilder = new UserProfileChangeRequest.Builder();

        if (jsonObject.has("displayName")) {
            String displayName = jsonObject.optString("displayName", null);
            requestBuilder = requestBuilder.setDisplayName(displayName);
        }

        if (jsonObject.has("photoURL")) {
            String photoURL = jsonObject.optString("photoURL", null);
            requestBuilder = requestBuilder.setPhotoUri(Uri.parse(photoURL));
        }

        return requestBuilder.build();
    }
}
