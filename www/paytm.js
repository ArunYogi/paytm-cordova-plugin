module.exports = {
    startPayment: function(options, env, successCallback, failureCallback) {
        cordova.exec(successCallback,
            failureCallback,
            "PayTM",
            "startPayment", [JSON.stringify(options), env]);
    }
};