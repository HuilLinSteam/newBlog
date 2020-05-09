var seeUserId = MyBlog.getParameter("userId");
var triggerFllowerUserId = -1;
var toUserFollowStatus = -1;
// console.log(userId);
MyBlog.ajax("/user/profile/"+seeUserId,null,"GET",function(resVO){
	if(resVO.code == 1){
		var tempSeeUserInfo = resVO.data;
		$(".seeUserImg").attr("src",tempSeeUserInfo.user.headerUrl);
		$(".seeUserName").html(tempSeeUserInfo.user.username);
		// 如果登录,则判断有没有关注或者是不是本人
		if(isLogin){
			// 如果查看的是本人，则隐藏起关注按钮
			if(seeUserId == userId){
				$(".followStatus").addClass("hidden");
			}else{
				// 查看当前用户没有对该用户关注
				if(tempSeeUserInfo.hasFollowed){
					$(".followStatus").removeClass("btn-info");
					$(".followStatus").addClass("btn-warning");
					toUserFollowStatus = 1;
					$(".followStatus").html("已关注");
				}else{
					$(".followStatus").removeClass("btn-warning");
					$(".followStatus").addClass("btn-info");
					toUserFollowStatus = 0;
					$(".followStatus").html("关注TA");
				}
			}
			triggerFllowerUserId = seeUserId;
		}else{
			triggerFllowerUserId = -1;
			$(".followStatus").html("关注TA");
		}
		
		$(".registerTime").html(formatDate(tempSeeUserInfo.user.createTime));
		$(".fllowerCount").html(tempSeeUserInfo.followerCount);
		// 粉丝的数量大于0
		if(tempSeeUserInfo.followerCount > 0){
			$(".fllowerCount").attr("href",userOfFllowersUrl+seeUserId);
		}else{
			$(".fllowerCount").attr("href","#");
		}
		
		$(".flloweeCount").html(tempSeeUserInfo.followeeCount);
		// 关注用户的数量大于0
		if(tempSeeUserInfo.followeeCount > 0){
			$(".flloweeCount").attr("href",userOfFlloweesUrl+seeUserId);
		}else{
			$(".flloweeCount").attr("href","#");
		}
		$(".getLikeCount").html(tempSeeUserInfo.likeCount);
	}else{
		toastr.error("服务器繁忙哦！");
	}
});