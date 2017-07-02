#import "FirebaseAuthenticationPlugin.h"

@import Firebase;


@implementation FirebaseAuthenticationPlugin

- (void)getIdToken:(CDVInvokedUrlCommand *)command {
    bool forceRefresh = [[command.arguments objectAtIndex:0] boolValue];

    [self.commandDelegate runInBackground: ^{
        FIRUser *user = [FIRAuth auth].currentUser;

        if (user) {
            [user getIDTokenWithCompletion:^(NSString *token, NSError *error) {
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
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{
                    @"uid": user.uid,
                    @"providerId": user.providerID,
                    @"displayName": user.displayName ? user.displayName : @"",
                    @"email": user.email ? user.email : @"",
                    @"phone": user.phoneNumber ? user.phoneNumber : @"",
                    @"photoURL": user.photoURL ? user.photoURL.absoluteString : @""
                }];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }];
}

@end
