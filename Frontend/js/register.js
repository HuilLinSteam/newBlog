var ALL_CHECK_USERNAME = false;
var ALL_CHECK_PHONE = false;
var ALL_CHECK_PASSWORD = false;
var ALL_CHECK_SENDCODE = false;
function checkUserName(username) {
	if (username == "" || username.length < 6 || username.length > 10) {
		$(".error-username").removeClass("hidden")
		$(".error-username").html("账号填写不规范!")
		ALL_CHECK_USERNAME = false;
		return false;
	} else {
		MyBlog.ajax("/userCheck/checkUserName/"+username,null,"POST",function(data){
			if(data.code == 1){
				ALL_CHECK_USERNAME = true;
				$(".error-username").addClass("hidden")
			}else{
				ALL_CHECK_USERNAME = false;
				$(".error-username").removeClass("hidden")
				$(".error-username").html("账号已存在!")
			}
		});
		return ALL_CHECK_USERNAME;
	}
}

function checkPhone(phone) {
	if (phone == '') {
		$(".error-phone").removeClass("hidden")
		$(".error-phone").html("手机不能为空!")
		ALL_CHECK_PHONE = false;
		return false;
	}
	// 手机格式是否正确
	var param = /^1[3456789]\d{9}$/;
	if (!param.test(phone)) { //如果不正确,则追加提示,隐藏验证码发送框
		$(".error-phone").removeClass("hidden")
		$(".error-phone").html("手机格式不正确!")
		ALL_CHECK_PHONE = false;
		return false;
	} else { //如果正确,则修改为正常样式
		MyBlog.ajax("/userCheck/checkPhone/"+phone,null,"POST",function(data){
			if(data.code == 1){
				ALL_CHECK_PHONE = true;
				$(".error-phone").addClass("hidden")
			}else{
				ALL_CHECK_PHONE = false;
				$(".error-phone").removeClass("hidden")
				$(".error-phone").html("手机已存在!")
			}
		});
	
		
		return ALL_CHECK_PHONE;
	}
}
function checkPassword(password){
	if(password == ''){
		$(".error-password").removeClass("hidden")
		$(".error-password").html("密码不能为空哦！")
		ALL_CHECK_PASSWORD = false;
		return false;
	}
	if(password.length <6 || password.length >15){
		$(".error-password").removeClass("hidden")
		$(".error-password").html("请输入6~15位的密码哦！")
		ALL_CHECK_PASSWORD = false;
		return false;
	}else{
		$(".error-password").addClass("hidden")
		ALL_CHECK_PASSWORD = true;
		return true;
	}
}
function checkSendCode(code){
	if(code == ''){
		$(".error-code").removeClass("hidden");
		$(".error-code").html("验证码不能为空!");
		ALL_CHECK_SENDCODE = false;
		return false;
	}else{
		MyBlog.ajax("/userCheck/checkRegCode","code="+code+"&phone="+$("#phone").val(),"POST",function(data){
			if(data.code == 1){
				$(".error-code").addClass("hidden");
				ALL_CHECK_SENDCODE = true;
			}else{
				$(".error-code").removeClass("hidden");
				$(".error-code").html("验证码错误哦!");
				ALL_CHECK_SENDCODE = false;
			}
		});
		return ALL_CHECK_SENDCODE;
	}
}
$("#username").keyup(function() {
	var un = $(this).val();
	checkUserName(un);
	checkCodeBox();
});
$("#phone").keyup(function() {
	var ph = $(this).val();
	checkPhone(ph);
	checkCodeBox();
});
$("#password").keyup(function(){
	var ps = $(this).val();
	checkPassword(ps);
	checkCodeBox();
})
$("#againPassword").keyup(function(){
	var apassword = $(this).val();
	var password = $("#password").val();
	if(apassword == password){
		$(".error-apassword").addClass("hidden")
		ALL_CHECK_PASSWORD = true;
	}else{
		$(".error-apassword").removeClass("hidden")
		$(".error-apassword").html("密码不一致哦！")
		
		ALL_CHECK_PASSWORD = false;
	}
	checkCodeBox();
})
function checkCodeBox(){
	if(ALL_CHECK_PASSWORD && ALL_CHECK_PHONE && ALL_CHECK_USERNAME){
		$(".codeBox").removeClass("hidden");
	}else{
		$(".codeBox").addClass("hidden");
	}
}

$(".inputCode").keyup(function(){
	var code = $(this).val();
	checkSendCode(code);
})

$(".saveUser").click(function(){
	if(ALL_CHECK_PASSWORD && ALL_CHECK_PHONE && ALL_CHECK_USERNAME && ALL_CHECK_SENDCODE){
		MyBlog.ajax("/user/saveUser","username="+$("#username").val()+"&phone="+$("#phone").val()+"&password="+$("#password").val(),"POST",function(data){
			if(data.code == 1){
				toastr.info("注册成功!");
				window.location.href = "login.html";
			}else{
				toastr.error("服务器繁忙!");
			}
		});
	}
})