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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import com.paytm.pgsdk.*;

public class PayTM extends CordovaPlugin {

    private PaytmPGService paytm_service;
    private String PAYTM_MERCHANT_ID;
    private String PAYTM_INDUSTRY_TYPE_ID;
    private String PAYTM_WEBSITE;

    protected void pluginInitialize() {
        int appResId = cordova.getActivity().getResources().getIdentifier("paytm_merchant_id", "string",
                cordova.getActivity().getPackageName());
        PAYTM_MERCHANT_ID = cordova.getActivity().getString(appResId);
        appResId = cordova.getActivity().getResources().getIdentifier("paytm_industry_type_id", "string",
                cordova.getActivity().getPackageName());
        PAYTM_INDUSTRY_TYPE_ID = cordova.getActivity().getString(appResId);
        appResId = cordova.getActivity().getResources().getIdentifier("paytm_website", "string",
                cordova.getActivity().getPackageName());
        PAYTM_WEBSITE = cordova.getActivity().getString(appResId);
    }

    public static Object wrap(Object o) {
        if (o == null) {
            return JSONObject.NULL;
        }
        if (o instanceof JSONArray || o instanceof JSONObject) {
            return o;
        }
        if (o.equals(JSONObject.NULL)) {
            return o;
        }
        try {
            if (o instanceof Collection) {
                return new JSONArray((Collection) o);
            } else if (o.getClass().isArray()) {
                return new JSONArray(o);
            }
            if (o instanceof Map) {
                return new JSONObject((Map) o);
            }
            if (o instanceof Boolean || o instanceof Byte || o instanceof Character || o instanceof Double
                    || o instanceof Float || o instanceof Integer || o instanceof Long || o instanceof Short
                    || o instanceof String) {
                return o;
            }
            if (o.getClass().getPackage().getName().startsWith("java.")) {
                return o.toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startPayment")) {
            //orderid, cust_id, email, phone, txn_amt
            String options = args.getString(0);

            startPayment(options, callbackContext);
            return true;
        }
        return false;
    }

    private void startPayment(String options, final CallbackContext callbackContext) {

        JSONObject jsonobj = new JSONObject(options);
        String env = jsonobj.getString("ENVIRONMENT");
        if ("production".equalsIgnoreCase(env)) {
            this.paytm_service = PaytmPGService.getProductionService();
        } else {
            this.paytm_service = PaytmPGService.getStagingService();
        }
        Map<String, String> paramMap = new HashMap<String, String>();
        Iterator optkeys = jsonobj.keys();
        while (optkeys.hasNext()) {
            String key = optkeys.next();
            paramMap.put(key, jsonobj.getString(key));
        }
        paramMap.remove("ENVIRONMENT");
        paramMap.put("MID", PAYTM_MERCHANT_ID);
        paramMap.put("INDUSTRY_TYPE_ID", PAYTM_INDUSTRY_TYPE_ID);
        paramMap.put("WEBSITE", PAYTM_WEBSITE);

        PaytmOrder order = new PaytmOrder(paramMap);

        this.paytm_service.initialize(order, null);
        this.paytm_service.startPaymentTransaction(cordova.getActivity(), false, false,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.i("Error", "onTransactionSuccess :" + inResponse);
                        JSONObject json = new JSONObject();
                        Set<String> keys = inResponse.keySet();
                        for (String key : keys) {
                            try {
                                json.put(key, wrap(inResponse.get(key)));
                            } catch (JSONException e) {
                                Log.e("Error", "Error onTransactionSuccess response parsing", e);
                            }
                        }
                        callbackContext.success(json);
                    }

                    @Override
                    public void networkNotAvailable() {
                        Log.i("Error", "networkNotAvailable");
                        JSONObject error = new JSONObject();
                        try {
                            error.put("STATUS", "TXN_FAILURE");
                            error.put("RESPCODE", 501);
                            error.put("RESPMSG", "Network Not Available");
                        } catch (JSONException e) {
                            Log.e("Error", "Error networkNotAvailable json object creation", e);
                        }
                        callbackContext.error(error);
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Log.i("Error", "clientAuthenticationFailed :" + inErrorMessage);
                        JSONObject error = new JSONObject();
                        try {
                            error.put("STATUS", "TXN_FAILURE");
                            error.put("RESPCODE", 922);
                            error.put("RESPMSG", inErrorMessage);
                        } catch (JSONException e) {
                            Log.e("Error", "Error clientAuthenticationFailed json object creation", e);
                        }
                        callbackContext.error(error);
                    }

                    @Override
                    public void someUIErrorOccurred(String arg0) {
                        Log.i("Error", "someUIErrorOccurred :" + arg0);
                        JSONObject error = new JSONObject();
                        try {
                            error.put("STATUS", "TXN_FAILURE");
                            error.put("RESPCODE", 501);
                            error.put("RESPMSG", "System Error");
                        } catch (JSONException e) {
                            Log.e("Error", "Error someUIErrorOccurred json object creation", e);
                        }
                        callbackContext.error(error);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                        Log.i("Error", "onErrorLoadingWebPage arg0  :" + iniErrorCode);
                        Log.i("Error", "onErrorLoadingWebPage arg1  :" + inErrorMessage);
                        Log.i("Error", "onErrorLoadingWebPage arg2  :" + inFailingUrl);
                        JSONObject error = new JSONObject();
                        try {
                            error.put("STATUS", "TXN_FAILURE");
                            error.put("RESPCODE", iniErrorCode);
                            error.put("RESPMSG", inErrorMessage);
                            error.put("ERRURL", inFailingUrl);
                        } catch (JSONException e) {
                            Log.e("Error", "Error onErrorLoadingWebPage json object creation", e);
                        }
                        callbackContext.error(error);
                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        Log.i("Error", "back button pressed");
                        JSONObject error = new JSONObject();
                        try {
                            error.put("STATUS", "TXN_FAILURE");
                            error.put("RESPCODE", 141);
                            error.put("RESPMSG", "Request cancelled by Customer");
                        } catch (JSONException e) {
                            Log.e("Error", "Error onBackPressedCancelTransaction json object creation", e);
                        }
                        callbackContext.error(error);
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.i("Error", "onTransactionCancel :" + inErrorMessage);
                        JSONObject error = new JSONObject();
                        Set<String> keys = inResponse.keySet();
                        for (String key : keys) {
                            try {
                                error.put(key, wrap(inResponse.get(key)));
                            } catch (JSONException e) {
                                Log.e("Error", "Error onTransactionCancel json object creation", e);
                            }
                        }
                        callbackContext.error(error);
                    }

                });
    }
}