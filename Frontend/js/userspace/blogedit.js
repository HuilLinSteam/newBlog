/*!
 * blogedit.html 页面脚本.
 * 
 * @since: 1.0.0 2017-03-26
 * @author Way Lau <https://waylau.com>
 */
"use strict";
//# sourceURL=blogedit.js

// DOM 加载完再执行
$(function() {
	var updateBlogId = MyBlog.getParameter("blogId");
	// 如果登录才给发博客
	if(isLogin){
		
		if(updateBlogId!=null){
			MyBlog.ajax("/blog/detail/"+updateBlogId,null,"GET",function(resVO){
				if(resVO.code == 1){
					$("#title").val(resVO.data.blog.title);
					$("#summary").val(resVO.data.blog.summary);
					$("#md").val(resVO.data.blog.content);
				}
			})
		}
		
		// 初始化 md 编辑器
		$("#md").markdown({
		    language: 'zh',
		    fullscreen: {
		        enable: true
		    },
		    resize:'vertical',
		    localStorage:'md',
		    //imgurl: 'http://localhost:8081',
		    //base64url: 'http://localhost:8081'
		});
		  
		 
		$("#uploadImage").click(function() {
			var tempFile = document.getElementById("file").files[0];
			var data = new FormData();
			data.append("headerImage",tempFile);
			$.ajax({
			    url: MyBlog.CTX+"/user/upload/",
			    contentType:"multipart/form-data",
			    type: 'POST',
			    cache: false,
			    data: data,
			    processData: false,
			    contentType: false,
			    dataType:"json",
			    xhrFields: {
					//允许接受从服务器端返回的cookie信息 ,默认值为false 也就是说如果必须设置为true的时候 才可以接受cookie 并且请求带上
					withCredentials: true
			    },
			    success: function(data){
					if(data.code == 1){
						var mdcontent=$("#md").val();
						 $("#md").val(mdcontent + "\n![]("+data.data+") \n");
					}else{
						toastr.error(data.message);
					}
		         }
			}).done(function(res) {
				$('#file').val('');
			}).fail(function(res) {});
		})
		 
		
	}else{
		window.location.href="../index.html";
	}
	
	// 发布博客
	$("#submitBlog").click(function() {
		if(isLogin){
			if(updateBlogId!=null){
				MyBlog.ajax("/blog/updateBlog","id="+updateBlogId+"&title="+$('#title').val()+"&summary="+$('#summary').val()+"&content="+$('#md').val()
				,"POST",function(resVO){
					if(resVO.code == 1){
						toastr.success("修改成功!");
						setTimeout(function(){
							window.location.href = allBlogHtmlUrl+updateBlogId;
						},1000);
					}else{
						toastr.info(resVO.message);
					}
				})
			}else{
				MyBlog.ajax("/blog/add","title="+$('#title').val()+"&summary="+$('#summary').val()+"&content="+$('#md').val(),"POST",function(data){
					if(data.code == 1){
						window.location.href="blog.html?blogId="+data.data;
					}
				});
			}
			
		}
	})
 	
});