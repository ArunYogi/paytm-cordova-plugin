Installation
============

```
cordova plugin add https://github.com/ArunYogi/paytm-cordova-plugin.git --variable GENERATE_URL=<Checksum Generation URL> --variable VERIFY_URL=<Checksum Validation Url> --variable MERCHANT_ID=<MerchantID> --variable INDUSTRY_TYPE_ID=<IndustryType> --variable WEBSITE=<WAPWebsiteName>
```

Usage
=====

```
window.plugins.paytm.startPayment(txn_id, customer_id, email, phone, amount, successCallback, failureCallback);
```