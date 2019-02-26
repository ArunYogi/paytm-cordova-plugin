Installation
============


```
cordova plugin add cordova-plugin-paytm --variable MERCHANT_ID=<MerchantID> --variable INDUSTRY_TYPE_ID=<IndustryType> --variable WEBSITE=<WAPWebsiteName> --save
```

**Note: -** For Ionic Implementation, refer [README-IONIC.md](https://github.com/ArunYogi/paytm-cordova-plugin/blob/master/README-IONIC.md)


Supported Platform
==================
Android
iOS


Usage
=====

Add below line to declaration.d.ts file
```
declare var paytm : any;
```

Now you can start a transaction by using below lines
```
paytm.startPayment(options, successCallback, failureCallback);
```
"options" is where you send payment, payee & collector information to paytm plugin. Sample "options" that has to be send to the plugin to start the transactions is
```
{
  "ENVIRONMENT" : "staging", // environment details. staging for test environment & production for live environment
  "REQUEST_TYPE": "DEFAULT", // You would get this details from paytm after opening an account with them
  "MID": "PAYTM_MERCHANT_ID", // You would get this details from paytm after opening an account with them
  "ORDER_ID": "ORDER0000000001", // Unique ID for each transaction. This info is for you to track the transaction details
  "CUST_ID": "10000988111", // Unique ID for your customer
  "INDUSTRY_TYPE_ID": "PAYTM_INDUSTRY_TYPE_ID", // You would get this details from paytm after opening an account with them
  "CHANNEL_ID": "WAP", // You would get this details from paytm after opening an account with them
  "TXN_AMOUNT": "1", // Transaction amount that has to be collected
  "WEBSITE": "PAYTM_WEBSITE", // You would get this details from paytm after opening an account with them
  "CALLBACK_URL": "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=ORDER0000000001", // Callback url
  "EMAIL": "abc@gmail.com", // Email of customer
  "MOBILE_NO": "9999999999", // Mobile no of customer
  "CHECKSUMHASH": "w2QDRMgp1/BNdEnJEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4="
}
```

**CHECKSUM has to be created without "ENVIRONMENT" field info, it is only for plugin's internal use to determine which environment it's going to hit (staging/production).**

In SuccessCallback method, you will get response object as json, with infromation present in http://paywithpaytm.com/developer/paytm_api_doc?target=interpreting-response-sent-by-paytm
```
var successCallback(response) {
    if (response.STATUS == "TXN_SUCCESS") {
        var txn_id = response.TXNID;
        var paymentmode = response.PAYMENTMODE;
        // other details and function after payment transaction
    } else {
        // error code will be available in RESPCODE
        // error list page https://docs.google.com/spreadsheets/d/1h63fSrAmEml3CYV-vBdHNErxjJjg8-YBSpNyZby6kkQ/edit#gid=2058248999
        alert("Transaction Failed for reason " + response.RESPMSG);
    }
}

var failureCallback(error) {
    // error code will be available in RESCODE
    // error list page https://docs.google.com/spreadsheets/d/1h63fSrAmEml3CYV-vBdHNErxjJjg8-YBSpNyZby6kkQ/edit#gid=2058248999
    alert("Transaction Failed for reason " + error.RESPMSG);
}
```

Sample response for paytm:
```
{
  "TXNID": "414709",
  "BANKTXNID": "",
  "ORDERID": "ORDER48886809916",
  "TXNAMOUNT": "1.00",
  "STATUS": "OPEN",
  "TXNTYPE": "SALE",
  "GATEWAYNAME": "",
  "RESPCODE": "",
  "RESPMSG": "",
  "BANKNAME": "",
  "MID": "klbGlV59135347348753",
  "PAYMENTMODE": "CC",
  "REFUNDAMT": "0.00",
  "TXNDATE": "2015-11-02 11:40:46.0"
}
```

References:
===========
* [Various fields avialbale in "options"](http://paywithpaytm.com/developer/paytm_api_doc?target=transaction-request-api)
* [Doc on response from the plugin](http://paywithpaytm.com/developer/paytm_api_doc?target=interpreting-response-sent-by-paytm)
* [Logic to generate checksum](http://paywithpaytm.com/developer/paytm_api_doc?target=generating-checksum)
* Paytm suggests to verify the transaction status manually after the successful transaction. [Refer this link to know about it](http://paywithpaytm.com/developer/paytm_api_doc?target=txn-status-api)

Note:
=====
* Version 0.0.9
  * Corrected the file path of config xml file in plugin.xml
* Version 0.0.8
  * Decreased cordova support version for older apps
* Version 0.0.7
  * Upgraded Paytm library which has fixes related to redirecting control from paytm site to mobile app
* Version 0.0.6
  * Removed library and used gradle depencies to pull paytm library
* Version 0.0.5 
  * Updated the paytm library to v1.2.3 in android and latest (as of 6 Dec 2018 ) lib file in ios.
* Version 0.0.3 is not backward compatibile with 0.0.2. In v0.0.3, send options as JSON object where in 0.0.2, send each value separately i.e (txn_id, customer_id, email, phone, amount, callbackurl, environment,..,..)
* Send transaction amount in String format, as that is the format accepted in iOS platform.
