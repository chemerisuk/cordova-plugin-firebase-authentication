#import <Cordova/CDV.h>

@import Firebase;

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
- (void)signInWithApple:(CDVInvokedUrlCommand*)command;
- (void)signInWithVerificationId:(CDVInvokedUrlCommand*)command;
- (void)verifyPhoneNumber:(CDVInvokedUrlCommand*)command;
- (void)signInWithCustomToken:(CDVInvokedUrlCommand*)command;
- (void)signOut:(CDVInvokedUrlCommand*)command;
- (void)setLanguageCode:(CDVInvokedUrlCommand*)command;
- (void)setAuthStateChanged:(CDVInvokedUrlCommand*)command;
- (void)updateProfile:(CDVInvokedUrlCommand*)command;
- (void)useEmulator:(CDVInvokedUrlCommand*)command;

@property (strong, nonatomic) FIRAuthStateDidChangeListenerHandle authChangedHandler;

@end
