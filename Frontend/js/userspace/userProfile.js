// DOM 加载完再执行
$(function() {
	$(".followUserBtn").click(function() {
		var followerUserId = triggerFllowerUserId;
		if(isLogin && followerUserId!=-1 && toUserFollowStatus != -1){
			// 已关注则要执行取消关注
			if(toUserFollowStatus == 1){
				MyBlog.ajax("/unFollow","entityType=3&entityId="+followerUserId,"POST",function(resVO){
					if(resVO.code == 1){
						window.location.reload();
					}else{
						toastr.error("服务器太繁忙哦！");
					}
				});
			}else{
				MyBlog.ajax("/follow","entityType=3&entityId="+followerUserId,"POST",function(resVO){
					if(resVO.code == 1){
						window.location.reload();
					}else{
						toastr.error("服务器太繁忙哦！");
					}
				});
			}
			
		}else{
			toastr.error("要登录后才能关注哦！");
		}
	});
	
	
	
});
