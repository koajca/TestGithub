<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<div data-role="popup" id="contactPopup" data-theme="a">
    <div data-role="content" >
        <h3>Tìm kiếm trong danh bạ</h3>
        <input type="search" name="searchField" id="searchField" data-inline="true" placeholder="Ho va ten">
        <a href="javascript:void(0);" data-role="button" data-inline="true" onclick="doDialogSearchContact();">Search</a>
        <a href="javascript:void(0);" data-role="button" data-inline="true" data-rel='back'>Close</a>

        <input type="hidden" name="txtContact" value="" id="txtContact" />
        <ul id="listcontact" data-role="listview" data-inset="true"></ul>

        <script>
            var sugList = $("#listcontact");
            
            doDialogSearchContact = function() {
                alert(1);
                var inputtype = BANKPLUSWAP.UTIL.getInputType();
                var text = $("#searchField").val();
                alert(text);
                if(text.length < 1) {
                    sugList.html("");
                    sugList.listview("refresh");
                } else {
                    $.get("Contact!doSearch.do", {search:text}, function(res,code) {
                        var str = "";
                        var items = res.items;
                        for(var i=0, len=items.length; i<len; i++) {
                            var ii = items[i].msisdn;
                            if(inputtype == 1){
                                ii = items[i].account;
                            }else if(inputtype == 2){
                                ii = items[i].contactName + ';' + items[i].account;
                            }
                            str += "<li data-icon='false'><a data-rel='back' href='javascript:void(0);' onclick=\"contactItemClick('" + ii + "')\" >"+items[i].contactName+ " - " + items[i].msisdn + " - " + items[i].account + "</a></li>";
                        }
                        sugList.html(str);
                        sugList.listview("refresh");
                        console.dir(res);
                    },"json");
                }
            }
            //    $("#contentmainpage").on("pageshow", function(e) {
            //    });
            function contactItemClick(val){
                var nameinput = BANKPLUSWAP.UTIL.getInputName();
                var inputtype = BANKPLUSWAP.UTIL.getInputType();
                if(inputtype != 2){
                    $("#txtContact").val(val);
                    $("#"+nameinput).val(val);
                    document.getElementById(nameinput).value = val;
                }else{
                    var name = nameinput.split(',');
                    var valArr = val.split(';');
                    for(var i=0, len=name.length; i<len;i++){
                        document.getElementById(name[i]).value = valArr[i];
                    }
                }
            }
        </script>
    </div>
</div>


