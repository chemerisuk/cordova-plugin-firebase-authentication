package by.chemerisuk.cordova.firebase;

import android.util.Log;
import java.util.concurrent.TimeUnit;

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


public class FirebaseAuthenticationPlugin extends CordovaPlugin {
    private static final String TAG = "FirebaseAuthentication";

    private FirebaseAuth firebaseAuth;

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "Starting Firebase Authentication plugin");

        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getIdToken")) {
            getIdToken(args.getBoolean(0), callbackContext);

            return true;
        } else if (action.equals("signInWithEmailAndPassword")) {
            signInWithEmailAndPassword(args.getString(0), args.getString(1), callbackContext);

            return true;
        } else if (action.equals("verifyPhoneNumber")) {
            verifyPhoneNumber(args.getString(0), args.getLong(1), callbackContext);

            return true;
        } else if (action.equals("signInWithVerificationId")) {
            signInWithVerificationId(args.getString(0), args.getString(1), callbackContext);

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

    private void signInWithEmailAndPassword(final String email, final String password, final CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();

                                callbackContext.success(getProfileData(user));
                            } else {
                                callbackContext.error(task.getException().getMessage());
                            }
                        }
                    });
            }
        });
    }

    private void verifyPhoneNumber(String phoneNumber, long timeout, final CallbackContext callbackContext) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, timeout, TimeUnit.MILLISECONDS, cordova.getActivity(),
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        signInWithCredential(credential, callbackContext);
                    } else {
                        user.updatePhoneNumber(credential)
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

                @Override
                public void onCodeAutoRetrievalTimeOut(String verificationId) {
                    callbackContext.success(verificationId);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    callbackContext.error(e.getMessage());
                }
            }
        );
    }

    private void signInWithVerificationId(String verificationId, String code, final CallbackContext callbackContext) {
        final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                signInWithCredential(credential, callbackContext);
            }
        });
    }

    private void signInWithCredential(PhoneAuthCredential credential, final CallbackContext callbackContext) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        callbackContext.success(getProfileData(user));
                    } else {
                        callbackContext.error(task.getException().getMessage());
                    }
                }
            });
    }

    private JSONObject getProfileData(FirebaseUser user) {
        JSONObject result = new JSONObject();

        try {
            result.put("uid", user.getUid());
            result.put("displayName", user.getDisplayName());
            result.put("email", user.getEmail());
            result.put("phone", user.getPhoneNumber());
            result.put("photoURL", user.getPhotoUrl());
            result.put("providerId", user.getProviderId());
        } catch (JSONException e) {
            Log.e(TAG, "Fail to process getProfileData", e);
        }

        return result;
    }
}
