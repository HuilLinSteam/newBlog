// DOM 加载完再执行
$(function() {
	var getNoticeUserId = MyBlog.getParameter("userId");
	$(".letterHtmlUrl").attr("href", userLetterUrl + getNoticeUserId);
	$(".noticeHtmlUrl").attr("href", userNoticeUrl + getNoticeUserId);
	if (isLogin && userId == getNoticeUserId) {
		getNotice();
		// getNoticePage();
	}else{
		window.location.href=indexHtmlUrl
	}
	function getNotice(){
		MyBlog.ajax("/notice/list",null,"GET",function(resVO){
			var resVOData = resVO.data;
			// 判断私信的数量
			if(resVOData.letterUnreadCount > 0){
				$(".friendLetter").html(resVOData.letterUnreadCount);
			}else if(resVOData.letterUnreadCount > 99){
				$(".friendLetter").html("99+");
			}else{
				$(".friendLetter").html("");
			}
			// 判断系统通知的数量
			if(resVOData.noticeUnreadCount > 0){
				$(".noticeLetter").html(resVOData.noticeUnreadCount);
			}else if(resVOData.noticeUnreadCount > 99){
				$(".noticeLetter").html("99+");
			}else{
				$(".noticeLetter").html("");
			}
			$(".noticeUserBox").empty();
			
			// 如果有评论的消息
			if(resVOData.commentNotice!=null){
				var commentNotice = resVOData.commentNotice;
				
				var li = 
						'<li class="media pb-3 pt-3 mb-3 border-bottom position-relative noticeCommentBox">'+
						'<span class="badge badge-danger commentNoticeCount" style="position:absolute;left:28px">'+(commentNotice.unread>0?commentNotice.unread:"")+'</span>'+
						'<img src="http://static.nowcoder.com/images/head/reply.png" style="width: 60px;height: 60px;" class="mr-4 user-header" alt="通知图标">'+
						'<div class="media-body">'+
							'<h6 class="mt-0 mb-3">'+
								'<span>评论</span>'+
								'<span class="float-right text-muted font-size-12">'+formatDate(commentNotice.message.createTime)+'</span>'+
							'</h6>'+
							'<div>'+
								'<a href="'+systemNoticeTyeUrl+'comment'+'">用户 <i>'+commentNotice.user.username+'</i> 评论了你的<b>'+(commentNotice.entityType == 1 ? "博客":"回复")+'</b> ...</a>'+
								'<ul class="d-inline font-size-12 float-right">'+
									'<li class="d-inline ml-2"><span class="text-primary">共 <i>'+commentNotice.count+'</i> 条会话</span></li>'+
								'</ul>'+
							'</div>'+
						'</div>'+
					'</li>'+
					'<hr/>';
						
				$(".noticeUserBox").append(li);
			}
			
			// 如果有点赞的消息
			if(resVOData.likeNotice!=null){
				var likeNotice = resVOData.likeNotice;
			
				var li = 
						'<li class="media pb-3 pt-3 mb-3 border-bottom position-relative noticeLikeBox">'+
						'<span class="badge badge-danger likeNoticeCount" style="position:absolute;left:28px">'+(likeNotice.unread > 0? likeNotice.unread:"")+'</span>'+
						'<img src="http://static.nowcoder.com/images/head/like.png" style="width: 60px;height: 60px;" class="mr-4 user-header" alt="通知图标">'+
						'<div class="media-body">'+
							'<h6 class="mt-0 mb-3">'+
								'<span>赞</span>'+
								'<span class="float-right text-muted font-size-12 likeUserTime">'+formatDate(likeNotice.message.createTime)+'</span>'+
							'</h6>'+
							'<div>'+
								'<a href="'+systemNoticeTyeUrl+'like'+'">用户 <i class="likeUserName">'+likeNotice.user.username+'</i> 点赞了你的<b class="likeUserType">'+(likeNotice.entityType == 1 ? "博客":"回复")+'</b> ...</a>'+
								'<ul class="d-inline font-size-12 float-right">'+
									'<li class="d-inline ml-2"><span class="text-primary">共 <i class="likeNoticeCount">'+likeNotice.count+'</i> 条会话</span></li>'+
								'</ul>'+
							'</div>'+
						'</div>'+
					'</li>'+
					'<hr/>';
				
				$(".noticeUserBox").append(li);
			}
			
			// 如果有人关注
			if(resVOData.followNotice!=null){
				var followNotice = resVOData.followNotice;
				
				var li = 
						'<li class="media pb-3 pt-3 mb-3 border-bottom position-relative noticeFollowBox">'+
						'<span class="badge badge-danger" style="position:absolute;left:28px">'+(followNotice.unread>0?followNotice.unread:"")+'</span>'+
						'<img src="http://static.nowcoder.com/images/head/follow.png" style="width: 60px;height: 60px;" class="mr-4 user-header" alt="通知图标">'+
						'<div class="media-body">'+
							'<h6 class="mt-0 mb-3">'+
								'<span>关注</span>'+
								'<span class="float-right text-muted font-size-12">'+formatDate(followNotice.message.createTime)+'</span>'+
							'</h6>'+
							'<div>'+
								'<a href="'+systemNoticeTyeUrl+'follow'+'">用户 <i>'+followNotice.user.username+'</i> 关注了你 ...</a>'+
								'<ul class="d-inline font-size-12 float-right">'+
									'<li class="d-inline ml-2"><span class="text-primary">共 <i>'+followNotice.count+'</i> 条会话</span></li>'+
								'</ul>'+
							'</div>'+
						'</div>'+
					'</li>'+
					'<hr/>';
				
				$(".noticeUserBox").append(li);
			}
			
			
		});
	}
});