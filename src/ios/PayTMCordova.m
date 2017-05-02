#import "PayTMCordova.h"
#import <Cordova/CDV.h>

@implementation PayTMCordova{
    NSString* callbackId;
    PGTransactionViewController* txnController;
}

- (void)startPayment:(CDVInvokedUrlCommand *)command {
    
    callbackId = command.callbackId;
//    orderId, customerId, email, phone, amount,
    NSString *orderId  = [command.arguments objectAtIndex:0];
    NSString *customerId = [command.arguments objectAtIndex:1];
    NSString *email = [command.arguments objectAtIndex:2];
    NSString *phone = [command.arguments objectAtIndex:3];
    NSString *amount = [command.arguments objectAtIndex:4];
    
    NSBundle* mainBundle;
    mainBundle = [NSBundle mainBundle];
    
    NSString* paytm_generate_url = [mainBundle objectForInfoDictionaryKey:@"PayTMGenerateChecksumURL"];
    NSString* paytm_validate_url = [mainBundle objectForInfoDictionaryKey:@"PayTMVerifyChecksumURL"];
    NSString* paytm_merchant_id = [mainBundle objectForInfoDictionaryKey:@"PayTMMerchantID"];
    NSString* paytm_ind_type_id = [mainBundle objectForInfoDictionaryKey:@"PayTMIndustryTypeID"];
    NSString* paytm_website = [mainBundle objectForInfoDictionaryKey:@"PayTMWebsite"];
    
    PGMerchantConfiguration* merchant = [PGMerchantConfiguration defaultConfiguration];
    merchant.merchantID = paytm_merchant_id;
    merchant.industryID = paytm_ind_type_id;
    merchant.website = paytm_website;
    merchant.channelID = @"WAP";
    
    merchant.checksumGenerationURL = paytm_generate_url;
    merchant.checksumValidationURL = paytm_validate_url;
    
    PGOrder* order =
    [PGOrder orderForOrderID:orderId customerID:customerId amount:amount customerMail:email customerMobile:phone];
    
    txnController = [[PGTransactionViewController alloc] initTransactionForOrder:order];
    txnController.serverType = eServerTypeProduction;
    txnController.merchant = merchant;
    txnController.delegate = self;
    txnController.loggingEnabled = true;
    UIViewController *rootVC = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
//    [rootVC.navigationController pushViewController:txnController animated:true];
    [rootVC presentViewController:txnController animated:YES completion:nil];
}

//Called when a transaction has completed. response dictionary will be having details about Transaction.
- (void)didSucceedTransaction:(PGTransactionViewController *)controller
                     response:(NSDictionary *)response{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:response];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    [txnController dismissViewControllerAnimated:YES completion:nil];
}

//Called when a transaction is failed with any reason. response dictionary will be having details about failed Transaction.
- (void)didFailTransaction:(PGTransactionViewController *)controller
                     error:(NSError *)error
                  response:(NSDictionary *)response{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:response];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    [txnController dismissViewControllerAnimated:YES completion:nil];
    
}

//Called when a transaction is Canceled by User. response dictionary will be having details about Canceled Transaction.
- (void)didCancelTransaction:(PGTransactionViewController *)controller
                       error:(NSError *)error
                    response:(NSDictionary *)response{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:response];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    [txnController dismissViewControllerAnimated:YES completion:nil];
}

//Called when CHeckSum HASH Generation completes either by PG_Server Or Merchant server.
// - (void)didFinishCASTransaction:(PGTransactionViewController *)controller response:(NSDictionary *)response{
//     CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:response];
//     [self.commandDelegate sendPluginResult:result callbackId:callbackId];
//     [txnController dismissViewControllerAnimated:YES completion:nil];
// }

@end
