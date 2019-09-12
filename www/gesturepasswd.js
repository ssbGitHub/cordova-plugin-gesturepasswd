var exec = require('cordova/exec');

exports.gesturescreen = {
    showVerify: function (successVerity, errorVerity) {
        exec(successVerity, errorVerity, 'GesturePasswd', 'showVerify', []);
    },

    hideVerify: function (hideSuccess) {
        exec(hideSuccess, null, "GesturePasswd", "hideVerify", []);
    },

    showSetting: function (successSetting, errorSetting) {
        exec(successSetting, errorSetting, 'GesturePasswd', 'showSetting', []);
    },

    hideSetting: function () {
        exec(null, null, "GesturePasswd", "hideSetting", []);
    },

    showLogin: function (successLogin, errorLogin, redirect, userName) {
        exec(successLogin, errorLogin, 'GesturePasswd', 'showLogin', [redirect, userName]);
    },

    hideLogin: function () {
        exec(null, null, "GesturePasswd", "hideLogin", []);
    },

    loginFail: function (successLogin, errorLogin, errMsg) {
        exec(successLogin, errorLogin, 'GesturePasswd', 'loginFail', [errMsg]);
    }
};

