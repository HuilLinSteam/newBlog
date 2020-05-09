var seeFollowerUserId = MyBlog.getParameter("userId");
var nowFollowerPageNumber = 1;
$(".userFollower").attr("href",userOfFllowersUrl+seeFollowerUserId);
$(".userFollowee").attr("href",userOfFlloweesUrl+seeFollowerUserId);
$(".psersonDetail").attr("href",allUserProfile+seeFollowerUserId);
function getFollower(pageNumber){
	MyBlog.ajax("/followers/"+seeFollowerUserId,"current="+pageNumber,"GET",function(res){
		if(res.code == 1){
			var getSeeUser = res.data.user;
			$(".userOfFollower").html(getSeeUser.username);
			$(".followerBox").empty();
			var getFollowerUsers = res.data.users;
			for(var i = 0;i<getFollowerUsers.length;i++){
				console.log(getFollowerUsers[i]);
				var li = 
				'<li class="media pb-3 pt-3 mb-3 border-bottom position-relative">'+
					'<a href="'+allUserProfile+getFollowerUsers[i].user.id+'">'+
						'<img src="'+getFollowerUsers[i].user.headerUrl+'" class="mr-4 rounded-circle user-header" alt="用户头像">'+
					'</a>'+
					'<div class="media-body">'+
						'<h6 class="mt-0 mb-3">'+
							'<span class="text-success">'+getFollowerUsers[i].user.username+'</span>'+
							'<span class="float-right text-muted font-size-12">关注于 <i>'+formatDate(getFollowerUsers[i].followTime)+'</i></span>'+
						'</h6>'+
						'<div>'+
							'<button type="button" entityId="'+getFollowerUsers[i].user.id+'" class="btn btn-info btn-sm float-right follow-btn followUserBtn followUserBtn'+i+'">关注TA</button>'+
						'</div>'+
					'</div>'+
				'</li>'+
				'<hr/>';
				$(".followerBox").append(li);
			}
			
			if(isLogin){
				var getAboutUsers = res.data.user;
				if(getAboutUsers.id == userId){
					$(".userOfFollower").html("我");
					$(".userOfFollower").removeClass("text-info");
				}
				for(var j = 0;j<getFollowerUsers.length;j++){
					var seeFolloweeUser = getFollowerUsers[j].user.id;
					// 如果查看的是本人，则隐藏起关注按钮
					if(seeFolloweeUser == userId){
						$(".followUserBtn"+j).html("已关注了我")
						$(".followUserBtn"+j).removeClass("btn-info");
						$(".followUserBtn"+j).addClass("btn-danger");
						$(".followUserBtn"+j).attr("disabled",true);
					}else{
						// 查看当前用户没有对该用户关注
						if(getFollowerUsers[j].hasFollowed){
							$(".followUserBtn"+j).removeClass("btn-info");
							$(".followUserBtn"+j).addClass("btn-warning");
							$(".followUserBtn"+j).attr("toFollowStatus",1);
							// toFollowStatus = 1;
							$(".followUserBtn"+j).html("已关注");
						}else{
							$(".followUserBtn"+j).removeClass("btn-warning");
							$(".followUserBtn"+j).addClass("btn-info");
							$(".followUserBtn"+j).attr("toFollowStatus",0);
							// toFollowStatus = 0;
							$(".followUserBtn"+j).html("关注TA");
						}
					}
				}
				
				
			}else{
				$(".followUserBtn").html("关注TA");
			}
			
			
		}else{
			toastr.error("服务器繁忙哦！")
		}
	});
}
function getFlowerPageFunc(){
	MyBlog.ajax("/followers/"+seeFollowerUserId,null,"GET",function(res){
		// 分页查询
		if(res.data.page.rows>0){
			var followeePage = res.data.page.total;
			$(".followerPage").append('<li class="page-item active"><a class="page-link">1</a></li>');
			for (var i = 1; i < followeePage; i++) {
				$(".followerPage").append('<li class="page-item"><a class="page-link">'+(i+1)+'</a></li>');
			}
		}else{
			$(".followerBox").html("暂无关注对象哦！");
			$(".followerPage").html("");
		}
	});
}
getFlowerPageFunc();
$(".followerPage").on('click','.page-link',function(){
	nowFollowerPageNumber = $(this).text();
	console.log(nowFollowerPageNumber);
	$(".followerBox").html("");
	$(this).parent().siblings().removeClass("active");
	$(this).parent().addClass("active");
	getFollower(nowFollowerPageNumber);
});
getFollower(nowFollowerPageNumber);