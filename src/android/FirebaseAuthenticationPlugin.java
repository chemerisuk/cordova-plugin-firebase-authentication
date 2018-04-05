package by.chemerisuk.cordova.firebase;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
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

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class FirebaseAuthenticationPlugin extends CordovaPlugin implements OnCompleteListener, AuthStateListener {
    private static final String TAG = "FirebaseAuthentication";

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider phoneAuthProvider;
    private CallbackContext signinCallback;
    private CallbackContext authStateCallback;

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "Starting Firebase Authentication plugin");

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.phoneAuthProvider = PhoneAuthProvider.getInstance();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getIdToken")) {
            getIdToken(args.getBoolean(0), callbackContext);
            return true;
        } else if (action.equals("createUserWithEmailAndPassword")) {
            createUserWithEmailAndPassword(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if (action.equals("sendEmailVerification")) {
            sendEmailVerification(callbackContext);
            return true;
        } else if (action.equals("sendPasswordResetEmail")) {
            sendPasswordResetEmail(args.getString(0), callbackContext);
            return true;
        } else if (action.equals("signInWithEmailAndPassword")) {
            signInWithEmailAndPassword(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if (action.equals("signInAnonymously")) {
            signInAnonymously(callbackContext);
            return true;
        } else if (action.equals("signInWithGoogle")) {
            signInWithGoogle(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if (action.equals("signInWithFacebook")) {
            signInWithFacebook(args.getString(0), callbackContext);
            return true;
        } else if (action.equals("signInWithTwitter")) {
            signInWithTwitter(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if (action.equals("signInWithVerificationId")) {
            signInWithVerificationId(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if (action.equals("verifyPhoneNumber")) {
            verifyPhoneNumber(args.getString(0), args.optLong(1), callbackContext);
            return true;
        } else if (action.equals("signOut")) {
            signOut(callbackContext);
            return true;
        } else if (action.equals("setLanguageCode")) {
            setLanguageCode(args.optString(0), callbackContext);
            return true;
        } else if (action.equals("onAuthStateChanged")) {
            setAuthStateChanged(args.getBoolean(0), callbackContext);
            return true;
        }

        return false;
    }

    private void getIdToken(final boolean forceRefresh, final CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    private void createUserWithEmailAndPassword(final String email, final String password, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(cordova.getActivity(), FirebaseAuthenticationPlugin.this);
            }
        });
    }

    private void sendEmailVerification(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    private void sendPasswordResetEmail(final String email, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    private void signInAnonymously(CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(cordova.getActivity(), FirebaseAuthenticationPlugin.this);
            }
        });
    }

    private void signInWithEmailAndPassword(final String email, final String password, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(cordova.getActivity(), FirebaseAuthenticationPlugin.this);
            }
        });
    }

    private void signInWithGoogle(String idToken, String accessToken, CallbackContext callbackContext) {
        signInWithCredential(GoogleAuthProvider.getCredential(idToken, accessToken), callbackContext);
    }

    private void signInWithFacebook(String accessToken, CallbackContext callbackContext) {
        signInWithCredential(FacebookAuthProvider.getCredential(accessToken), callbackContext);
    }

    private void signInWithTwitter(String token, String secret, CallbackContext callbackContext) {
        signInWithCredential(TwitterAuthProvider.getCredential(token, secret), callbackContext);
    }

    private void signInWithCredential(final AuthCredential credential, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(cordova.getActivity(), FirebaseAuthenticationPlugin.this);
            }
        });
    }

    private void signInWithVerificationId(String verificationId, String code, final CallbackContext callbackContext) {
        final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        this.signinCallback = callbackContext;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                signInWithPhoneCredential(credential);
            }
        });
    }

    private void verifyPhoneNumber(final String phoneNumber, final long timeoutMillis, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                phoneAuthProvider.verifyPhoneNumber(phoneNumber, timeoutMillis, MILLISECONDS, cordova.getActivity(),
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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
                    }
                );
            }
        });
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

    private void signOut(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.signOut();

                callbackContext.success();
            }
        });
    }

    private void setLanguageCode(final String languageCode, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (languageCode == null) {
                    firebaseAuth.useAppLanguage();
                } else {
                    firebaseAuth.setLanguageCode(languageCode);
                }

                callbackContext.success();
            }
        });
    }

    private void setAuthStateChanged(final boolean disable, CallbackContext callbackContext) {
        this.authStateCallback = disable ? null : callbackContext;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (disable) {
                    firebaseAuth.removeAuthStateListener(FirebaseAuthenticationPlugin.this);
                } else {
                    firebaseAuth.addAuthStateListener(FirebaseAuthenticationPlugin.this);
                }
            }
        });
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
