#import "FirebaseAuthenticationPlugin.h"

@import FirebaseAuth;


@implementation FirebaseAuthenticationPlugin

- (void)getIdToken:(CDVInvokedUrlCommand *)command {
    BOOL forceRefresh = [[command.arguments objectAtIndex:0] boolValue];

    [self.commandDelegate runInBackground: ^{
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
    }];
}

- (void)createUserWithEmailAndPassword:(CDVInvokedUrlCommand *)command {
    NSString* email = [command.arguments objectAtIndex:0];
    NSString* password = [command.arguments objectAtIndex:1];

    [self.commandDelegate runInBackground: ^{
        [[FIRAuth auth] createUserWithEmail:email
                                   password:password
                                 completion:^(FIRUser *user, NSError *error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self userToDictionary:user]];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }];
}

- (void)sendEmailVerification:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground: ^{
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
    }];
}

- (void)sendPasswordResetEmail:(CDVInvokedUrlCommand *)command {
    NSString* email = [command.arguments objectAtIndex:0];

    [self.commandDelegate runInBackground: ^{
        [[FIRAuth auth] sendPasswordResetWithEmail:email completion:^(NSError *_Nullable error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }];
}

- (void)signInWithEmailAndPassword:(CDVInvokedUrlCommand *)command {
    NSString* email = [command.arguments objectAtIndex:0];
    NSString* password = [command.arguments objectAtIndex:1];

    [self.commandDelegate runInBackground: ^{
        [[FIRAuth auth] signInWithEmail:email
                               password:password
                             completion:^(FIRUser *user, NSError *error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self userToDictionary:user]];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }];
}

- (void)signInAnonymously:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground: ^{
        [[FIRAuth auth] signInAnonymouslyWithCompletion:^(FIRUser *user, NSError *error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self userToDictionary:user]];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }];
}

- (void)signInWithGoogle:(CDVInvokedUrlCommand *)command {
    NSString* idToken = [command.arguments objectAtIndex:0];
    NSString* accessToken = [command.arguments objectAtIndex:1];

    [self.commandDelegate runInBackground: ^{
        FIRAuthCredential *credential =
            [FIRGoogleAuthProvider credentialWithIDToken:idToken
                                             accessToken:accessToken];

        [[FIRAuth auth] signInWithCredential:credential
                                  completion:^(FIRUser *user, NSError *error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self userToDictionary:user]];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }];
}

- (void)signInWithFacebook:(CDVInvokedUrlCommand *)command {
    NSString* accessToken = [command.arguments objectAtIndex:0];

    [self.commandDelegate runInBackground: ^{
        FIRAuthCredential *credential =
            [FIRFacebookAuthProvider credentialWithAccessToken:accessToken];

        [[FIRAuth auth] signInWithCredential:credential
                                  completion:^(FIRUser *user, NSError *error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self userToDictionary:user]];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }];
}

- (void)signInWithTwitter:(CDVInvokedUrlCommand *)command {
    NSString* token = [command.arguments objectAtIndex:0];
    NSString* secret = [command.arguments objectAtIndex:1];

    [self.commandDelegate runInBackground: ^{
        FIRAuthCredential *credential =
            [FIRTwitterAuthProvider credentialWithToken:token
                                                 secret:secret];

        [[FIRAuth auth] signInWithCredential:credential
                                  completion:^(FIRUser *user, NSError *error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self userToDictionary:user]];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }];
}

- (void)signInWithVerificationId:(CDVInvokedUrlCommand*)command {
    NSString* verificationId = [command.arguments objectAtIndex:0];
    NSString* smsCode = [command.arguments objectAtIndex:1];

    [self.commandDelegate runInBackground: ^{
        FIRAuthCredential *credential = [[FIRPhoneAuthProvider provider]
                credentialWithVerificationID:verificationId
                            verificationCode:smsCode];

        [[FIRAuth auth] signInWithCredential:credential
                                  completion:^(FIRUser *user, NSError *error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self userToDictionary:user]];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }];
}

- (void)verifyPhoneNumber:(CDVInvokedUrlCommand*)command {
    NSString* phoneNumber = [command.arguments objectAtIndex:0];

    [self.commandDelegate runInBackground: ^{
        [[FIRPhoneAuthProvider provider] verifyPhoneNumber:phoneNumber
                                                completion:^(NSString* verificationId, NSError* error) {
            CDVPluginResult *pluginResult;
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:verificationId];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }];
}

- (void)signOut:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate runInBackground: ^{
        NSError *signOutError;
        BOOL status = [[FIRAuth auth] signOut:&signOutError];
        CDVPluginResult *pluginResult;
        if (status) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:signOutError.localizedDescription];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)setLanguageCode:(CDVInvokedUrlCommand*)command {
    NSString* languageCode = [command.arguments objectAtIndex:0];

    [self.commandDelegate runInBackground: ^{
        [FIRAuth auth].languageCode = languageCode;

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)useAppLanguage:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate runInBackground: ^{
        [[FIRAuth auth] useAppLanguage];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
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
