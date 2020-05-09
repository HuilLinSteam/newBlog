//var searchKeyword = MyBlog.getParameter("keyword");
var nowSearchPage = 1;
var searchUrl =window.location.href;
var searchData =searchUrl.split("=");        //截取 url中的“=”,获得“=”后面的参数
var searchKeyword =decodeURI(searchData[1]);  
$(".inputKeyword").val(searchKeyword);
function getSearchPage(keyword,searchPage){
	MyBlog.ajax("/search","keyword="+keyword+"&current="+nowSearchPage,"GET",function(data){
		var blogMap = data.data.blogMap;
		for (var i = 0; i < blogMap.length; i++) {
			var blogContent = '<div class="mb-4 blogData">'+
						'<div class="card-block">'+
							'<h6 class="card-title">'+
								'标题:<a href="userspace/blog.html?blogId='+blogMap[i].blog.id+'" class="card-link" title="blog">'+
									 blogMap[i].blog.title+
								'</a>'+
							'</h6>'+
							'<p class="card-text"> 摘要:'+
									blogMap[i].blog.summary+
							'</p>'+
							'<div class="card-text">'+
								' 用户：<a href="'+allUserProfile+blogMap[i].user.id+'" class="card-link" style="margin-right: 20px">'+blogMap[i].user.username+'</a>'+
								'<span class="float-right">发表于 '+(formatDate(blogMap[i].blog.createTime))+'<span>'+
							'</div>'+
						'</div>'+
					'</div><hr/>';
			$(".searchBlog").append(blogContent);
		}
	});
}
$(".pagination").on('click','.page-link',function(){
	nowSearchPage = $(this).text();
	$("div").remove(".blogData");
	$(this).parent().siblings().removeClass("active");
	$(this).parent().addClass("active");
	getSearchPage(searchKeyword,nowSearchPage);
});
function getSearchPageFunc(keyword){
	MyBlog.ajax("/search","keyword="+keyword,"GET",function(data){
		var totalPage = data.data.page.total;
		$(".pagination").append('<li class="page-item active"><a class="page-link">'+1+'</a></li>')
		for (var i = 1; i < totalPage; i++) {
			$(".pagination").append('<li class="page-item"><a class="page-link">'+(i+1)+'</a></li>')
		}
		$(".searchCount").html(data.data.page.rows);
	})
}
getSearchPage(searchKeyword);
getSearchPageFunc(searchKeyword,nowSearchPage);