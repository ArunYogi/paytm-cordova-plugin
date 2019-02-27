# Implementation of Cordova Plugin in Ionic App For PayTM Integration

This plugin can be used to integrate PayTM Payment Gateway in your Cordova App.

In this doc, you'll find how to implemenat this plugin in an Ionic App.

## Getting Started

### Prerequisites

1. **PayTM Credentials**

    Firstly, you'll have to register on PayTM Business as a Merchant for PayTM Gateway Credentials.
    
    You can register here - [PayTM Payment Gateway](https://business.paytm.com/payment-gateway).

    You'll be provided with the Sandbox/Staging  or API Keys.
    
    * Merchant ID
    * App Url : "APPSTAGING" (For Staging Environment)
    * Sandbox Merchant Key
    * Industry Type


2. **Checksum Generation and Verification API**
    
    Visit the [PayTM Gateway Docs](https://developer.paytm.com/docs/v1/payment-gateway) and implement the provided Checksum Generation and Verification Functions on your back-end/server (_like Firebase Cloud Functions_).


### Installation

1. Open terminal and `cd` to the _root directory_ of Ionic Project.

2. Run the following command with the `--save` option : - 
    ```
    ionic cordova plugin add cordova-plugin-paytm --variable MERCHANT_ID=<MerchantID> --variable INDUSTRY_TYPE_ID=<IndustryType> --variable WEBSITE=<WAPWebsiteName> --save
    ```

    _For e.g.: -_
    _(Staging Environment)_
    ```
    cordova-plugin-paytm --variable MERCHANT_ID=ABC12345 --variable INDUSTRY_TYPE_ID=Retail --variable WEBSITE=APPSTAGING --save
    ```

3. Ensure the plugin entry in `config.xml`.
    
    _For e.g.: -_
    _(Staging Environment)_
    ```
    <plugin name="cordova-plugin-paytm" spec="0.0.10">
        <variable name="MERCHANT_ID" value="ABC12345" />
        <variable name="INDUSTRY_TYPE_ID" value="Retail" />
        <variable name="WEBSITE" value="APPSTAGING" />
    ```

### Usage

1. **Before proceeding, check whether Cordova is present.**
    ```
    if (!(<any>window).cordova) {
        // Cordova Not Present
        return;
    } else {
        // Proceed Forword
    }
    ```

2. **Prepare Transaction request** as per the [PayTM Gateway Docs](https://developer.paytm.com/docs/v1/payment-gateway).

    For e.g.: -
    ```
    let txnRequest = {
        "MID": "ABC12345",                  // PayTM Credentials
        "ORDER_ID": "ORDER0000000001",      //Should be unique for every order.
        "CUST_ID": "CUST0001",
        "INDUSTRY_TYPE_ID": "Retail",       // PayTM Credentials
        "CHANNEL_ID": "WAP",                // PayTM Credentials
        "TXN_AMOUNT": "1",                  // Transaction Amount should be a String
        "WEBSITE": "APPSTAGING",            // PayTM Credentials
    }
    ```

3. **Add Callback URL to the Transaction Request.**

    Please note that you have to add the latest Android/iOS SDK CALLBACK URL from [here](https://developer.paytm.com/docs/v1/android-sdk).

    > Staging Environment: "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=<order_id>" 

    > Production Environment: "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=<order_id>"

    **Note: -** _Always add the Order ID to the end._

    _For e.g.: -_
    ```
    txnRequest.CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + txnRequest.ORDER_ID;
    ```


4. **Generate Checksum for Transaction Request** by sending the `TxnRequest` to the Checksum generation function. 

    **Note: -** _CHECKSUMHASH has to be created without `ENVIRONMENT` property._ 
    _`ENVIRONMENT` Property is used by the plugin to determine which Transaction Endpoint to hit, and is removed before sending Request to PayTM._

    ```
    try {
        txnRequest = await this.generateChecksumAPI(txnRequest);
    }
    catch (err) {
        console.error('Error Generating Checksum', err)
    }
    ```


5. **Add `"ENVIRONMENT"` Property to the Request.**

    `ENVIRONMENT` Property is used by the plugin to determine which Transaction Endpoint to use, 'staging' or 'production', and is removed before sending Request to PayTM. 

    ```
    // For Staging Enviroment
    txnRequest["ENVIRONMENT"] = "staging"

    // For Production Enviroment
    txnRequest["ENVIRONMENT"] = "production"
    ```


6. **Define the `successCallback` and `failureCallback`.**

    These Callbacks will be used by the Plugin. 
    
    `successCallback` will receive a response object as defined in the [PayTM Gateway Docs](https://developer.paytm.com/docs/v1/payment-gateway).

    **Note: -** _List of Response Codes is available [here](https://developer.paytm.com/assets/Transaction%20response%20codes%20and%20messages.pdf)._

    ``` 
    const successCallback = (response) => {
        if (response.STATUS == "TXN_SUCCESS") {
            // Verify Transaction Status and Amount.
            // Proceed further...
            // Refer PayTM Gateway Docs for Response Attributes/Properties
        } else {
            // response.RESPCODE will be the error code.
            
            alert(`Transaction Failed for reason: - ${response.RESPMSG} (${response.RESPCODE})`);

            // Handle Error...
        }
    }
    ```

    ```
    const failureCallback = (error) => {
        // response.RESPCODE will be the error code.
            
        alert(`Transaction Failed for reason: - ${response.RESPMSG} (${response.RESPCODE})`);

        // Handle Error...
    }
    ```

7. **Call the plugin and Initiate the Payment.**

    ```
    (<any>window).paytm.startPayment(
		txnRequest,
		successCallback,
		failureCallback
	);
    ```

    _`txnRequest` will look like this: -_
    ```
    {
        "MID": "ABC12345",                  // PayTM Credentials
        "ORDER_ID": "ORDER0000000001",      //Should be unique for every order.
        "CUST_ID": "CUST0001",
        "INDUSTRY_TYPE_ID": "Retail",       // PayTM Credentials
        "CHANNEL_ID": "WAP",                // PayTM Credentials
        "TXN_AMOUNT": "1",                  // Transaction Amount should be a String
        "WEBSITE": "APPSTAGING",            // PayTM Credentials
        "CALLBACK_URL": "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=ORDER0000000001",         // Contains Order-ID
        "CHECKSUMHASH": "w2QDRMgp1/BNdEnJEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4="          // Generated by our Back-End/Server/Firebase
        "ENVIRONMENT": "staging"            // Used and removed by plugin.
    }
    ```

8. **Verify Transaction Status and Transaction Amount** as per the [PayTM Gateway Docs](https://developer.paytm.com/docs/).



## Few Points to Note

* Version 0.0.3 is not backward compatibile with 0.0.2. 

    In v0.0.3, send options as JSON object where in 0.0.2, send each value separately i.e (txn_id, customer_id, email, phone, amount, callbackurl, environment,..,..)

* Make sure to use the correct `CALLBACK_URL` and Always add the Order ID to its end. 
Refer [here](https://developer.paytm.com/docs/v1/android-sdk) for the URL.

* `CHECKSUMHASH` has to be created without `ENVIRONMENT` property. 
Add `ENVIRONMENT` after generating the `CHECKSUMHASH`.

* `ENVIRONMENT` property has to be part of `txnRequest`. 
It is used by the plugin to determine which Transaction Endpoint to use, and is removed before sending Request to PayTM. 

    _Possible Values are `staging` and `production`._

* Send `Transaction Amount` in `String` format, as that's the format accepted in iOS platform.

* If you want to change the PayTM Credentials provided at the time of Plugin Installation, best method is to remove the plugin and re-install it with new credentials.
    
    _To remove plugin: -_
    ```
    ionic cordova plugin remove cordova-plugin-paytm --save
    ```


## Bibliography

1. [PayTM Developer Docs](https://developer.paytm.com/docs/)
2. [PayTM Business Login](https://business.paytm.com/)
3. [PayTM Payment Gateway Docs](https://developer.paytm.com/docs/v1/payment-gateway)
4. [PayTM Android SDK Docs](https://developer.paytm.com/docs/v1/android-sdk)
5. [PayTM Credentials/API Keys](https://dashboard.paytm.com/next/apikeys)
6. [PayTM List of Transaction Response Codes and Messages ](https://developer.paytm.com/assets/Transaction%20response%20codes%20and%20messages.pdf)


## Common Issues

1. **Network Not Available (RESPCODE: 501)** 
[#5](https://github.com/ArunYogi/paytm-cordova-plugin/issues/5)

    *Solution: -* 
    This issue is faced when `ENVIRONMENT` property is not added to the `txnRequest`. 
    Adding `ENVIRONMENT` property to `txnRequest` resolves this issue.

2. **Unable to build project after adding plugin**
[#6](https://github.com/ArunYogi/paytm-cordova-plugin/issues/6)

    *Solution: -*
    This can be resolved by adding Android 6.4.0 instead of Android 7.0.0 with cordova using the commands.

    ```
    cordova platform remove android
    cordova platform add android@6.4.0
    cordova build android
    ```

3. **Paytm screen keeps waiting for response** 
[#7](https://github.com/ArunYogi/paytm-cordova-plugin/issues/7)

    *Solution: -*
    This issue is faced when either
    
    * `CALLBACK_URL` is wrong.
    * `WEBSITE` is not set to `APPSTAGING` (Incase of stagin environment).
    * `CALLBACK_URL` is blocked or wrongly configured at PayTM's end. Contact PayTM Support in this case.
