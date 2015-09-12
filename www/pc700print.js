var Pc700PrintLoader = function (require, exports, module) {
    
    var exec = require('cordova/exec');
    
    var Pc700Print = function () {
	
    };
    
    Pc700Print.prototype.printText = function (text, successCallback, errorCallback) {
        
        if (successCallback === null) {
            successCallback = function (response) {
                console.log('PrintPc700.printText sukses: ' + response);
            };
        }
        
        if (errorCallback === null) {
            errorCallback = function (error) {
                console.error('PrintPc700.printText deshtim: ' + error);
            };
        }
        
        if (typeof errorCallback != "function") {
            console.error("PrintPc700.printText failure: parametri deshtimit nuk eshte funksion");
            return;
        }
        
        if (typeof successCallback != "function") {
            console.error("PrintPc700.printText failure: parametri callback i suksesit duhet te jete patjeter funksion");
            return;
        }
        
        exec(
            successCallback,
            errorCallback,
            "PrintPc700",
            "printText",
            [text]
        );
    };
    
    module.exports = new Pc700Print();
};

Pc700PrintLoader(require, exports, module);
cordova.define("cordova/plugin/Pc700Print", Pc700PrintLoader);
