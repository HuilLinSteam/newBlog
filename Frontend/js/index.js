var nowCurrent = 1;
var tempOrdeModel = MyBlog.getParameter("orderModel");
if(tempOrdeModel!=null){
	nowModel = tempOrdeModel;
}else{
	nowModel = 0;
}
function getIndexPage(current,nowModel){
	MyBlog.ajax("/index","current="+current+"&orderMode="+nowModel,"GET",function(data){
		var blogMap = data.blogMap;
		for (var i = 0; i < blogMap.length; i++) {
			console.log(blogMap[i])
			var blogContent = 
					'<div class="card mb-4 blogData">'+
						'<div class="card-block">'+
							'<h4 class="card-title">'+
								'<span>'+
									'<img src="'+blogMap[i].user.headerUrl+'" class="blog-avatar-50" alt="'+blogMap[i].user.id+'">'+
								'</span>'+
								'<a href="userspace/blog.html?blogId='+blogMap[i].blog.id+'" class="card-link" style="margin-left:20px" title="blog">'+
									 blogMap[i].blog.title+
								'</a>'+
								(blogMap[i].blog.type == 1 ? '<button class="btn badge badge-danger badge-pill" style="margin-left: 25px;">置顶</button>':"")+
								(blogMap[i].blog.status == 1 ?  '<button class="btn badge badge-warning badge-pill" style="margin-left: 25px;">精华</button>':"")+
							'</h4>'+
							'<hr/>'+
							'<p class="card-text"> 摘要:'+
									blogMap[i].blog.summary+
							'</p>'+
							'<div class="card-text">'+
								'<a href="'+allUserProfile+blogMap[i].user.id+'" class="card-link" style="margin-right: 20px">'+blogMap[i].user.username+'</a>'+
								'发表于 '+(formatDate(blogMap[i].blog.createTime))+
								'<div style="float:right">'+
									'<button class="btn badge badge-default badge-pill" style="margin-left: 25px;font-size:12px">浏览量'+blogMap[i].enjoyCount+'</button>'+
									'<button class="btn badge badge-info badge-pill" style="margin-left: 5px;font-size:12px">点赞量'+blogMap[i].likeCount+'</button>'+
									'<button class="btn badge badge-primary badge-pill" style="margin-left: 5px;font-size:12px">评论量'+blogMap[i].blog.commentCount+'</button>'+
								'</div>'+
							'</div>'+
						'</div>'+
					'</div>';
			$(".blogContents").append(blogContent);
		}
	});
}
function getPageFunc(){
	MyBlog.ajax("/index",null,"GET",function(data){
		var totalPage = data.page.total;
		$(".pagination").append('<li class="page-item active"><a class="page-link">'+1+'</a></li>')
		for (var i = 1; i < totalPage; i++) {
			$(".pagination").append('<li class="page-item"><a class="page-link">'+(i+1)+'</a></li>')
		}
	})
}


$(".pagination").on('click','.page-link',function(){
	nowCurrent = $(this).text();
	$("div").remove(".blogData");
	$(this).parent().siblings().removeClass("active");
	$(this).parent().addClass("active");
	getIndexPage(nowCurrent,nowModel);
});
getIndexPage(nowCurrent,nowModel);
getPageFunc();


// 热门文章:显示前5条
// http://api.codecloud.com:8080/index?orderMode=1&limit=5
MyBlog.ajax("/index?","orderMode=1&limit=5","GET",function(data){
	var blogs = data.blogMap;
	for(blog of blogs){
		var Alist = '<a href="userspace/blog.html?blogId='+blog.blog.id+'" class="list-group-item" title="blog">'+
						blog.blog.title+
						'<span class="badge badge-danger badge-pill">阅读量 '+blog.enjoyCount+'</span>'+
					'</a>';
		$(".hotArticle").append(Alist);
	}
	
});
// 最新发布文章：显示前5条
// http://api.codecloud.com:8080/index?orderMode=0&limit=5
MyBlog.ajax("/index?","orderMode=0&limit=10","GET",function(data){
	
	var blogs = data.blogMap;
	for(var i = 0;i<blogs.length;i++){
		if(blogs[i].blog.type==0){
			var Alist = '<a href="userspace/blog.html?blogId='+blogs[i].blog.id+'" class="list-group-item" title="blog">'+
							blogs[i].blog.title+
							'<span class="badge badge-info badge-pill">阅读量 '+blogs[i].enjoyCount+'</span>'+
						'</a>';
			$(".newPublic").append(Alist);
		}
	}
});