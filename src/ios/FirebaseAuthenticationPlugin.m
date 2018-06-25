#import "FirebaseAuthenticationPlugin.h"
@import Firebase;

@implementation FirebaseAuthenticationPlugin

- (void)pluginInitialize {
    NSLog(@"Starting Firebase Authentication plugin");

    if(![FIRApp defaultApp]) {
        [FIRApp configure];
    }
}

- (void)getIdToken:(CDVInvokedUrlCommand *)command {
    BOOL forceRefresh = [[command.arguments objectAtIndex:0] boolValue];
    FIRUser *user = [FIRAuth auth].currentUser;

    if (user) {
        [user getIDTokenForcingRefresh:forceRefresh completion:^(NSString *token, NSError *error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:token];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"User must be signed in"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void)createUserWithEmailAndPassword:(CDVInvokedUrlCommand *)command {
    NSString* email = [command.arguments objectAtIndex:0];
    NSString* password = [command.arguments objectAtIndex:1];

    [[FIRAuth auth] createUserWithEmail:email
                               password:password
                             completion:^(FIRAuthDataResult *result, NSError *error) {
        [self.commandDelegate sendPluginResult:[self createAuthResult:result
                                                            withError:error] callbackId:command.callbackId];
    }];
}

- (void)sendEmailVerification:(CDVInvokedUrlCommand *)command {
    FIRUser *currentUser = [FIRAuth auth].currentUser;

    if (currentUser) {
        [currentUser sendEmailVerificationWithCompletion:^(NSError *_Nullable error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"User must be signed in"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void)sendPasswordResetEmail:(CDVInvokedUrlCommand *)command {
    NSString* email = [command.arguments objectAtIndex:0];

    [[FIRAuth auth] sendPasswordResetWithEmail:email completion:^(NSError *_Nullable error) {
        CDVPluginResult *pluginResult;
        if (error) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)signInWithEmailAndPassword:(CDVInvokedUrlCommand *)command {
    NSString* email = [command.arguments objectAtIndex:0];
    NSString* password = [command.arguments objectAtIndex:1];

    [[FIRAuth auth] signInWithEmail:email
                           password:password
                         completion:^(FIRAuthDataResult *result, NSError *error) {
        [self.commandDelegate sendPluginResult:[self createAuthResult:result
                                                            withError:error] callbackId:command.callbackId];
    }];
}

- (void)signInAnonymously:(CDVInvokedUrlCommand *)command {
    [[FIRAuth auth] signInAnonymouslyWithCompletion:^(FIRAuthDataResult *result, NSError *error) {
        [self.commandDelegate sendPluginResult:[self createAuthResult:result
                                                            withError:error] callbackId:command.callbackId];
    }];
}

- (void)signInWithGoogle:(CDVInvokedUrlCommand *)command {
    NSString* idToken = [command.arguments objectAtIndex:0];
    NSString* accessToken = [command.arguments objectAtIndex:1];

    FIRAuthCredential *credential =
        [FIRGoogleAuthProvider credentialWithIDToken:idToken
                                         accessToken:accessToken];

    [[FIRAuth auth] signInAndRetrieveDataWithCredential:credential
                                             completion:^(FIRAuthDataResult *result, NSError *error) {
        [self.commandDelegate sendPluginResult:[self createAuthResult:result
                                                            withError:error] callbackId:command.callbackId];
    }];
}

- (void)signInWithFacebook:(CDVInvokedUrlCommand *)command {
    NSString* accessToken = [command.arguments objectAtIndex:0];

    FIRAuthCredential *credential =
        [FIRFacebookAuthProvider credentialWithAccessToken:accessToken];

    [[FIRAuth auth] signInAndRetrieveDataWithCredential:credential
                                             completion:^(FIRAuthDataResult *result, NSError *error) {
        [self.commandDelegate sendPluginResult:[self createAuthResult:result
                                                            withError:error] callbackId:command.callbackId];
    }];
}

- (void)signInWithTwitter:(CDVInvokedUrlCommand *)command {
    NSString* token = [command.arguments objectAtIndex:0];
    NSString* secret = [command.arguments objectAtIndex:1];

    FIRAuthCredential *credential =
        [FIRTwitterAuthProvider credentialWithToken:token
                                             secret:secret];

    [[FIRAuth auth] signInAndRetrieveDataWithCredential:credential
                                             completion:^(FIRAuthDataResult *result, NSError *error) {
        [self.commandDelegate sendPluginResult:[self createAuthResult:result
                                                            withError:error] callbackId:command.callbackId];
    }];
}

- (void)signInWithVerificationId:(CDVInvokedUrlCommand*)command {
    NSString* verificationId = [command.arguments objectAtIndex:0];
    NSString* smsCode = [command.arguments objectAtIndex:1];

    FIRAuthCredential *credential = [[FIRPhoneAuthProvider provider]
            credentialWithVerificationID:verificationId
                        verificationCode:smsCode];

    [[FIRAuth auth] signInAndRetrieveDataWithCredential:credential
                                             completion:^(FIRAuthDataResult *result, NSError *error) {
        [self.commandDelegate sendPluginResult:[self createAuthResult:result
                                                            withError:error] callbackId:command.callbackId];
    }];
}

- (void)verifyPhoneNumber:(CDVInvokedUrlCommand*)command {
    NSString* phoneNumber = [command.arguments objectAtIndex:0];

    [[FIRPhoneAuthProvider provider] verifyPhoneNumber:phoneNumber
                                            UIDelegate:nil
                                            completion:^(NSString* verificationId, NSError* error) {
        CDVPluginResult *pluginResult;
        if (error) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:verificationId];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)signOut:(CDVInvokedUrlCommand*)command {
    NSError *signOutError;
    CDVPluginResult *pluginResult;

    if ([[FIRAuth auth] signOut:&signOutError]) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:signOutError.localizedDescription];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setLanguageCode:(CDVInvokedUrlCommand*)command {
    NSString* languageCode = [command.arguments objectAtIndex:0];
    if (languageCode) {
        [FIRAuth auth].languageCode = languageCode;
    } else {
        [[FIRAuth auth] useAppLanguage];
    }

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (CDVPluginResult*) createAuthResult:(FIRAuthDataResult*)result withError:(NSError*)error {
    CDVPluginResult *pluginResult;
    if (error) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self userToDictionary:result.user]];
    }
    return pluginResult;
}

- (NSDictionary*)userToDictionary:(FIRUser *)user {
    return @{
        @"uid": user.uid,
        @"providerId": user.providerID,
        @"displayName": user.displayName ? user.displayName : @"",
        @"email": user.email ? user.email : @"",
        @"phoneNumber": user.phoneNumber ? user.phoneNumber : @"",
        @"photoURL": user.photoURL ? user.photoURL.absoluteString : @""
    };
}

@end
