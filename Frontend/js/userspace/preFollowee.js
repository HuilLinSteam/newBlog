var seeFolloweeUserId = MyBlog.getParameter("userId");
var nowFolloweePageNumber = 1;
$(".userFollower").attr("href",userOfFllowersUrl+seeFolloweeUserId);
$(".userFollowee").attr("href",userOfFlloweesUrl+seeFolloweeUserId);
$(".psersonDetail").attr("href",allUserProfile+seeFolloweeUserId);
// $(".userOfFollow")
function getFollowee(pageNumber){
	MyBlog.ajax("/followees/"+seeFolloweeUserId,"current="+pageNumber,"GET",function(res){
		if(res.code == 1){
			var getSeeUser = res.data.user;
			$(".userOfFollow").html(getSeeUser.username);
			$(".followeeBox").empty();
			var getFolloweeUsers = res.data.users;
			for(var i = 0;i<getFolloweeUsers.length;i++){
				console.log(getFolloweeUsers[i]);
				var li =
				'<li class="media pb-3 pt-3 mb-3 border-bottom position-relative">'+
					'<a href="'+allUserProfile+getFolloweeUsers[i].user.id+'">'+
						'<img src="'+getFolloweeUsers[i].user.headerUrl+'" class="mr-4 rounded-circle user-header blog-avatar-50"  alt="用户头像">'+
					'</a>'+
					'<div class="media-body">'+
						'<h6 class="mt-0 mb-3">'+
							'<span class="text-success">'+getFolloweeUsers[i].user.username+'</span>'+
							'<span class="float-right text-muted font-size-12">关注于 <i>'+formatDate(getFolloweeUsers[i].followTime)+'</i></span>'+
						'</h6>'+
						'<div>'+
							'<button type="button" entityId="'+getFolloweeUsers[i].user.id+'" class="btn btn-info btn-sm float-right follow-btn followUserBtn followUserBtn'+i+'">关注TA</button>'+
						'</div>'+
					'</div>'+
				'</li>'+
				'<hr>';
				$(".followeeBox").append(li);
			}
			
			if(isLogin){
				var getAboutUsers = res.data.user;
				if(getAboutUsers.id == userId){
					$(".userOfFollow").html("我");
					$(".userOfFollow").removeClass("text-info");
				}
				for(var j = 0;j<getFolloweeUsers.length;j++){
					var seeFolloweeUser = getFolloweeUsers[j].user.id;
					// 如果查看的是本人，则隐藏起关注按钮
					if(seeFolloweeUser == userId){
						$(".followUserBtn"+j).html("已关注了我")
						$(".followUserBtn"+j).removeClass("btn-info");
						$(".followUserBtn"+j).addClass("btn-danger");
						$(".followUserBtn"+j).attr("disabled",true);
					}else{
						// 查看当前用户没有对该用户关注
						if(getFolloweeUsers[j].hasFollowed){
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

function getFloweePageFunc(){
	MyBlog.ajax("/followees/"+seeFolloweeUserId,null,"GET",function(res){
		// 分页查询
		if(res.data.page.rows>0){
			var followeePage = res.data.page.total;
			$(".followeePage").append('<li class="page-item active"><a class="page-link">1</a></li>');
			for (var i = 1; i < followeePage; i++) {
				$(".followeePage").append('<li class="page-item"><a class="page-link">'+(i+1)+'</a></li>');
			}
		}else{
			$(".followeeBox").html("暂无关注对象哦！");
			$(".followeePage").html("");
		}
	});
}
getFloweePageFunc();

$(".followeePage").on('click','.page-link',function(){
	nowFolloweePageNumber = $(this).text();
	console.log(nowFolloweePageNumber);
	$(".followeeBox").html("");
	$(this).parent().siblings().removeClass("active");
	$(this).parent().addClass("active");
	getFollowee(nowFolloweePageNumber);
});
getFollowee(nowFolloweePageNumber);
