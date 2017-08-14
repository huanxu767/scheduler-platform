var selectMenu = $("#activeMenu").val();
$("#supMenu a").each(function(){
    if(selectMenu == $(this).attr("data")){
        $(this).removeClass("def-color").addClass("menu_active");
        $(this).parent().parent().prev().removeClass("text").removeClass("def-color").addClass("ctext").addClass("menu_active");
        $(this).parent().parent().show();
    }
});

function itemToggle(dom){
    var $this = $(dom);
    if($this.hasClass('text')){
        $this.removeClass('text');
        $this.addClass('ctext');
        $this.siblings().slideDown(400);
    }else{
        $this.removeClass('ctext');
        $this.addClass('text');
        $this.siblings().slideUp(400);
    }
}
function showContent(obj){
    var contentFrame = self.parent.frames["content"];
    contentFrame.location.href =  $(obj).attr("data");
}