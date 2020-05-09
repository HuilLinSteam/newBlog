// DOM 加载完再执行
$(function() {
	$(".followeeContent").on("click",".followUserBtn",function(){
		// 1为关注 0为未关注
		var toFollowerUserStatus = $(this).attr("toFollowStatus");
		var toFollowerEntityId = $(this).attr("entityId");
		var thisObject = $(this);
		if(isLogin && toFollowerUserStatus != null){
			if(toFollowerEntityId == userId){
				toastr.stop("干嘛自己关注自己！");
			}
			// 已关注则要执行取消关注
			if(toFollowerUserStatus == 1){
				MyBlog.ajax("/unFollow","entityType=3&entityId="+toFollowerEntityId,"POST",function(resVO){
					if(resVO.code == 1){
						toastr.info("取消关注");
						thisObject.removeClass("btn-warning");
						thisObject.addClass("btn-info");
						thisObject.attr("toFollowStatus",0);
						thisObject.html("关注TA")
					}else{
						toastr.error("服务器繁忙");
					}
				});
			}else{
				MyBlog.ajax("/follow","entityType=3&entityId="+toFollowerEntityId,"POST",function(resVO){
					if(resVO.code == 1){
						toastr.success("已关注");
						thisObject.removeClass("btn-info");
						thisObject.addClass("btn-warning");
						thisObject.attr("toFollowStatus",1);
						thisObject.html("已关注")
					}else{
						toastr.error("服务器繁忙");
					}
				});
			}
		}else{
			toastr.info("要登录后才能点关注哦！");
		}
	});
	// $(".followUserBtn").click(function() {
		
	// });
});