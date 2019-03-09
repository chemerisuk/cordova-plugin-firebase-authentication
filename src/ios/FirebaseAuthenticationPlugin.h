#import <Cordova/CDV.h>

@interface FirebaseAuthenticationPlugin : CDVPlugin

- (void)getCurrentUser:(CDVInvokedUrlCommand*)command;
- (void)getIdToken:(CDVInvokedUrlCommand*)command;
- (void)createUserWithEmailAndPassword:(CDVInvokedUrlCommand*)command;
- (void)sendEmailVerification:(CDVInvokedUrlCommand*)command;
- (void)sendPasswordResetEmail:(CDVInvokedUrlCommand*)command;
- (void)signInWithEmailAndPassword:(CDVInvokedUrlCommand*)command;
- (void)signInAnonymously:(CDVInvokedUrlCommand*)command;
- (void)signInWithGoogle:(CDVInvokedUrlCommand*)command;
- (void)signInWithFacebook:(CDVInvokedUrlCommand*)command;
- (void)signInWithTwitter:(CDVInvokedUrlCommand*)command;
- (void)signInWithVerificationId:(CDVInvokedUrlCommand*)command;
- (void)verifyPhoneNumber:(CDVInvokedUrlCommand*)command;
- (void)signOut:(CDVInvokedUrlCommand*)command;
- (void)setLanguageCode:(CDVInvokedUrlCommand*)command;
- (void)setAuthStateChanged:(CDVInvokedUrlCommand*)command;

@end
