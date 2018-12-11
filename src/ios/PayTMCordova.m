#import "PayTMCordova.h"
#import <Cordova/CDV.h>

#define NILABLE(obj) ((obj) != nil ? (NSObject *)(obj) : (NSObject *)[NSNull null])

@implementation PayTMCordova{
    NSString* callbackId;
    PGTransactionViewController* txnController;
}


- (void)startPayment:(CDVInvokedUrlCommand *)command {
    
    callbackId = command.callbackId;
    NSDictionary *options = [NSJSONSerialization JSONObjectWithData:[[[command arguments] objectAtIndex:0]
                             dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    NSString *environment = command.arguments[1];
    
    NSBundle* mainBundle;
    mainBundle = [NSBundle mainBundle];
    
    NSString* paytm_merchant_id = [mainBundle objectForInfoDictionaryKey:@"PayTMMerchantID"];
    NSString* paytm_ind_type_id = [mainBundle objectForInfoDictionaryKey:@"PayTMIndustryTypeID"];
    NSString* paytm_website = [mainBundle objectForInfoDictionaryKey:@"PayTMWebsite"];
    
    PGMerchantConfiguration* merchant = [PGMerchantConfiguration defaultConfiguration];
    
    //Step 2: Create the order with whatever params you want to add. But make sure that you include the merchant mandatory params
    NSMutableDictionary *orderDict = [options mutableCopy];
    //Merchant configuration in the order object
    orderDict[@"MID"] = paytm_merchant_id;
    orderDict[@"INDUSTRY_TYPE_ID"] = paytm_ind_type_id;
    orderDict[@"CHANNEL_ID"] = @"WAP";
    orderDict[@"WEBSITE"] = paytm_website;

    PGOrder *order = [PGOrder orderWithParams:orderDict];
    
    //Choose the PG server. In your production build dont call selectServerDialog. Just create a instance of the
    //PGTransactionViewController and set the serverType to eServerTypeProduction
        PGTransactionViewController *txnController = [[PGTransactionViewController alloc] initTransactionForOrder:order];
        if ([environment  caseInsensitiveCompare: @"production"] == NSOrderedSame) {
            txnController.serverType = eServerTypeProduction;
        } else {
            txnController.serverType = eServerTypeStaging;
            txnController.useStaging = true;
        }
        txnController.merchant = merchant;
        txnController.delegate = self;
        txnController.loggingEnabled = YES;
        UIViewController *rootVC = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
        [rootVC presentViewController:txnController animated:YES completion:nil];
}

//Called when a transaction has completed. response dictionary will be having details about Transaction.
- (void)didFinishedResponse:(PGTransactionViewController *)controller response:(NSString *)responseString {
    DEBUGLOG(@"ViewController::didFinishedResponse:response = %@", responseString);
    NSDictionary *response = [NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    if ([response[@"STATUS"]  isEqual: @"TXN_SUCCESS"]) {
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:response];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    } else {
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:response];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    }
    [controller dismissViewControllerAnimated:YES completion:nil];
}

//Called when a transaction is Canceled by User. response dictionary will be having details about Canceled Transaction.
- (void)didCancelTransaction:(PGTransactionViewController *)controller {
    DEBUGLOG(@"ViewController::didCancelTransaction ");
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"Cancelled Transaction"];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    [controller dismissViewControllerAnimated:YES completion:nil];
}

 - (void)errorMisssingParameter:(PGTransactionViewController *)controller  error:(NSError *) error {
      DEBUGLOG(@"ViewController::errorMisssingParameter:error = %@", error);
     CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:@{ @"RESPCODE": NILABLE([NSNumber numberWithInteger:error.code]),
                                                                                                                @"RESPMSG": NILABLE(error.localizedDescription)}];
     [self.commandDelegate sendPluginResult:result callbackId:callbackId];
     [controller dismissViewControllerAnimated:YES completion:nil];
 }

@end
