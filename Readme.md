Installation
============


```
cordova plugin add cordova-plugin-paytm --variable MERCHANT_ID=<MerchantID> --variable INDUSTRY_TYPE_ID=<IndustryType> --variable WEBSITE=<WAPWebsiteName> --save
```

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

```
paytm.startPayment(options, successCallback, failureCallback);
```

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
Note: 
# Version 0.0.3 is not backward compatibile with 0.0.2. In v0.0.3, send options as JSON object where in 0.0.2, send each value separately i.e (txn_id, customer_id, email, phone, amount, callbackurl, environment,..,..)
# ENVIRONMENT info has to be part of 'options', which says the environment  transaction has to be started. Possible values are 'staging' and 'production'.
# Send transaction amount in String format, as that is the format accepted in iOS platform.