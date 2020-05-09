var nowModel = 0;
var userId = null;

// 首页连接
var indexHtmlUrl = "http://www.codecloud.com:8848/blog/index.html";
// 查看谋篇博客
var allBlogHtmlUrl = "http://www.codecloud.com:8848/blog/userspace/blog.html?blogId=";
// 查看用户的简介
var allUserProfile = "http://www.codecloud.com:8848/blog/userspace/userProfile.html?userId=";
// 查看用户的关注对象
var userOfFlloweesUrl = "http://www.codecloud.com:8848/blog/userspace/followee.html?userId=";
// 	查看用户的粉丝
var userOfFllowersUrl = "http://www.codecloud.com:8848/blog/userspace/follower.html?userId=";
// 私信地址
var userLetterUrl = "http://www.codecloud.com:8848/blog/userspace/letter.html?userId=";
// 系统通知地址
var userNoticeUrl = "http://www.codecloud.com:8848/blog/userspace/notice.html?userId=";
// 系统通知查看详情
// type=like表示查看点赞详情
// type=comment表示查看评论详情
// type=follow表示查看关注详情
var systemNoticeTyeUrl = "http://www.codecloud.com:8848/blog/userspace/notice-detail.html?type=";
// 查看用户私信
var userLetterDetailUrl = "http://www.codecloud.com:8848/blog/userspace/letter-detail.html?conversationId=";
//编辑博客
var allUpdateBlogUrl = "http://www.codecloud.com:8848/blog/userspace/blogedit.html?blogId=";
// 返回用户私信列表
//用户是否登录
var isLogin = false;
// 当前登录用户的权限
var userType = null;
// 当前登录的用户名称
var loginUsername = null;
// 当前登录用户的头像
var loginUserHeaderUrl = null;
var loginUserOfname = null;
var loginUserEmail = null;
MyBlog.ajax("/user/myUserInfo", null, "GET", function(resVo) {
	if (resVo.code == 1) {
		var loginUserImg = '<img src="'+resVo.data.user.headerUrl+'" style="border-radius:50%;height:30px;width:30px">';
		$(".userName").html(loginUserImg)
		$(".loginArea").removeClass("hidden");
		$(".login").addClass("hidden");
		$(".register").addClass("hidden");
		if (resVo.data.allUnreadCount > 0) {
			var message = '<span class="badge badge-danger badge-pill" style="position:absolute;bottom:12px;left:22px" id="messageCount">' + resVo.data.allUnreadCount +
				'</span>';
			$("#message").append(message);
			$(".dropdown-menu").append('<div class="dropdown-item unreadCount" >未读消息  <span class="badge badge-danger badge-pill">' +
				resVo.data.allUnreadCount + '</span></div>')
		} 
		else if(resVo.data.allUnreadCount > 99){
			$(".dropdown-menu").append('<div class="dropdown-item unreadCount" >未读消息  <span class="badge badge-info badge-pill">99+</span></div>')
		}
		else {
			$(".dropdown-menu").append('<div class="dropdown-item unreadCount" >未读消息  <span class="badge badge-info badge-pill">' + resVo.data
				.allUnreadCount + '</span></div>')
		}
		userId = resVo.data.user.id;
		userType = resVo.data.user.type;
		loginUsername = resVo.data.user.username;
		loginUserHeaderUrl = resVo.data.user.headerUrl;
		loginUserOfname = resVo.data.user.name;
		loginUserEmail = resVo.data.user.email;
		isLogin = true;
		//$(".myUserProfile"),attr("href","http://www.codecloud.com:8848/blog/userspace/userProfile.html?userId="+resVo.data.user.id);
		// console.log(isLogin);
	} else {
		isLogin = false;
		userId = null;
		$(".loginArea").addClass("hidden");
		$(".login").removeClass("hidden");
		$(".register").removeClass("hidden");
	}
});

$(".logout").click(function() {
	MyBlog.ajax("/user/logout", null, "POST", function(resVo) {
		if (resVo.code == 1) {
			toastr.info("退出成功!");
			isLogin = false;
			window.location.reload();
		} else {
			toastr.error("服务器繁忙！");
		}
	})
})


function formatDate(d) {
	now = new Date(d);
	var year = now.getFullYear(); //取得4位数的年份
	var month = now.getMonth() + 1; //取得日期中的月份，其中0表示1月，11表示12月
	var date = now.getDate(); //返回日期月份中的天数（1到31）
	var hour = now.getHours(); //返回日期中的小时数（0到23）
	var minute = now.getMinutes(); //返回日期中的分钟数（0到59）
	var second = now.getSeconds(); //返回日期中的秒数（0到59）
	return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second;
}
$(".form-inline").on('click',".searchBtn",function(){
	var keyword = $(".inputKeyword").val();
	console.log(keyword)
});

$(".LoginPersonBox").on('click',".unreadCount",function(){
	if(isLogin){
		window.location.href=userLetterUrl+userId;
	}else{
		toastr.error("要登录哦！");
	}
});

$(".searchBtn").click(function(){
	var keyword = $(".inputKeyword").val();
	console.log(keyword)
	if(keyword == ""){
		toastr.info("要输入内容哦！");
		return;
	}else{
		var searchUrl = encodeURI("http://www.codecloud.com:8848/blog/search.html?keyword="+keyword); 
		window.location.href=searchUrl;
	}
})

$(".myUserProfile").click(function(){
	// 登录才能点
	if(isLogin){
		var myUserProfileUrl = encodeURI(allUserProfile+userId);
		window.location.href=myUserProfileUrl;
	}else{
		toastr.error("请登录后再操作哦！");
	}
	
});

// $(".userProfile").click(function(){
// 	var userProfileUrl = encodeURI("../blog/userspace/userProfile.html?userId="+keyword);
// });
// $(".myBlog").click(function(){
// 	var myBlogUrl = encodeURI("../blog/userspace/my-blog.html?userId="+keyword);
// });