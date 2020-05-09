// DOM 加载完再执行
var currentPageNumber = 1;
var conversationId = MyBlog.getParameter("conversationId");
function getLetterDetail(pageNumber) {
	MyBlog.ajax("/letter/detail/" + conversationId, "current=" + pageNumber, "GET", function(resVO) {
		if (resVO.code == 1) {
			var letters = resVO.data.letters;
			allPageNumber = resVO.data.page.total;
			$(".fromUserLetter").html(resVO.data.target.username);
			$("#recipient-name").attr("value", resVO.data.target.username);
			$(".userLetterDetailBox").empty();
			if (letters.length > 0) {
				for (var i = 0; i < letters.length; i++) {
					var li =
						'<li class="media pb-3 pt-3 mb-2">' +
						'<a href="profile.html">' +
						'<span class="badge badge-warning float-left" style="position:absolute;left:10px">'+(letters[i].fromUser.id == userId ? "我":"")+'</span>'+
						'<img src="' + letters[i].fromUser.headerUrl +
						'" class="mr-4 rounded-circle user-header" style="width: 50px;height: 50px;" alt="用户头像">' +
						'</a>' +
						'<div class="card media-body p-3" role="alert" aria-live="assertive" aria-atomic="true">' +
						'<div class="toast-header">' +
						'<strong class="mr-auto"><a href="' + allUserProfile + letters[i].fromUser.id + '">' + letters[i].fromUser.username +
						'<a/></strong> ' +
						'<small class="float-right">' + formatDate(letters[i].letter.createTime) + '</small>' +
						'</div>' +
						'<div class="toast-body">' +letters[i].letter.content +'</div>' +
						'</div>' +
						'</li>' +
						'<hr/>';
					$(".userLetterDetailBox").append(li);
				}
			} else {
				$(".userLetterDetailBox").html("");
			}

		} else {
			toastr.info("服务器繁忙哦！");
		}
	});
}

function getLetterDetailPageNumber() {
	MyBlog.ajax("/letter/detail/" + conversationId, null, "GET", function(resVO){
		var allPageNumber = resVO.data.page.total;
		console.log(allPageNumber);
		if (allPageNumber > 0) {
			$(".userLetterDetailPageNumber").append('<li class="page-item active"><a class="page-link">1</a></li>');
			for (var i = 1; i < allPageNumber; i++) {
				$(".userLetterDetailPageNumber").append('<li class="page-item"><a class="page-link">' + (i + 1) + '</a></li>');
			}
		}
	});
	
}
$(function() {
	if (isLogin && userId != null) {
		$(".returnLetterPage").click(function() {
			window.location.href = userLetterUrl + userId
		});
		getLetterDetail(currentPageNumber);
		getLetterDetailPageNumber();
	} else {
		window.location.href = indexHtmlUrl;
	}
	$(".userLetterDetailPageNumber").on('click','.page-link',function(){
		currentPageNumber = $(this).text();
		console.log(currentPageNumber);
		// $(".noticeDetailPageManger").html("");
		$(this).parent().siblings().removeClass("active");
		$(this).parent().addClass("active");
		getLetterDetail(currentPageNumber)
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
