package by.chemerisuk.cordova.firebase;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.FirebaseException;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class FirebaseAuthenticationPlugin extends CordovaPlugin implements OnCompleteListener<AuthResult> {
    private static final String TAG = "FirebaseAuthentication";

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider phoneAuthProvider;
    private CallbackContext signinCallback;

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
        } else if (action.equals("signInWithEmailAndPassword")) {
            signInWithEmailAndPassword(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if (action.equals("signInWithVerificationId")) {
            signInWithVerificationId(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if (action.equals("verifyPhoneNumber")) {
            verifyPhoneNumber(args.getString(0), callbackContext);
            return true;
        } else if (action.equals("signOut")) {
            signOut(callbackContext);
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

    @Override
    public void onComplete(Task<AuthResult> task) {
        if (this.signinCallback == null) return;

        if (task.isSuccessful()) {
            FirebaseUser user = task.getResult().getUser();
            this.signinCallback.success(getProfileData(user));
        } else {
            this.signinCallback.error(task.getException().getMessage());
        }

        this.signinCallback = null;
    }

    private void signInWithEmailAndPassword(final String email, final String password, CallbackContext callbackContext) throws JSONException {
        this.signinCallback = callbackContext;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(cordova.getActivity(), FirebaseAuthenticationPlugin.this);
            }
        });
    }

    private void signInWithVerificationId(final String verificationId, final String code, final CallbackContext callbackContext) {
        final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        this.signinCallback = callbackContext;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(cordova.getActivity(), FirebaseAuthenticationPlugin.this);
                } else {
                    user.updatePhoneNumber(credential)
                        .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    callbackContext.success(getProfileData(firebaseAuth.getCurrentUser()));
                                } else {
                                    callbackContext.error(task.getException().getMessage());
                                }
                            }
                        });
                }
            }
        });
    }

    private void verifyPhoneNumber(final String phoneNumber, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                phoneAuthProvider.verifyPhoneNumber(phoneNumber, 0, MILLISECONDS, cordova.getActivity(),
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential credential) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user == null) {
                                firebaseAuth.signInWithCredential(credential);
                            } else {
                                user.updatePhoneNumber(credential);
                            }

                            callbackContext.success("");
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

    private void signOut(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.signOut();

                callbackContext.success();
            }
        });
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
