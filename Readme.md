Installation
============

```
cordova plugin add https://github.com/ArunYogi/paytm-cordova-plugin.git --variable MERCHANT_ID=<MerchantID> --variable INDUSTRY_TYPE_ID=<IndustryType> --variable WEBSITE=<WAPWebsiteName> --save
```


Usage
=====

Add below line to declaration.d.ts file
```
declare var paytm : any;
```

```
paytm.startPayment(txn_id, customer_id, email, phone, amount, callbackurl, environment, successCallback, failureCallback);
```