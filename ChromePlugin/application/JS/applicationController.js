// Module
const app = angular.module('clientApplication',['ngCookies']);


// Components

app.component("notificationsGroup" ,{
    templateUrl : '../HTML/notificationGroup.html',
    controller : 'notificationGroupController',
    bindings: {
        group: '=', // data about the group represented by this element
        requestFocus: '=', // callback function for requesting focus
        addSelf: '=' // callback function for adding this element to elements table
    }
});

app.component("notificationElement" ,{
    templateUrl : '../HTML/notification.html',
    controller : 'notificationController',
    bindings: {
        removeMy: '=',  // callback function for removing this element
        myNotification: '=',    // data about notification represented by this element
        requestFocus: '=', // callback function for requesting focus
        addSelf: '=' // callback function for adding this element to elements table
    }
});

app.component("customLogo" , {
    templateUrl : '../HTML/logo.html'
});

app.component("notificationsList" ,{
    templateUrl : '../HTML/notificationsList.html',
    controller : 'notificationListController',
    bindings: {
        type: '=',  // type of the elements stored on this list, can by 'groups' or 'notifications'
        from: '='   // url added after API address, under with data about list elements are stored
    }
});

app.component("loginWindow" ,{
    templateUrl : '../HTML/login.html',
    controller :  'loginController'
});

app.component("registerWindow" ,{
    templateUrl : '../HTML/register.html',
    controller :  'registerController'
});

app.component("upperBar" ,{
    templateUrl : '../HTML/upperBar.html',
    controller : 'upperBarrController'
});

app.component("userPanel",{
    templateUrl : '../HTML/userPanel.html',
    controller : 'userPanelController'
});

app.component("dialogPopup", {
    templateUrl : '../HTML/myPopup.html',
    controller : 'popupController',
    bindings: {
        myCallback: '=', // Callback that is called after selecting on of the popup options
        context: '='    //  popup context. Element that called popup
    }
});


//  Values

// Define in mainController
app.value("Token");     // Token that allows application to connect with RestAPI

app.value("viewEnum");  // enumeration of possible view options

app.value("setView");   // function that allows to change vie mode

app.value("setApplicationStyle");    // function that allows to set style of application (light - dark)

//  Testing values
app.value("showPopup");

app.value("hidePopup");


//  Controllers

app.controller("mainController" , function($http,$cookies,$scope) {


    //  Functions

    app.setView = function (viewElement) {
        $scope.mainView = viewElement;
    };

    app.setApplicationStyle = function(appStyle) {
        $scope.applicationSyle = appStyle;
    };


    //  Defaults

    app.viewEnum = {
        LOGIN : 1,
        LIST : 2,
        REGISTER : 3
    };


    //  Initialize

    app.Token = $cookies.get("Token"); //   Check if token is stored in cookies
    if (app.Token == null){
        app.setView(app.viewEnum.LOGIN); // If not, set Login view mode
    }else {
        app.setView(app.viewEnum.LIST); // If yes, set notification list view mode
    }


});

app.controller("upperBarrController" , function($cookies,$scope) {


    //  Functions

    $scope.setStyle = function () {     // This function is switching between styles of application

        if ($scope.styleBox === "T"){ // If current style is dark
            // Set light style

            $cookies.put("style","T");
            app.setApplicationStyle('../CSS/applicationLight.css');

        }else{
            // otherwise, Set dark style

            $cookies.put("style","F");
            app.setApplicationStyle('../CSS/applicationDarkula.css');
        }
    };


    // Initialize

    if( $cookies.get("style") == null){ // check if style is stored in cookies
        $cookies.put("style","F"); // if not set style to default (dark)
    }

    $scope.styleBox = $cookies.get("style"); // get style from cookies
    $scope.setStyle();

});

app.controller("loginController" , function($http,$cookies,$scope) {


    //  Functions

    $scope.registerNewUser = function(){ // Change view to register mode
        app.setView(app.viewEnum.REGISTER);
    };

    $scope.inputReset = function () { // Reset input style
        $scope.info = "";
        $scope.loginInputStyle = "login-input login-text";
    };

    $scope.validate = function () { // Validate login data

        app.setView(app.viewEnum.LIST);

/*       $http.get('http://35.204.202.104:8080/api/v1.0/login/',{
            params: {username:$scope.username,password:$scope.password }})
            .then(
                //Success
                function(response){
                this.Token = response.data;
                $cookies.put("Token",Token);
                app.setView(app.viewEnum.LIST);
                },
                //Failure
                function (response) {
                    $scope.loginInputStyle = $scope.loginInputStyle + " wrong";
                    $scope.info = response.data;
                }
            );*/
    };


    //  Initialize

    $scope.loginInputStyle = "login-input login-text";


});

app.controller("notificationListController" , function($interval,$http,$cookies,$scope) {

    //  Functions

    $scope.addThis = function (element) { // This function is adding elements that represents list data to elements list
        elements.push(element);
    };

    $scope.elementRequestFocus = function(element){ // This function is called when one of the elements is requesting focus
        if (element.focused===false) {
            if ($scope.type === "notifications"){ //  If list that stores elements is of "notifications" type the rest of elements loses focus
                angular.forEach(elements, function (value) {
                    value.loseFocus();
                });
            }

            element.getFocus();
        }else {                     //  If element is already focused, it loses the focus
            element.loseFocus();
        }
    };

    $scope.removeElement = function(element){   // This function removes element from list
        $scope.storedData.notifications.splice($scope.storedData.notifications.indexOf(element.myNotification), 1);
        elements.splice(elements.indexOf(element), 1);
        $scope.$apply();
    };

    $scope.loadData = function(from,type){ // This function loads data from specified location in to storedData array
        $scope.type = type;
        $http.get('../JSON/'+from+'.json')
            .then(function(response){
                elements = [];
                $scope.storedData = [];
                $scope.storedData = response.data;
            });
    };

    $scope.refreshList = function(){ // This function reloads data from specified location in to storedData array
        //$http.get('../JSON/notifications.json')
        //$http.get('http://35.204.202.104:8080/api/v1.0/notf/' ,{
         //  params: {token:$cookies.get("Token")}})
        $http.get('../JSON/'+$scope.$ctrl.from+'.json')
            .then(function(response){
                elements = [];
                $scope.storedData = [];
                $scope.storedData = response.data;
            });
    };



    //  Initialize

    $scope.listType;
    var elements = []; // Array of visual elements that represent storedData
    $scope.storedData = []; // Array of data stored on the list, can by groups or notifications

    //$scope.refreshList();

/*    revreshInterval = $interval(function() {
        $scope.refreshList
    }, 60000);*/

});

app.controller("userPanelController" , function($interval,$http,$cookies,$scope) {

    //  Functions

    $scope.logout = function () { // This function is logging out user
        $cookies.remove("Token");   //  Removing token from cookies
        app.setView(app.viewEnum.LOGIN);    //  Changing view mode to Login mode
    };

    app.showPopup = function(element,elementFunction){  //  Experimental function

        $scope.context = element;
        $scope.confFunction = elementFunction;
        $scope.showPopup = true;

    };

    app.hidePopup = function(){     //  Experimental function
        $scope.showPopup = false;
    };

    //  Initialize

    $scope.showPopup = false;
    $scope.listType = "groups"; //  For testing purposes the first list is of 'groups' type by default

});

app.controller("notificationController" , function ($scope) {


    //  Functions

    this.loseFocus = function () {  // This function removes focus from this elements
        $scope.notificationFocus="notification-div";
        this.focused = false;
    };

    this.getFocus = function () {   // This function set focus on this elements
        $scope.notificationFocus=$scope.notificationFocus+" div-selected";
        this.focused = true;
    };


    //  Initialize

    this.focused = false;   // By default the element don't have focus
    $scope.notificationFocus="notification-div";

});

app.controller("notificationGroupController" , function ($scope) {


    //  Functions

    this.loseFocus = function () {  // This function removes focus from this elements
        $scope.notificationFocus="notificationGroup-div";
        this.focused = false;
    };

    this.getFocus = function () {   // This function set focus on this elements
        $scope.notificationFocus=$scope.notificationFocus+" div-selected";
        this.focused = true;
    };


    //  Initialize

    this.focused = false;   // By default the element don't have focus
    $scope.notificationFocus="notificationGroup-div";

});

app.controller("registerController", function ($http,$scope) {


    //  Functions

    $scope.inputReset = function () {   //  This functions resets input style
        $scope.info = "";
        $scope.loginInputStyle = "login-input login-text";
    };

    $scope.signUp = function () {       // This function is igining up ne user

        const hashPass = $scope.password; // Encrypted password in the future
        const hashRePass = $scope.rePassword; // Encrypted rePassword in the future

        if(hashRePass===hashPass){  // Are two inserted passwords the same
            $http.post('http://35.204.202.104:8080/api/v1.0/register/',{
                params: { username:$scope.username,password:hashPass }})
                .then(
                    //Success
                    function(response){
                        this.Token = response.data;
                        $cookies.put("Token",Token);
                        app.setView(app.viewEnum.LIST);
                    },
                    //Failure
                    function (response) {
                        $scope.info = response.data;
                        $scope.loginInputStyle = $scope.loginInputStyle + " wrong";
                    }
                );

        } else{
            $scope.info = "password and repeat password are not the same";
            $scope.loginInputStyle = $scope.loginInputStyle + " wrong";
        }


    };

    $scope.backToLogin = function(){    // This function sets view mode to Login
        app.setView(app.viewEnum.LOGIN);
    };


    //  Initialize

    $scope.loginInputStyle = "login-input login-text"; // Setting default inputs sytle
});

app.controller("popupController", function () {

});