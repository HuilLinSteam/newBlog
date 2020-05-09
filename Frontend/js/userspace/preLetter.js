// DOM 加载完再执行
$(function() {
	var getLetterUserId = MyBlog.getParameter("userId");
	$(".letterHtmlUrl").attr("href", userLetterUrl + getLetterUserId);
	$(".noticeHtmlUrl").attr("href", userNoticeUrl + getLetterUserId);
	var currentLetterPage = 1;
	if (isLogin && userId == getLetterUserId) {
		getLetter(currentLetterPage);
		getLetterPage();
	}else{
		window.location.href=indexHtmlUrl
	}
	
	function getLetter(toSearchPage) {
		MyBlog.ajax("/letter/list", "current="+toSearchPage, "GET", function(resVO) {
			if (resVO.code == 1) {
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
				var conversations = resVOData.conversations;
				if(conversations.length > 0){
					$(".letterListBox").empty();
					for (var i = 0; i < conversations.length; i++) {
						
						var li = 
							'<li class="media pb-3 pt-3 mb-3 border-bottom position-relative">'+
								'<span class="badge badge-danger " style="position:absolute;left:20px">'+(conversations[i].unreadCount > 0 ? conversations[i].unreadCount : "")+'</span>'+
								'<a href="'+allUserProfile+conversations[i].target.id+'">'+
									'<img src="'+conversations[i].target.headerUrl+'" class="mr-4 rounded-circle user-header" alt="用户头像">'+
								'</a>'+
								'<div class="media-body">'+
									'<h6 class="mt-0 mb-3">'+
										'<span class="text-success">'+conversations[i].target.username+'</span>'+
										'<span class="float-right text-muted font-size-12">'+formatDate(conversations[i].conversation.createTime)+'</span>'+
									'</h6>'+
									'<div>'+
										'<a href="'+userLetterDetailUrl+conversations[i].conversation.conversationId+'">'+conversations[i].conversation.content+'</a>'+
										'<ul class="d-inline font-size-12 float-right">'+
											'<li class="d-inline ml-2"><a href="#" class="text-primary">共'+conversations[i].letterCount+'条会话</a></li>'+
										'</ul>'+
									'</div>'+
								'</div>'+
							'</li>'+
							'<hr>';
						
						$(".letterListBox").append(li);
					}
				}else{
					$(".letterListBox").html("暂时没有人发私信给你哦！");
				}
				
				
			} else {
				toastr.error("你还没有登录哦！");
			}
		});
	}
	
	// 分页
	function getLetterPage(){
		MyBlog.ajax("/letter/list", null, "GET", function(resVO) {
			$(".userLetterPage").empty();
			// 分页查询
			if(resVO.data.page.rows>0){
				var followeePage = resVO.data.page.total;
				$(".userLetterPage").append('<li class="page-item active"><a class="page-link">1</a></li>');
				for (var i = 1; i < followeePage; i++) {
					$(".userLetterPage").append('<li class="page-item"><a class="page-link">'+(i+1)+'</a></li>');
				}
			}else{
				$(".letterListBox").html("暂无关注对象哦！");
				$(".userLetterPage").html("");
			}
		});
	}
	
	// 点击分页
	$(".userLetterPage").on('click','.page-link',function(){
		currentLetterPage = $(this).text();
		console.log(currentLetterPage);
		$(".letterListBox").html("");
		$(this).parent().siblings().removeClass("active");
		$(this).parent().addClass("active");
		getLetter(currentLetterPage);
	});
	
	// 发送私信
	$("#sendBtn").click(function(){
		var targetUsername = $("#recipient-name").val();
		var sendContent = $("#message-text").val();
		if(targetUsername == "" || sendContent == ""){
			toastr.info("请输入内容哦!");
		}else{
			MyBlog.ajax("/letter/send","toName="+targetUsername+"&content="+sendContent,"POST",function(data){
				if(data.code == 1){
					// $("#hintBody").text("发送成功!");
					// $("#sendModal").modal("hide");
					toastr.success("发送成功!");
					$("#sendModal").modal("hide");
				}else{
					//$("#hintBody").text("服务器繁忙哦!");
					toastr.info("服务器繁忙哦!");
				}
				//$("#hintModal").modal("show");
				setTimeout(function(){
					// $("#hintModal").modal("hide");
					window.location.reload();
				},2000);
			});
		}
		
	});

});
