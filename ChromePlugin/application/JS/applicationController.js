//  Stand by

//  Const
    const ServerAddress = "http://35.204.202.104:8080/api/v1.0";
    const ServerAddress2 = "http://35.204.202.104:8084/api/v1.0";


//  Module
const app = angular.module('clientApplication',['ngCookies']);


//  Components

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
        from: '=',   // url added after API address, under with data about list elements are stored
        myGroup: '='       // data about notification group
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

app.component("externalServices" ,{
    templateUrl : '../HTML/externalServices.html',
    controller : 'externalServicesController'
});

app.component("externalServicesList" ,{
    templateUrl : '../HTML/externalServicesList.html',
    controller : 'externalServicesListController'
});

app.component("externalServiceElement" ,{
    templateUrl : '../HTML/externalServiceElement.html',
    controller : 'externalServiceElementController',
    bindings: {
        removeMy: '=',  // callback function for removing this element
        myWebsite: '=',    // data about notification represented by this element
        requestFocus: '=', // callback function for requesting focus
        addSelf: '=' // callback function for adding this element to elements table
    }
});

app.component("twitterWindow" ,{
    templateUrl : '../HTML/TwitterWindow.html',
    controller : 'twitterServiceController'
});

app.component("websiteWindow" ,{
    templateUrl : '../HTML/WebsiteWindow.html',
    controller : 'websiteServiceController'
});

app.component("serviceWindow" ,{
    templateUrl : '../HTML/ServiceWindow.html',
    controller : 'customServiceController'
});

app.component("myAccount" ,{
    templateUrl : '../HTML/myAccountWindow.html',
    controller : 'myAccountController'
});

// Values

// Define in mainController
app.value("Token");     // Token that allows application to connect with RestAPI

app.value("Offset");     // Token that allows application to connect with RestAPI

app.value("viewEnum");  // enumeration of possible view options

app.value("userViewEnum");  // enumeration of possible user view options

app.value("setView");   // function that allows to change vie mode

app.value("setApplicationStyle");    // function that allows to set style of application (light - dark)

app.value("reqid");    // tmp


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

    app.userViewEnum = {
        LIST : 1,
        SERVICES : 2
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

        //app.setView(app.viewEnum.LIST);

        $http.post(ServerAddress+'/login/',{
            login:$scope.username,
            password:$scope.password
        }).then(
                //Success
                function(response){
                    app.Token = response.data.status;
                    chrome.storage.local.set({'token': app.Token });

                    $cookies.put("Token", app.Token);
                    app.setView(app.viewEnum.LIST);
                },
                //Failure
                function (response) {
                    $scope.loginInputStyle = $scope.loginInputStyle + " wrong";
                    $scope.info = response.data;
                }
                );
    };


    //  Initialize

    $scope.loginInputStyle = "login-input login-text";
    if(app.Token === "reg"){
        $scope.info = "registered successful";
    }

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

        $scope.$ctrl.myGroup.$ctrl.countDecrease();

        const myID = element.myNotification.notificationID;
        $http.post(ServerAddress+'/notf/remove/',{
            notfid:myID.toString(),
            token:app.Token
        }).then(
            //Success
            function(response){
                $scope.storedData.splice($scope.storedData.indexOf(element.myNotification), 1);
                elements.splice(elements.indexOf(element), 1);
            },
            //Failure
            function (response){
                $scope.info = response.data;
            }
        );
    };
    
    "../JSON/groups.json" //
    $scope.loadData = function(from,type){ // This function loads data from specified location in to storedData array
        $scope.type = type;
        $http.post(ServerAddress+'/notifications/source/',{
            offset:app.Offset,
            token:app.Token,
            source:from
        })
            .then(function(response){
                let newContent = response.data.notifications;
                let offset = newContent.length;
                let oldContent = $scope.storedData;

                $scope.storedData = [].concat(oldContent, newContent);
                app.Offset = app.Offset + offset;
            });

    };

    $scope.loadGroups = function(rom,type){


        $http.post(ServerAddress+'/notifications/',{
            token:app.Token
        }).then(
            //Success
            function(response){
                let newContent = response.data.sources;
                let oldContent = $scope.storedData;

                $scope.storedGroups = [].concat(oldContent, newContent);
            },
            //Failure
            function (response) {
                $scope.infoType = "error-info";
                $scope.info = "Failure to load services";
            }
        );

    };

    //  Initialize
    app.Offset = 0;
    let elements = [];
    $scope.storedData = [];
    $scope.storedGroups = [];
});

app.controller("userPanelController" , function($interval,$http,$cookies,$scope) {

    //  Functions

    $scope.moveToAddExtension = function (){
        $cookies.put("serviceState",0);
        $scope.curUserPanel = 1;
        $cookies.put("UserState",1);
    };

    $scope.moveToExtensionsList = function (){
        $scope.curUserPanel = 2;
        $cookies.put("UserState",2);
    };

    $scope.moveToList = function () {
        $scope.curUserPanel = 0;
        $cookies.put("UserState",0);
        $cookies.remove("serviceState");
    };

    $scope.moveToAccount = function(){
        $scope.curUserPanel = 3;
        $cookies.put("UserState",3);
    };

    $scope.logout = function () { // This function is logging out user
        chrome.storage.local.set({'token': null });
        $cookies.remove("Token");   //  Removing token from cookies
        $cookies.remove("Offset");   //  Removing token from cookies
        $cookies.remove("UserState");   //  Removing user stage from cookies
        $cookies.remove("serviceState"); //  Removing service stage from cookies
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

    let UserState = $cookies.get("UserState");
    if ( UserState==null ){
        $cookies.put("UserState",0);
        $scope.curUserPanel = 0;
    }
    else{
        $scope.curUserPanel = UserState;
    }

    $scope.showPopup = false;
    $scope.listType = "groups"; //  For testing purposes the first list is of 'groups' type by default

});

app.controller("notificationController" , function ($http,$scope) {

    //  Functions

    this.loseFocus = function () {  // This function removes focus from this elements
        $scope.notificationFocus="notification-div";
        this.focused = false;
    };

    this.getFocus = function () {   // This function set focus on this elements

        $scope.notificationFocus+=" div-selected";
        this.focused = true;


        const myID = this.myNotification.notificationID;
        //$scope.info = myID;
        $http.post(ServerAddress+'/setFlag/',{
            notfid:myID.toString(),
            token:app.Token
        }).then(
            //Success
            function(response){
               // $scope.info = response.data;
            },
            //Failure
            function (response){
               // $scope.info = response.data;
            }
        );

        if(this.myNotification.flag === false){
            this.myNotification.flag = true;
        }
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

    this.countDecrease = function(){
        $scope.myCount = $scope.myCount-1;
    };

    $scope.loadCount = function (count) {
        $scope.myCount = count;
    };

    //  Initialize

    this.focused = false;   // By default the element don't have focus
    $scope.notificationFocus="notificationGroup-div";

});

app.controller("registerController", function ($http,$scope,$window) {


    //  Functions

    $scope.inputReset = function () {   //  This functions resets input style
        $scope.info = "";
        $scope.loginInputStyle = "login-input login-text";
    };

    $scope.signUp = function () {       // This function is igining up ne user

        const hashPass = $scope.password; // Encrypted password in the future
        const hashRePass = $scope.rePassword; // Encrypted rePassword in the future

        if(hashRePass===hashPass){  // Are two inserted passwords the same
            $http.post(ServerAddress+'/register/',{
                name:$scope.username,
                surname:$scope.surname,
                login:$scope.login,
                password:hashPass
            }).then(
                    //Success
                    function(response){
                        app.Token = "reg";
                        $scope.info = "Successful created account";
                        app.setView(app.viewEnum.LOGIN);
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

app.controller("popupController", function ($scope) {
});

app.controller("externalServicesController", function ($http,$scope,$cookies) {


    //  Functions

    $scope.ToTwitter = function () { // Function setting twitter window as current
        let url = " [empty] ";

        $http.post(ServerAddress+'/new/twitter/request/',{
           token:app.Token
        }).then(
            function (response) {
                url = response.data.status;

                $cookies.put("serviceState",1);
                $scope.currentState = 1;
                window.open(url);

                alert("Success: " + response.status);
            },
            function (response) {
                url = response;
                alert("Fail:  " + url.status);
                url = response.data;
            }
        );

    };

    $scope.ToPWR= function () { // Function setting pwr side window as current

        $cookies.put("serviceState",2);
        $scope.currentState = 2;
    };
    
    $scope.ToServices = function () {

        $cookies.put("serviceState",3);
        $scope.currentState = 3;
    };
    
    $scope.backToList = function () { // Function setting list of services window as current
        $cookies.remove("serviceToken");
        $cookies.put("serviceState",0);
        $scope.currentState = 0;
    };


    //  Initialize

    let serviceState = $cookies.get("serviceState");
    if ( serviceState == null ){
        $cookies.put("serviceState",0);
        $scope.currentState = 0;
    }
    else{
        $scope.currentState = serviceState;
    }

});

app.controller("twitterServiceController", function ($scope,$http) {


    //  Functions

    $scope.TwitMe = function () {       //  Sending twitter tokens to data base

        $http.post(ServerAddress+'/new/twitter/confirm/',{
            token:app.Token,
            pin:$scope.pinKey,
        }).then(
            //Success
            function(response){
                $scope.infoType = "";
                $scope.info = "Twitter successfully added"
                $scope.pinKey = "";
            },
            //Failure
            function (response) {
                $scope.infoType = "error-info";
                $scope.info = "Failure while adding twitter account";
            }
        );

    };


    //  Initialize

    $scope.loginInputStyle = "login-input login-text";

});

app.controller("websiteServiceController", function ($scope,$http) {


    //  Functions

    $scope.WebMe = function () {       //  Sending twitter tokens to data base

        $http.post(ServerAddress+'/new/website/',{
            token:app.Token,
            website:$scope.websiteURL
        }).then(
            //Success
            function(response){
                $scope.infoType = "";
                $scope.info = "Website successfully added";
                $scope.websiteURL = "";
                refreshList();
            },
            //Failure
            function (response) {
                $scope.infoType = "error-info";
                $scope.info = "Failure while adding website";
            }
        );

    };

    //  Initialize

    $scope.loginInputStyle = "login-input login-text";

});

app.controller("customServiceController", function ($scope,$http,$cookies) {

    //  Functions
    $scope.GenerateToken = function () {

        $http.post(ServerAddress2+'/customservice/register/',{
            token:app.Token
        }).then(
            //Success
            function(response){
                $scope.added = true;
                $scope.info = "Added new custom service";
                $scope.GenToken = "Token: "+response.data.token;
                $cookies.put("serviceToken",response.data.token);
            },
            //Failure
            function (response) {
                $scope.info = "Failure while adding service";
                $scope.infoType = "error-info";
            }
        );

    };

    $scope.moveToInstruction = function () {

        window.open('http://35.204.202.104/CustomService.pdf');

    };

    //  Initialize

    if($cookies.get("serviceToken") == null) {
        $scope.added = false;
    }
    else {
        $scope.added = true;
        $scope.info = "Added new custom service";
        $scope.GenToken = "Token: "+$cookies.get("serviceToken");
    }

    $scope.loginInputStyle = "login-input login-text";

});

app.controller("externalServicesListController", function ($scope,$http) {

    //  Functions

    var refreshList = function () {
        $scope.webservices = null;

        $http.post(ServerAddress+'/accounts/',{
            token:app.Token
        }).then(
            //Success
            function(response){
                //$scope.info = response.data;
                $scope.webservices = response.data.accounts
            },
            //Failure
            function (response) {
                $scope.infoType = "error-info";
                $scope.info = "Failure to load services";
            }
        );
    };

    $scope.removeWebsite = function (sourceID, accessToken) {

        $http.post(ServerAddress+'/remove/account/',{
            token:app.Token,
            source:sourceID,
            accesstoken:accessToken
        }).then(
            //Success
            function(response){
                refreshList();
            },
            //Failure
            function (response) {
                $scope.infoType = "error-info";
                $scope.info = "Failure to remove website";
            }
        );
    };

    $scope.addThis = function (element) { // This function is adding elements that represents list data to elements list
        elements.push(element);
    };

    $scope.elementRequestFocus = function(element){ // This function is called when one of the elements is requesting focus
        if (element.focused===false) {

            angular.forEach(elements, function (value) {
                    value.loseFocus();
                });

            element.getFocus();
        }else {                     //  If element is already focused, it loses the focus
            element.loseFocus();
        }
    };

    //  Initialize
    let elements = [];
    $scope.webservices = [];
    refreshList();

});

app.controller("externalServiceElementController" , function ($timeout,$http,$scope) {

    let agrBy;
    let subString;
    let agrDurationType;
    let agrType;

    //  Functions


    this.loseFocus = function () {  // This function removes focus from this elements
        $scope.info ="";
        $scope.notificationFocus="notification-div";
        this.focused = false;
    };

    this.getFocus = function () {   // This function set focus on this elements

        $scope.notificationFocus+=" div-selected";
        this.focused = true;
    };

    $scope.setAggregation = function (type) {

        agrType = type;
        switch (type) {
            case 0:
            default:
                $scope.curAggregaton= "None";
                break;
            case 1:
                $scope.curAggregaton= "First";
                break;
            case 2:
                $scope.curAggregaton= "Last";
                break;
            case 3:
                $scope.curAggregaton= "Count";
                break;
        }

        $scope.dropmenu_class = "dropdown-content-hide";
        $timeout(function () {
            $scope.dropmenu_class = "dropdown-content";
        },100);


    };

    $scope.setAggregationDuration = function(type){

        agrDurationType = type;
        switch (type) {
            case 0:
            default:
                $scope.durType= "Hoers";
                break;
            case 1:
                $scope.durType= "Days";
                break;
            case 2:
                $scope.durType= "Weeks";
                break;
            case 3:
                $scope.durType= "Months";
                break;
            case 4:
                $scope.durType= "Years";
                break;
        }

        $scope.dropmenu_class = "dropdown-content-hide";
        $timeout(function () {
            $scope.dropmenu_class = "dropdown-content";
        },100);
    };

    $scope.serviceInitSUB = function () {
        $scope.aggSubStr = "sub";
    };

    $scope.setAggregationBy = function (type) {

        agrBy = type;
        switch (type) {
            case 1:
            default:
                $scope.curAggregatonBy= "Topic";
                break;
            case 2:
                $scope.curAggregatonBy= "Message";
                break;
        }

        $scope.dropmenu_class = "dropdown-content-hide";
        $timeout(function () {
            $scope.dropmenu_class = "dropdown-content";
        },100);
    };

    $scope.serviceInitTYP = function(duration) {

        if(duration<24){
            agrDurationType = 0;
            $scope.aggrDuration = duration;
        }else if(duration<24*7){
            agrDurationType=1;
            $scope.aggrDuration = duration/24;
        }else if(duration<24*30){
            agrDurationType=2;
            $scope.aggrDuration = duration/(24*7);
        }else if(duration<24*12*30){
            agrDurationType=3;
            $scope.aggrDuration = duration/(24*30);
        }else {
            agrDurationType=4;
            $scope.aggrDuration = duration/(24*12*30);
        }

        switch (agrDurationType) {
            case 0:
            default:
                $scope.durType= "Hoers";
                break;
            case 1:
                $scope.durType= "Days";
                break;
            case 2:
                $scope.durType= "Weeks";
                break;
            case 3:
                $scope.durType= "Months";
                break;
            case 4:
                $scope.durType= "Years";
                break;
        }

    };

    $scope.serviceInitAGR = function(aggregation){
        agrType = aggregation;

        switch (aggregation) {
            case 0:
            default:
                $scope.curAggregaton= "None";
                break;
            case 1:
                $scope.curAggregaton= "First";
                break;
            case 2:
                $scope.curAggregaton= "Last";
                break;
            case 3:
                $scope.curAggregaton= "Count";
                break;
        }
    };

    $scope.confirmAggregation = function(source){

        if($scope.showTimeAggr){
            aggrDur = $scope.aggrDuration; // duration
        }else {
            aggrDur=0;
        }

        if($scope.showSubAggr){
            subString = $scope.aggSubStr; // substring
        }else{
            subString="null";
        }


        //$scope.info = "source:"+source + " type:"+agrType+" duration:"+aggrDur+" durationType:"+agrDurationType+" by:"+agrBy+" substring "+subString;

        $http.post(ServerAddress+'/account/aggregation/',{
            token: app.Token,
            account: source,
            aggregation: agrType,
            aggregationdate: aggrDur,
            aggregationtype: agrDurationType,
            aggregationby:agrBy,
            aggregationkey:subString
        }).then(
            //Success
            function(response){
                $scope.info = "Aggregation set successfully: ";
                },
            //Failure
            function (response) {
                $scope.infoType = "error-info";
                $scope.info = "Failure to set aggregation: ";
            }
        );
    };

    $scope.moveToSide = function(url){
        window.open(url);
    };

    //  Initialize

    agrDurationType=0;
    subString = null;
    agrBy=1;
    $scope.curAggregatonBy = "Topic";
    $scope.dropmenu_class = "dropdown-content";

    this.focused = false;   // By default the element don't have focus
    $scope.notificationFocus="notification-div";

});

app.controller("myAccountController" , function ($http,$scope) {

    $scope.changePassword = function(){

        if($scope.newpass!=$scope.newpassrep){
            $scope.info = "password and repeat password are not the same";
            $scope.loginInputStyle = $scope.loginInputStyle + " wrong";
            return;
        }

        $http.post(ServerAddress+'/user/password/',{
            token:app.Token,
            oldpassword:$scope.oldpass,
            newpassword:$scope.newpass
        }).then(
            //Success
            function(response){

                $scope.oldpass = "";
                $scope.newpass= "";
                $scope.newpassrep= "";
                $scope.cpBox = false;
                $scope.loginInputStyle = "login-input login-text";

                $scope.info = "Password changed";
            },
            //Failure
            function (response) {
                $scope.infoType = "error_info";
                $scope.info = "Failure to remove website";
            }
        );

    };

    $scope.resetInput = function(){

        $scope.oldpass = "";
        $scope.newpass= "";
        $scope.newpassrep= "";
        $scope.infoType = "";
        $scope.info = "";
        $scope.loginInputStyle = "login-input login-text";

    };

    $scope.changeColor = function(){
        chrome.storage.local.set({'color': $scope.color });
    };

    $scope.removeUser = function () {

        $http.post(ServerAddress+'/delete/user/',{
            token: app.Token
        }).then(
            //Success
            function(response){
                chrome.storage.local.set({'token': null });
                $cookies.remove("Token");   //  Removing token from cookies
                $cookies.remove("Offset");   //  Removing token from cookies
                $cookies.remove("UserState");   //  Removing user stage from cookies
                $cookies.remove("serviceState"); //  Removing service stage from cookies
                app.setView(app.viewEnum.LOGIN);    //  Changing view mode to Login mode
            },
            //Failure
            function (response) {
                $scope.infoType = "error-info";
                $scope.info = "Failure to remove account ";
            }
        );

    };

    chrome.storage.local.get('color', function(result){
        let Color = result.color;
        $scope.color = Color;
    });
    $scope.loginInputStyle = "login-input login-text";

});
