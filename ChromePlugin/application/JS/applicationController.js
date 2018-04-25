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
    };

    applicationController.setApplicationStyle = function(appStyle) {
        $scope.applicationSyle = appStyle;
    }

    // defaults
});

applicationController.controller("upperBarrController" , function($cookies,$scope) {

    // functions
    $scope.setStyle = function () {

        if ($scope.styleBox === "T"){

            $cookies.put("style","T");
            applicationController.setApplicationStyle('../CSS/applicationLight.css');

        }else{

            $cookies.put("style","F");
            applicationController.setApplicationStyle('../CSS/applicationDarkula.css');
        }
    };


    // defaults
    if( $cookies.get("style") == null){
        $cookies.put("style","F");
    }

    $scope.styleBox = $cookies.get("style");
    $scope.setStyle();

});

applicationController.controller("loginController" , function($http,$cookies,$scope) {


    // functions
    $scope.inputReset = function () {
        $scope.loginInputStyle = "login-input login-text";
    };

    $scope.validate = function () {

       $http.get('http://35.204.202.104:8080/api/v1.0/login/',{
            params: {username:$scope.username,password:$scope.password }})
            .then(function(response){
                this.Token = response.data;
                $cookies.put("Token",Token);
                applicationController.changeWindow();
            });
    };


    // defaults
    $scope.loginInputStyle = "login-input login-text";

});

applicationController.controller("notificationListController" , function($interval,$http,$cookies,$scope) {

    var elements = [];
    $scope.notificationsJSON = [];

    $scope.addThis = function (element) {
        elements.push(element);
    };

    $scope.elementRequestFocus = function(element){
        if (element.focused===false) {
            angular.forEach(elements, function (value) {
                value.loseFocus();
            });

            element.getFocus();
        }
    };

    $scope.removeElement = function(element){
        //element.hideElement();
        $scope.notificationsJSON.notifications.splice( $scope.notificationsJSON.notifications.indexOf(element.myNotification), 1 );
        elements.splice(elements.indexOf(element),1);
        $scope.$apply();
    };

    $scope.logout = function () {
        $cookies.remove("Token");
        applicationController.changeWindow();
    };


    $scope.refreshList = function(){
        //$http.get('../JSON/notifications.json')
        $http.get('http://35.204.202.104:8080/api/v1.0/notf/' ,{
           params: {token:$cookies.get("Token")}})
            .then(function(response){
                elements = [];
                $scope.notificationsJSON = [];
                $scope.notificationsJSON = response.data;
            });
    };

    $scope.refreshList();

/*    $scope.myTimer = 0;
    stop = $interval(function() {
        $scope.myTimer = $scope.myTimer+1;
    }, 1000);*/

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
    };

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