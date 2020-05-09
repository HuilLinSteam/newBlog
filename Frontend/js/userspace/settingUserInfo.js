var htmlContent = 
'<style>'+
'.cropImg {'+
'    position: relative;'+
'    width: 100%;'+
'    height: 300px;'+
'    background-color: #ccc;'+
'    color: #fff;'+
'    line-height: 300px;'+
'    text-align: center;'+
'    border: 1px dashed rgba(0, 0, 0, .4);'+
'}'+
'.cropImg > img {'+
'    position: absolute;'+
'    left: 50%;'+
'    top: 50%;'+
'    -webkit-transform: translate3d(-50%, -50%, 0);'+
'    -moz-transform: translate3d(-50%, -50%, 0);'+
'    -ms-transform: translate3d(-50%, -50%, 0);'+
'    -o-transform: translate3d(-50%, -50%, 0);'+
'    transform: translate3d(-50%, -50%, 0);'+
'}'+
'</style>'+
'<div id="cropImg" class="cropImg">'+
'    <span>图片剪切</span>'+
'    <img src="">'+
'</div>'+
'<div class="crop" id="crop">'+
'    <input type="file" accept="image/*" class="crop-input">'+
'    <div class="crop-mask"></div>'+
'    <div class="crop-wrap">'+
'        <div class="crop-wrap-content">'+
'            <div class="crop-wrap-thum"></div>'+
'            <div class="crop-wrap-spinner">Loading...</div>'+
'        </div>'+
'        <div class="crop-wrap-group">'+
'            <a href="javascript:;" class="croped">剪切</a>'+
'        </div>'+
'         <div class="crop-wrap-group">'+
'            <a href="javascript:;" class="zoomIn">放大</a>'+
'            <a href="javascript:;" class="zoomOut">缩小</a>'+
'        </div>'+
'    </div>'+
'</div>'+
'<form  enctype="multipart/form-data"  id="avatarformid"  >'+
'</form> '+
'<script>'+
'    var crop = document.querySelector("#cropImg");'+
'    var cropNote = crop.querySelector("span");'+
'    var cropImg = crop.querySelector("img");'+
'    var cropper = new Cropper({'+
'        el: "#crop",'+
'        cp: "#cropImg",'+
'        callback: function (dataURL, dataBlob) {'+
		'cropNote.style.display = "none";'+
		'cropImg.style.display = "block";'+
		'cropImg.src = dataURL;"'+
'        }'+
'    });'+
'</script>';
 

$(function() {
	if (isLogin) {
		if (loginUsername != null) {
			$("#username").val(loginUsername);
		}
		if (loginUserHeaderUrl != null) {
			$(".loginUserHeaderUrl").attr("src", loginUserHeaderUrl);
		}
		if(loginUserOfname != null){
			$("#name").val(loginUserOfname);
		}
		if(loginUserEmail != null){
			$("#email").val(loginUserEmail);
		}
		// 获取编辑用户头像的界面
		$(".blog-content-container").on("click",".blog-edit-avatar", function () {
				$.ajax({
					type:"get",
					url: "avatar.html",
					async:true,
					success: function(data){
						$("#avatarFormContainer").html(data);
					},
					error : function() {
						toastr.error("error!");
					}
				});
		});
		// 提交用户头像的图片数据
		$("#submitEditAvatar").on("click", function () {
			 var file = document.getElementById("crop-input").files[0];
			 var data = new FormData();
			 data.append("headerImage",file);
			 $.ajax({
			 	url: 'http://api.codecloud.com:8080/user/upload',
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
			 			var userHeaderImgUrl = data.data;
			 			$(".blog-avatar").attr("src", userHeaderImgUrl);
						toastr.success("上传成功!");
			 		}else{
			 			toastr.error(data.message);
			 		}
			 	},
			 	error : function() {
			 		toastr.error("error!");
			 	}
			 })
		});


		// 提交修改信息
		$(".updateUserBtn").on("click",function(){
			var userHeaderUrl = $(".blog-avatar").attr("src");
			var userOfName = $("#name").val();
			var userOfEmail = $("#email").val();
			var userOfPassword = $("#password").val();
			MyBlog.ajax("/user/updateUserInfo","id="+userId+"&name="+userOfName+"&email="+userOfEmail+"&headerUrl="+userHeaderUrl+"&password="+userOfPassword,"POST",function(resVO){
				if(resVO.code == 1){
					toastr.success("修改信息成功!");
					setTimeout(function(){
						window.location.reload();
					},1000);
				}else{
					toastr.info("服务器繁忙哦!");
				}
			});
		});


	} else {
		window.location.href = indexHtmlUrl;
	}
});
