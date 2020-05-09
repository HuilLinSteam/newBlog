$.ajaxSetup({
    type:"post",
    error:function (xhr, textStatus, errorThrown) {
    	if(xhr.status==0){
    		console.log("亲,请检查后台服务器是否启动...手动滑稽")
    	}
    	if(xhr.status==404){
    		console.log("亲,请检查请求地址是否正确...手动滑稽")
    	}
    	if(xhr.status==405){
    		console.log("亲,请检查你是否实现了doPost方法...手动滑稽")
    	}
    }
})

var MyBlog=
	{
		CTX:"http://api.codecloud.com:8080",
		getParameter:function(name){
			var pvalues=this.getParameterValues(name);
	    	return pvalues==null?null:pvalues[0];
		},
		getParameterValues:function(name){
			return this.getParameterMap()[name];
		},
		getParameterMap:function(){
			var url = location.search;  
		    var MyBlogRequest = new Object();  
		    if (url.indexOf("?") != -1) {  
		      var str = url.substr(1);  
		      strs = str.split("&");  
		      for(var i = 0; i < strs.length; i ++) {  
		      	 var pname=strs[i].split("=")[0];
		      	 var pvalue=unescape(strs[i].split("=")[1]);
		      	  var pvalues=null;
		      	 if(MyBlogRequest[pname]==null){
		      	 	MyBlogRequest[pname]=new Array(pvalue)
		      	 }else{
		      	 	MyBlogRequest[pname].push(pvalue);
		      	 }
		      }  
		   }  
		   return MyBlogRequest;
		},
		isLogin:function(){
			return this.cookie("JSESSIONID")==null?false:true;
		},
		cookie:function (name) {
			return this.cookies()[name];
        },
		cookieValue:function(name){
			var cookie=this.cookie(name);
			if(cookie==null){
				return null;
			}else{
				return cookie.value;
			}

		},
		cookies:function () {
            var cookiesStr = document.cookie ? document.cookie.split('; ') : [];
			var cookies={};
            for (var i = 0; i < cookiesStr.length; i++) {
                var parts = cookiesStr[i].split('=');
                var cookie = {
                	"name":parts[0],
					"value":decodeURIComponent(parts[1])
				};
                cookies[parts[0]]=cookie;
            }
            return cookies;
        },
		ajax:function(url,parameter,way,fn){
			$.ajax({
		        url:this.CTX+url,
		        type:way,
		        dataType:"json",
		        data:parameter,
		        xhrFields: {
						//允许接受从服务器端返回的cookie信息 ,默认值为false 也就是说如果必须设置为true的时候 才可以接受cookie 并且请求带上
					    withCredentials: true
				},
		        success:function (vo,status,xhr) {
		        	/*if(vo.code==2){
		        		console.log("请检查请求方法是否存在");
		        		return;
		        	}
		        	if(vo.code==3){
		        		console.log("请检查您的服务端控制错误日志");
		        		return;
		        	}*/
		        	
		        	
		            //处理登录的filter
		            if(true){
		                fn(vo,status,xhr);
		            }
		        }
		    })
		},
		ajaxFile:function(url,formId,fn){
			var formData = new FormData($("#"+formId)[0]);
			$.ajax({
	        type: 'post',
	        url:this.CTX+url,
	        dataType:'text',
	        data:formData,
	        contentType:false,
	        processData:false, 
	        success:function(data,status,xhr){
		            //处理登录的filter
		            if(true){
		                fn(data,status,xhr);
		            }
		        }
			})
		},
		page:function(pb,url){
			
			if(url.indexOf("?") != -1){
				//带参数
				
			}else{
				//不带参数
				url+="?_t="+new Date().getTime();
			}
			var pageHTML="";
			if(pb.pageNumber==1){
				pageHTML+="<li class=\"disabled\"><a href=\"javascript:;\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>\n";
			}else{
				pageHTML+="<li ><a href=\""+url+"&pageNumber="+(pb.pageNumber-1)+"\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>\n";
			}
			for(var i=pb.start;i<=pb.end;i++){
				if(i==pb.pageNumber){
					pageHTML+="<li class='active'><a href='javascript:;' >"+i+"</a></li>"
				}else{
					pageHTML+="<li ><a  href='"+url+"&pageNumber="+i+"'>"+i+"</a></li>"
				}
				
			}
			if(pb.pageNumber==pb.totalPage){
				pageHTML+="<li class=\"disabled\" ><a href=\"javascript:;\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>"
			}else{
				pageHTML+="<li><a href='"+url+"&pageNumber="+(pb.pageNumber+1)+"' aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>"
			}
			return pageHTML;	
			
		},
		time2str:function(t){
			var date=new Date(t);
			return ""+date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
		}
}


