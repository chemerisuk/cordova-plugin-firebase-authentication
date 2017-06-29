#import <Cordova/CDV.h>

@interface FirebaseAuthenticationPlugin : CDVPlugin

- (void)getIdToken:(CDVInvokedUrlCommand*)command;
- (void)signInWithEmailAndPassword:(CDVInvokedUrlCommand*)command;

@end
