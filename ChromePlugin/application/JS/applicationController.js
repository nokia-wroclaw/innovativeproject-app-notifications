// Module
var applicationController = angular.module('clientApplication',['ngCookies']);


// Components
applicationController.component("notificationElement" ,{
    templateUrl : '../HTML/notification.html',
    controller : 'notificationController',
    bindings: {
        removeMy: '=',
        myNotification: '=',
        requestFocus: '=',
        addSelf: '='
    }
});

applicationController.component("notificationsList" ,{
    templateUrl : '../HTML/notificationsList.html',
    controller : 'notificationListController'
});

applicationController.component("loginWindow" ,{
    templateUrl : '../HTML/login.html',
    controller :  'loginController'
});

applicationController.component("upperBar" ,{
    templateUrl : '../HTML/upperBar.html',
    controller : 'upperBarrController'
});


//  Values
applicationController.value("Token");

applicationController.value("changeWindow");

applicationController.value("setApplicationStyle");

applicationController.value("notificationsJSON");


//Controllers
applicationController.controller("mainController" , function($http,$cookies,$scope) {

    applicationController.Token = $cookies.get("Token");
    $scope.isCliendLoged =( applicationController.Token != null );

    // functions
    applicationController.changeWindow = function () {

        applicationController.Token = $cookies.get("Token");
        $scope.isCliendLoged =( applicationController.Token != null );
    }

    applicationController.setApplicationStyle = function(appStyle) {
        $scope.applicationSyle = appStyle;
    }

    // defaults
});

applicationController.controller("upperBarrController" , function($cookies,$scope) {

    // functions
    $scope.setStyle = function () {

        if ($scope.styleBox == "T"){

            $cookies.put("style","T");
            applicationController.setApplicationStyle('../CSS/applicationLight.css');

        }else{

            $cookies.put("style","F");
            applicationController.setApplicationStyle('../CSS/applicationDarkula.css');
        }
    }


    // defaults
    if( $cookies.get("style") == null){
        $cookies.put("style","F");
    }

    $scope.styleBox = $cookies.get("style");
    $scope.setStyle();

});

applicationController.controller("loginController" , function($cookies,$scope) {


    // functions
    $scope.inputReset = function () {
        $scope.loginInputStyle = "login-input login-text";
    }

    $scope.validate = function () {

        if( $scope.username == "user" && $scope.password == "paslo" ){
            var Token = "AA11";
            $cookies.put("Token",Token);
            applicationController.changeWindow();

        }else{
            $scope.loginInputStyle = "login-input login-text wrong";
        }
    }


    // defaults
    $scope.loginInputStyle = "login-input login-text";

});

applicationController.controller("notificationListController" , function($http,$cookies,$scope) {

    var elements = [];

    $scope.addThis = function (element) {
        elements.push(element);
    };

    $scope.elementRequestFocus = function(element){
        if (element.focused==false) {
            angular.forEach(elements, function (value) {
                value.loseFocus();
            });

            element.getFocus();
        }
    };

    $scope.removeElement = function(element){
        element.hideElement();

        elements.splice(elements.indexOf(element),1);
    };

    $scope.logout = function () {
        $cookies.remove("Token");
        applicationController.changeWindow();
    };

    $http.get('../JSON/notifications.json')
        .then(function(res){
            $scope.notificationsJSON = res.data;
        });

});

applicationController.controller("notificationController" , function ($scope) {

    this.focused = false;

    //functions
    $scope.remove = function () {
        delete this;
        $scope.delet = "deleted !!";
    };

    this.hideElement = function () {
        $scope.notificationFocus="hidden";
    }

    this.loseFocus = function () {
        $scope.notificationFocus="notification-div";
        this.focused = false;
    };

    this.getFocus = function () {
        $scope.notificationFocus=$scope.notificationFocus+" div-selected";
        this.focused = true;
    };

    //defaults
    $scope.notificationFocus="notification-div";
    $scope.delet = "delete";

});