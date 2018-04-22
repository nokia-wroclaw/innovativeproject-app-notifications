// Module
var applicationController = angular.module('clientApplication',['ngCookies']);


// Components
applicationController.component("notificationInfo" ,{
    templateUrl : '../HTML/notification.html'
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


//Controllers
applicationController.controller("mainController" , function($cookies,$scope) {


    // functions
    applicationController.changeWindow = function () {

        applicationController.Token = $cookies.get("Token");
        $scope.isCliendLoged =( applicationController.Token != null );
    }

    applicationController.setApplicationStyle = function(appStyle) {
        $scope.applicationSyle = appStyle;
    }


    // defaults
    //$scope.applicationSyle = '../CSS/applicationDarkula.css';
    applicationController.changeWindow();


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

applicationController.controller("notificationListController" , function($cookies,$scope) {

    // functions
    $scope.logout = function () {
        $cookies.remove("Token");
        applicationController.changeWindow();
    }

});
