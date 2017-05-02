package com.paytm.cordova;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.paytm.pgsdk.*;

public class PayTM extends CordovaPlugin {

    private PaytmPGService paytm_service;

    private String PAYTM_GENERATE_URL;
    private String PAYTM_VALIDATE_URL;

    private String PAYTM_MERCHANT_ID;
    private String PAYTM_INDUSTRY_TYPE_ID;
    private String PAYTM_WEBSITE;

    protected void pluginInitialize() {
        int appResId = cordova.getActivity().getResources().getIdentifier("paytm_gen_url", "string", cordova.getActivity().getPackageName());
        PAYTM_GENERATE_URL = cordova.getActivity().getString(appResId);
        appResId = cordova.getActivity().getResources().getIdentifier("paytm_chk_url", "string", cordova.getActivity().getPackageName());
        PAYTM_VALIDATE_URL = cordova.getActivity().getString(appResId);
        appResId = cordova.getActivity().getResources().getIdentifier("paytm_merchant_id", "string", cordova.getActivity().getPackageName());
        PAYTM_MERCHANT_ID = cordova.getActivity().getString(appResId);
        appResId = cordova.getActivity().getResources().getIdentifier("paytm_industry_type_id", "string", cordova.getActivity().getPackageName());
        PAYTM_INDUSTRY_TYPE_ID = cordova.getActivity().getString(appResId);
        appResId = cordova.getActivity().getResources().getIdentifier("paytm_website", "string", cordova.getActivity().getPackageName());
        PAYTM_WEBSITE = cordova.getActivity().getString(appResId);
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        if (action.equals("startPayment")) {
            //orderid, cust_id, email, phone, txn_amt
            startPayment(args.getString(0), args.getString(1), args.getString(2), args.getString(3), args.getString(4), callbackContext);
            return true;
        }
        return false;
    }

    private void startPayment(final String order_id,
                              final String cust_id,
                              final String email,
                              final String phone,
                              final String txn_amt,
                              final CallbackContext callbackContext){

        paytm_service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("REQUEST_TYPE", "DEFAULT");
        paramMap.put("ORDER_ID", order_id);
        paramMap.put("MID", PAYTM_MERCHANT_ID);
        paramMap.put("CUST_ID", cust_id);
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("INDUSTRY_TYPE_ID", PAYTM_INDUSTRY_TYPE_ID);
        paramMap.put("WEBSITE", PAYTM_WEBSITE);
        paramMap.put("TXN_AMOUNT", txn_amt);
        paramMap.put("EMAIL", email);
        paramMap.put("MOBILE_NO", phone);
        paramMap.put("THEME", "merchant");

        PaytmOrder order = new PaytmOrder(paramMap);
        PaytmMerchant merchant = new PaytmMerchant(this.PAYTM_GENERATE_URL, this.PAYTM_VALIDATE_URL);

        this.paytm_service.initialize(order, merchant, null);
        this.paytm_service.startPaymentTransaction(cordova.getActivity(), false, false, new PaytmPaymentTransactionCallback()
        {

            @Override
            public void onTransactionSuccess(Bundle inResponse) {
                Log.i("Error", "onTransactionSuccess :" + inResponse);
//                onTransactionSuccess :Bundle[{GATEWAYNAME=WALLET, PAYMENTMODE=PPI, TXNDATE=2015-02-19 17:01:42.0, STATUS=TXN_SUCCESS, MID=sumjkE62398232705701, CURRENCY=INR, ORDERID=5384643, TXNID=70013, IS_CHECKSUM_VALID=N, TXNAMOUNT=100.00, BANKTXNID=CC9795B5013489B9, BANKNAME=, RESPMSG=Txn Successful., RESPCODE=01, CHECKSUMHASH=8liiSa0uQ0S1lCALiQA3FsyQx6xMey9m8VrF+WZu1tTxG+72c3bU1UYZZg+j/UMS5w9F8iHXq051G4/XtVe4L7FSTk5PGnQpp4r6+QkuyWM=}]
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, 0));
            }

            @Override
            public void onTransactionFailure(String inErrorMessage,Bundle inResponse)
            {
                Log.i("Error","onTransactionFailure :"+inErrorMessage);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, 0));
            }


            @Override
            public void clientAuthenticationFailed(String inErrorMessage)
            {
                Log.i("Error","clientAuthenticationFailed :"+inErrorMessage);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, 0));
            }


            @Override
            public void networkNotAvailable() {
                // TODO Auto-generated method stub
                Log.i("Error","networkNotAvailable");
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, 0));
            }

            @Override
            public void onErrorLoadingWebPage(int arg0, String arg1, String arg2) {
                // TODO Auto-generated method stub
                Log.i("Error","onErrorLoadingWebPage arg0  :"+arg0);
                Log.i("Error","onErrorLoadingWebPage arg1  :"+arg1);
                Log.i("Error","onErrorLoadingWebPage arg2  :"+arg2);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, 0));
            }

            @Override
            public void someUIErrorOccurred(String arg0) {
                // TODO Auto-generated method stub
                Log.i("Error","someUIErrorOccurred :"+arg0);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, 0));
            }
        });
    }
}