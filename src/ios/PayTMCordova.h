#import "PaymentsSDK.h"
#import <Cordova/CDV.h>

@interface PayTMCordova : CDVPlugin <PGTransactionDelegate>

- (void)startPayment:(CDVInvokedUrlCommand*)command;

@end
