// DOM 加载完再执行
$(function() {
	$.catalog("#catalog", ".post-content");
	//点击显示回复框
	var replyEntityId = -1;
	var replyTargetId = -1;
	$("#likeBlog").click(function() {
		if (isLogin) {
			var likeBlogId = $("#likeBlog").attr("blogId");
			var likeBlogUserId = $("#likeBlog").attr("blogUserId");
			if (likeBlogId != -1 && likeBlogUserId != -1 && userId != null) {
				MyBlog.ajax("/like",
					"entityType=1&entityId=" + likeBlogId + "&entityUserId=" + likeBlogUserId + "&blogId=" + likeBlogId,
					"GET",
					function(data) {
						if (data.code == 1) {
							if (data.data.likeStatus == 1) {
								$("#likeBlog").removeClass("btn-warning");
								$("#likeBlog").addClass("btn-danger");
								$(".likeCount").html(data.data.likeCount);
								$("#likeBlog").html("已赞");

							} else {
								$("#likeBlog").removeClass("btn-danger");
								$("#likeBlog").addClass("btn-warning");
								$(".likeCount").html(data.data.likeCount);
								$("#likeBlog").html("点赞");
							}
						} else {
							toastr.info("服务器繁忙！");
						}
					});
			}
		} else {
			toastr.error("请登录后再点赞哦！");
		}
	});


	$("#commentBlog").click(function() {
		if (isLogin) {
			var commentBlogId = $("#commentBlog").attr("blogId");
			if (commentBlogId != -1 && userId != null) {
				MyBlog.ajax("/comment/add/" + commentBlogId, "content=" + $(".commentContent").val() +
					"&entityType=1&entityId=" + commentBlogId,"POST",
					function(data) {
						if (data.code == 1) {
							window.location.reload();
						} else {
							toastr.error("服务器太繁忙哦！");
						}
					});
			} else {
				toastr.error("服务器太繁忙哦！");
			}
		}


	});

	// 显示回复框
	$(".card-block").on("click","#toreply",function(){
		if (isLogin) {
			toastr.info("回复框在下面哦！");
			$(".returnUserBox").attr("src",loginUserHeaderUrl)
			var replyUN = $(this).parent().parent().siblings()[0];
			var commentUserId = $(this).attr("commentUserId");
			var entityId = $(this).attr("entityId");
			var targetId = $(this).attr("targetId");
			console.log("entityId="+entityId);
			console.log("targetId="+targetId);
			$("#replyCommentContent").attr("placeholder", "");
			$("#replyCommentContent").attr("placeholder", "@ " + replyUN.text);
			$("#replyDiv").toggle();
			replyEntityId = entityId;
			replyTargetId = targetId;
		}else {
			toastr.error("请登录后再操作哦！");
			replyEntityId = -1;
		}
	});
	// 点赞评论
	$(".card-block").on("click",".likeReply",function(){
		if (isLogin) {
			// console.log("comment的id:"+$(this).attr("likeCommentId"));
			// console.log("评论的用户id:"+$(this).attr("commentUserId"));
			// console.log("blogId:"+$(this).attr("blogId"));
			var tempThis = $(this);
			MyBlog.ajax("/like",'entityType=2&entityId='+$(this).attr("likeCommentId")+'&entityUserId='+$(this).attr("commentUserId")+'&blogId='+$(this).attr("blogId"),"GET",function(data){
				if(data.code == 1){
					if(data.data.likeStatus == 1){
						toastr.success("点赞成功哦！")
						tempThis.html("已赞("+data.data.likeCount+")");
					}else{
						toastr.success("取消点赞了哦！")
						tempThis.html("赞("+data.data.likeCount+")");
					}
				}else{
					toastr.error("服务器太繁忙哦！");
				}
			});
		}
		else{
			toastr.error("请登录后再操作哦！");
		}
	});
	
	
	$("#submitComment").click(function(){
		if(isLogin && replyEntityId!=-1 && replyTargetId!=-1){
			MyBlog.ajax("/comment/add/"+blogId,"content="+$("#replyCommentContent").val()+"&entityType=2&entityId="+replyEntityId+"&targetId="+replyTargetId,
			"POST",function(data){
				if(data.code == 1){
					window.location.reload();
				}else{
					toastr.info("服务器太繁忙咯！");
				}
			});
		}else{
			toastr.error("请登录后再操作哦！");
		}
	})
	// 置顶
	$(".toTopBlogBtn").click(function(){
		if(isLogin && userType == 2){
			MyBlog.ajax("/blog/top","id="+blogId,"POST",function(resVO){
				if(resVO.code == 1){
					toastr.success("置顶成功哦！");
					setTimeout(function(){
						window.location.reload();
					},2000);
				}
			});
		}else{
			toastr.info("只有版主才能干这事哦！");
		}
	});
	// 加精
	$(".toWonderfulBlogBtn").click(function(){
		if(isLogin && userType == 2){
			MyBlog.ajax("/blog/wonderful","blogId="+blogId,"POST",function(resVO){
				if(resVO.code == 1){
					toastr.success("加精成功哦！");
					setTimeout(function(){
						window.location.href = indexHtmlUrl;
					},2000);
				}
			});
		}else{
			toastr.info("只有版主才能干这事哦！");
		}
	});
	
	// 删除
	$(".deleteBlogBtn").click(function(){
		if(isLogin && userType == 1){
			MyBlog.ajax("/blog/delete","id="+blogId,"POST",function(resVO){
				if(resVO.code == 1){
					toastr.success("删除成功!");
					setTimeout(function(){
						window.location.reload();
					},1000);
				}else{
					toastr.error("自己看服务器日志去!");
				}
			});
		}else{
			toastr.info("只有管理员才能干这事哦！");
		}
	});
	
	// 编辑
	$(".updateBlogBtn").click(function(){
		if(isLogin){
			window.location.href = allUpdateBlogUrl+blogId;
		}else{
			toastr.info("只有博客本人才能干这事哦！");
		}
	});
	
});
