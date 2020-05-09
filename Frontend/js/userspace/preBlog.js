var blogId = MyBlog.getParameter("blogId");
var nowCommentPage = 1;
function getDetailBlog(sendCommentPage){
	MyBlog.ajax("/blog/detail/" + blogId, "current="+sendCommentPage, "GET", function(resVo) {
		if(resVo.code != 1){
			window.location.href = indexHtmlUrl;
		}
		var blog = resVo.data.blog;
		var baseAdd = sendCommentPage == 1?0:(sendCommentPage-1)*5;
		// 显示评论内容
		if(blog.commentCount > 0){
			$(".commentArea").append("<h5>评论区</h5><hr/>");
			var blogComments = resVo.data.comments;
			for (var i = 0; i < blogComments.length; i++) {
				var completelyComment = 
				'<div class="row p-3">'+
					// 显示头像
					'<h2 class="card-title col-lg-1 col-md-2">'+
						'<span>'+
							'<a href="'+allUserProfile+blogComments[i].commentUser.id+'" title="'+blogComments[i].commentUser.name+'">'+
								'<img src="'+blogComments[i].commentUser.headerUrl+'" class="blog-avatar-50" alt="'+blogComments[i].commentUser.id+'">'+
							'</a>'+
						'</span>'+
					'</h2>'+
					'<div class="card-text col-lg-11 col-md-10 comm'+i+'">'+
						// 评论具体内容
						'<div style="font-size: 13px;">'+
							'<a href="'+allUserProfile+blogComments[i].commentUser.id+'" class="card-link">'+blogComments[i].commentUser.username+'</a> '+(baseAdd+i+1)+'楼 '+formatDate(blogComments[i].comment.createTime)+
							'<ul class="d-inline float-right">'+
								'<li class="d-inline ml-2">'+
									'<span class="text-primary likeReply" blogId="'+blogId+'" likeCommentId="'+blogComments[i].comment.id+'" commentUserId="'+blogComments[i].commentUser.id+'">'+((blogComments[i].likeStatus)>0?"已赞":"赞")+'('+blogComments[i].likeCount+')</span>'+
								'</li>'+
								'<li class="d-inline ml-2">|</li>'+
								'<li class="d-inline ml-2">'+
									'<span id="toreply" class="text-primary" blogId="'+blogId+'" targetId="0" entityId="'+blogComments[i].comment.id+'" commentUserId="'+blogComments[i].commentUser.id+'">回复('+blogComments[i].replyCount+')</span>'+
								'</li>'+
							'</ul>'+
							'<p>'+blogComments[i].comment.content+'</p>'+
						'</div>'+
					'<div>'+
					
				'</div>';
				// console.log(blogComments[i]);
				$(".commentArea").append(completelyComment);
				// 该条评论下有回复
				if(blogComments[i].replyCount>0){
					var ulreply =
					'<ul class="list-unstyled bg-gray font-size-12 text-muted ulreply'+i+'"></ul>';
					$(".comm"+i).append(ulreply);
					var bcReplys = blogComments[i].replys;
					for (var j = 0; j < bcReplys.length; j++) {
						console.log(bcReplys[j].reply);
						var replyCommentLi =
						 '<li class="pt-3 mb-3 border-bottom">'+
							'<div style="font-size: 12px;">'+
								'<a href="'+allUserProfile+bcReplys[j].replyUser.id+'">'+bcReplys[j].replyUser.username+'</a>'+
								(bcReplys[j].target == null ?"":'回复  <a href="'+allUserProfile+bcReplys[j].target.id+'">'+bcReplys[j].target.username+'</a>')+
										'<span style="margin-left: 13px;">'+formatDate(bcReplys[j].reply.createTime)+'</span>'+
								'<ul class="d-inline float-right">'+
									'<li class="d-inline ml-2">'+
										'<span class="text-primary likeReply" blogId="'+blogId+'" likeCommentId="'+bcReplys[j].reply.id+'" commentUserId="'+bcReplys[j].replyUser.id+'">'+((bcReplys[j].likeStatus)>0?"已赞":"赞")+'('+bcReplys[j].likeCount+')'+'</span>'+
									'</li>'+
									'<li class="d-inline ml-2">|</li>'+
									'<li class="d-inline ml-2">'+
										'<span id="toreply" class="text-primary" blogId="'+blogId+'" entityId="'+bcReplys[j].reply.entityId+'" targetId="'+bcReplys[j].replyUser.id+'" commentUserId="'+bcReplys[j].replyUser.id+'">回复</span>'+
									'</li>'+
								'</ul>'+
								'<p>'+bcReplys[j].reply.content+'</p>'+
							'</div>'+
							'<hr />'+
						 '</li>';
						
						$(".ulreply"+i).append(replyCommentLi);
					}
					
				}
				
			}
		}
	});
}


MyBlog.ajax("/blog/detail/" + blogId, null, "GET", function(resVo) {
	//console.log(resVo);
	if(resVo.code != 1){
		return;
	}
	var blog = resVo.data.blog;
	var blogUser = resVo.data.user;
	var comment = resVo.data.comments;
	var enjoyCount = resVo.data.enjoyCount;
	var likeCount = resVo.data.likeCount;
	$(".blogTitle").html(blog.title);
	$(".blogDetailUserImgUrl").attr("src",blogUser.headerUrl);
	$(".blogUser").html(blogUser.username);
	$(".blogUser").attr("href",allUserProfile+blogUser.id);
	$(".blogTime").html(formatDate(blog.createTime));
	$(".seeCount").html(enjoyCount)
	$(".likeCount").html(likeCount);
	$(".commentCount").html(blog.commentCount);
	$(".post-content").html(blog.htmlContent);
	$(".summary").html(blog.summary);
	if (isLogin) {
		$("#likeBlog").removeClass("hidden");
		// 等于0说明该用户没有对博客点赞
		if(resVo.data.likeStatus == 0){
			$("#likeBlog").removeClass("btn-danger");
			$("#likeBlog").addClass("btn-warning");
			$("#likeBlog").html("点赞");
		}else{
			$("#likeBlog").removeClass("btn-warning");
			$("#likeBlog").addClass("btn-danger");
			$("#likeBlog").html("已赞");
		}
		$("#likeBlog").attr("blogId",blogId);
		$("#likeBlog").attr("blogUserId",blogUser.id);
		$("#commentBlog").removeClass("hidden");
		$("#commentBlog").attr("blogId",blogId);
		if(resVo.data.blog.userId == userId){
			$(".blogEditBox").append('<button href="#" class="btn btn-primary float-right updateBlogBtn">编辑</button>');
		}
		if(userType != null){
			if(userType == 1){
				$(".blogEditBox").append('<button href="#" class="btn btn-danger float-right deleteBlogBtn">删除</button>');
			}
			else if(userType == 2){
				if(blog.type == 1){
					$(".blogEditBox").append('<button href="#" class="btn btn-secondary float-right ml-3" disabled="disabled">已置顶</button>');
				}else{
					$(".blogEditBox").append('<button href="#" class="btn btn-secondary float-right toTopBlogBtn ml-3">置顶</button>');
				}
				if(blog.status == 1){
					$(".blogEditBox").append('<button href="#" class="btn btn-warning float-right" disabled="disabled">已加精</button>');
				}else{
					$(".blogEditBox").append('<button href="#" class="btn btn-warning float-right toWonderfulBlogBtn">加精</button>');
				}
			}
		}
	}
	
	// 评论分页
	if(blog.commentCount > 0){
		var pageTotal = resVo.data.page.total;
		$(".commentPage").append('<li class="page-item active"><a class="page-link">1</a></li>');
		for (var i = 1; i < pageTotal; i++) {
			$(".commentPage").append('<li class="page-item"><a class="page-link">'+(i+1)+'</a></li>');
		}
	}else{
		$(".commentArea").html("");
		$(".commentPage").html("");
	}
});

// 初始化获取评论
getDetailBlog(nowCommentPage);

// 点击分页
$(".commentPage").on('click','.page-link',function(){
	nowCommentPage = $(this).text();
	//$("div").remove(".commentArea");
	$(".commentArea").empty();
	$(this).parent().siblings().removeClass("active");
	$(this).parent().addClass("active");
	getDetailBlog(nowCommentPage);
});
// // 处理删除博客事件

// $(".blog-content-container").on("click",".blog-delete-blog", function () { 
// 	// 获取 CSRF Token 
// 	var csrfToken = $("meta[name='_csrf']").attr("content");
// 	var csrfHeader = $("meta[name='_csrf_header']").attr("content");


// 	$.ajax({ 
// 		 url: $(this).attr("blogUrl") , 
// 		 type: 'DELETE', 
// 		 beforeSend: function(request) {
//                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
//             },
// 		 success: function(data){
// 			 if (data.success) {
// 				 // 成功后，重定向
// 				 window.location = data.body;
// 			 } else {
// 				 toastr.error(data.message);
// 			 }
// 	     },
// 	     error : function() {
// 	    	 toastr.error("error!");
// 	     }
// 	 });
// });

