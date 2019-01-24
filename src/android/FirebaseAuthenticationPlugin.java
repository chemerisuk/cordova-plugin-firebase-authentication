package by.chemerisuk.cordova.firebase;

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
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONException;
import org.json.JSONObject;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.HashMap;
import java.util.Map;


public class FirebaseAuthenticationPlugin extends ReflectiveCordovaPlugin implements OnCompleteListener, AuthStateListener {
    private static final String TAG = "FirebaseAuthentication";

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider phoneAuthProvider;
    private PhoneAuthProvider.ForceResendingToken mForceResendingTokenStore;
    private PhoneAuthCredential mCredential;
    private CallbackContext signinCallback;
    private CallbackContext authStateCallback;

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "Starting Firebase Authentication plugin");

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.phoneAuthProvider = PhoneAuthProvider.getInstance();
        this.mForceResendingTokenStore = null;
        this.mCredential = null;
    }

    @CordovaMethod
    private void getIdToken(boolean forceRefresh, final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            user.getIdToken(forceRefresh)
                .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            callbackContext.success(task.getResult().getToken());
                        } else {
                            callbackContext.error(task.getException().getMessage());
                        }
                    }
                });
        }
    }

    @CordovaMethod
    private void createUserWithEmailAndPassword(String email, String password, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(cordova.getActivity(), this);
    }

    @CordovaMethod
    private void sendEmailVerification(final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            user.sendEmailVerification()
                .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            callbackContext.success();
                        } else {
                            callbackContext.error(task.getException().getMessage());
                        }
                    }
                });
        }
    }

    @CordovaMethod
    private void sendPasswordResetEmail(String email, final CallbackContext callbackContext) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        callbackContext.success();
                    } else {
                        callbackContext.error(task.getException().getMessage());
                    }
                }
            });
    }

    @CordovaMethod
    private void signInAnonymously(CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.signInAnonymously()
            .addOnCompleteListener(cordova.getActivity(), this);
    }

    @CordovaMethod
    private void signInWithEmailAndPassword(String email, String password, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(cordova.getActivity(), this);
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

    private void signInWithCredential(final AuthCredential credential, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(cordova.getActivity(), this);
    }

    @CordovaMethod
    private void signInWithVerificationId(String verificationId, String code, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        signInWithPhoneCredential(PhoneAuthProvider.getCredential(verificationId, code));
    }

    @CordovaMethod
    private void verifyPhoneNumber(String phoneNumber, long timeoutMillis, final CallbackContext callbackContext) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                if (credential.getSmsCode() == null) {
                    mCredential = credential;
                }
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, getVerifyPhoneNumberCallbackMap("onVerificationCompleted", null, credential));
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mForceResendingTokenStore = forceResendingToken;
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, getVerifyPhoneNumberCallbackMap("onCodeSent", verificationId, null));
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                callbackContext.error(getVerifyPhoneNumberExceptionMap(e));
            }
        };
        if (mForceResendingTokenStore != null) {
            phoneAuthProvider.verifyPhoneNumber(phoneNumber, timeoutMillis, MILLISECONDS, cordova.getActivity(), verificationCallbacks, mForceResendingTokenStore);
        } else {
            phoneAuthProvider.verifyPhoneNumber(phoneNumber, timeoutMillis, MILLISECONDS, cordova.getActivity(), verificationCallbacks);
        }
    }

    private static JSONObject getVerifyPhoneNumberCallbackMap(String type, String verificationId, PhoneAuthCredential credential) {
        JSONObject callbackMap = new JSONObject();

        try {
            callbackMap.put("type", type);
            if (credential != null) {
                callbackMap.put("SMS", credential.getSmsCode());
            } else {
                callbackMap.put("verificationId", verificationId);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Fail to process getVerifyPhoneNumberCallbackMap", e);
        }
        
        return callbackMap;
    }

    private static JSONObject getVerifyPhoneNumberExceptionMap(FirebaseException e) {
        JSONObject exceptionMap = new JSONObject();

        String errorCode = "verifyPhoneNumberError";
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            errorCode = "invalidCredential";
        } else if (e instanceof FirebaseAuthException) {
            errorCode = "firebaseAuth";
        } else if (e instanceof FirebaseTooManyRequestsException) {
            errorCode = "quotaExceeded";
        } else if (e instanceof FirebaseApiNotAvailableException) {
            errorCode = "apiNotAvailable";
        }

        try {
            exceptionMap.put("code", errorCode);
            exceptionMap.put("message", e.getMessage());
        } catch (JSONException e1) {
            Log.e(TAG, "Fail to process getVerifyPhoneNumberExceptionMap", e);
        }

        return exceptionMap;
    }

    private void signInWithPhoneCredential(PhoneAuthCredential credential) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(cordova.getActivity(), FirebaseAuthenticationPlugin.this);
        } else {
            user.updatePhoneNumber(credential)
                .addOnCompleteListener(cordova.getActivity(), FirebaseAuthenticationPlugin.this);
        }
    }

    @CordovaMethod
    private void signInWithPhoneAutoVerification(final CallbackContext callbackContext) {
        if (mCredential != null) {
            signInWithPhoneCredential(mCredential);
            callbackContext.success();
        } else {
            callbackContext.error("No credential present.");
        }
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
    private void setAuthStateChanged(boolean disable, CallbackContext callbackContext) {
        this.authStateCallback = disable ? null : callbackContext;

        if (disable) {
            firebaseAuth.removeAuthStateListener(this);
        } else {
            firebaseAuth.addAuthStateListener(this);
        }
    }

    @Override
    public void onComplete(Task task) {
        if (this.signinCallback != null) {
            if (task.isSuccessful()) {
                this.signinCallback.success(getProfileData(firebaseAuth.getCurrentUser()));
            } else {
                this.signinCallback.error(task.getException().getMessage());
            }

            this.signinCallback = null;
        }
    }

    @Override
    public void onAuthStateChanged(FirebaseAuth auth) {
        if (this.authStateCallback != null) {
            PluginResult pluginResult;
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                pluginResult = new PluginResult(PluginResult.Status.OK, getProfileData(user));
            } else {
                pluginResult = new PluginResult(PluginResult.Status.OK, "");
            }

            pluginResult.setKeepCallback(true);
            this.authStateCallback.sendPluginResult(pluginResult);
        }
    }

    private static JSONObject getProfileData(FirebaseUser user) {
        JSONObject result = new JSONObject();

        try {
            result.put("uid", user.getUid());
            result.put("displayName", user.getDisplayName());
            result.put("email", user.getEmail());
            result.put("phoneNumber", user.getPhoneNumber());
            result.put("photoURL", user.getPhotoUrl());
            result.put("providerId", user.getProviderId());
        } catch (JSONException e) {
            Log.e(TAG, "Fail to process getProfileData", e);
        }

        return result;
    }
}
