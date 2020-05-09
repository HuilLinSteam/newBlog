// DOM 加载完再执行
$(function() {
	var noticeType = MyBlog.getParameter("type");
	var currentPageNumber = 1;
	if(isLogin && userId!=null){
		$(".returnNoticePage").click(function(){
			window.location.href = userNoticeUrl+userId
		});
		getSystemNoticePage(currentPageNumber,noticeType);
		getSystemNoticePageNumber();
	}else{
		window.location.href=indexHtmlUrl;
	}
	
	
	function getSystemNoticePage(pageNumber,paramType){
		MyBlog.ajax("/notice/detail/"+paramType,"current="+pageNumber,"GET",function(resVO){
			if(resVO.code == 1){
				var resVOData = resVO.data;
				var resVODataNotice = resVOData.notices;
				$(".systemNoticeBox").empty();
				for(var i = 0;i<resVODataNotice.length;i++){
					var li = 
							'<li class="media pb-3 pt-3 mb-2">'+
								'<img src="http://static.nowcoder.com/images/head/notify.png" class="mr-4 user-header" style="width: 50px;height: 50px;" alt="系统图标">'+
								'<div class="card media-body p-3">'+
									'<div class="toast-header">'+
										'<strong style="color: orangered;">'+resVODataNotice[i].fromUser.username+'</strong>'+
										'<small class="float-right">'+formatDate(resVODataNotice[i].notice.createTime)+'</small>'+
									'</div>'+
									'<div class="toast-body">'+
										'<span>用户 <i class="fa fa-user" style="color:#aa8e06">'+resVODataNotice[i].user.username+'</i>'+
										// 1是评论 -博客 -回复
										// 2是点赞 -博客 -回复
										// 3是关注用户
										(
											(paramType == 'like') ? 
											(resVODataNotice[i].entityType == 1 ? '<span>点赞了你的<b style="color:#71aa82">博客</b></span>' :'<span>点赞了你的<b style="color:#ff7575">回复</b> </span>'):
											(
												(paramType == 'comment') ?
												(resVODataNotice[i].entityType == 1 ? '<span>评论了你的<b style="color:#71aa82">博客</b></span>' :'<span>评论了你的<b style="color:#ff7575">回复<b/> </span>'):
												'<span>关注了你</span>'
											)
										)+
										'<a class="text-primary" href="'+((resVODataNotice[i].entityType == 1) ? (allBlogHtmlUrl+resVODataNotice[i].blogId):(resVODataNotice[i].entityType == 2)?allBlogHtmlUrl+resVODataNotice[i].blogId:allUserProfile+resVODataNotice[i].user.id )+'">点击查看</a> !</span>'+
									'</div>'+
								'</div>'+
							'</li>'+
							'<hr />';
					$(".systemNoticeBox").append(li);
					
					
				}
				

			}else{
				toastr.error("服务器异常");
			}
		});
	}
	
	
	
	$(".noticeDetailPageManger").on('click','.page-link',function(){
		currentPageNumber = $(this).text();
		console.log(currentPageNumber);
		// $(".noticeDetailPageManger").html("");
		$(this).parent().siblings().removeClass("active");
		$(this).parent().addClass("active");
		getSystemNoticePage(currentPageNumber,noticeType)
	});
	
	
	function getSystemNoticePageNumber(){
		MyBlog.ajax("/notice/detail/"+noticeType,null,"GET",function(resVO){
			// 分页查询
			if(resVO.data.page.rows>0){
				var noticeDetailPage = resVO.data.page.total;
				$(".noticeDetailPageManger").append('<li class="page-item active"><a class="page-link">1</a></li>');
				for (var i = 1; i < noticeDetailPage; i++) {
					$(".noticeDetailPageManger").append('<li class="page-item"><a class="page-link">'+(i+1)+'</a></li>');
				}
			}else{
				$(".noticeDetailPageManger").html("");
			}
		});
	}
});



