
var getToken = function(){
    chrome.storage.local.get('token', function(result){
        let Token = result.token;
        //alert(Token);
        if(Token != null){}
    });
};

setInterval( getToken, 12000 );