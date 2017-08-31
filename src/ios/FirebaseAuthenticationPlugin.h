#import <Cordova/CDV.h>

@interface FirebaseAuthenticationPlugin : CDVPlugin

- (void)getIdToken:(CDVInvokedUrlCommand*)command;
- (void)signInWithEmailAndPassword:(CDVInvokedUrlCommand*)command;
- (void)signInWithVerificationId:(CDVInvokedUrlCommand*)command;
- (void)verifyPhoneNumber:(CDVInvokedUrlCommand*)command;
- (void)signOut:(CDVInvokedUrlCommand*)command;

@end
