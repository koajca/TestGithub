token = {
    getTokenParam:function(){
        var myParam = new Object();

        var tokenDiv;
        var tokenrand = Math.floor(Math.random() * 1100);
        var param = 'rand=' + tokenrand+'&pIntercep=1';
        $.ajax({
            url: 'token!reloadToken.do',
            type: 'post',
            cache: false,
            data: param,
            async: false,
            success: function(data) {
                tokenDiv = data;
            } 
        });
        var tempDiv = $("<div/>");
        tempDiv.append(tokenDiv);
        var myValue = tempDiv.find("input");

        myParam[myValue[0].name] = myValue[0].value;
        myParam[myValue[1].name] = myValue[1].value;
        return myParam;
    }
}