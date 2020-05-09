## 架构

采用前后端分离方式的架构，然后前端使用Ajax交互

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/37cdf1f9089b4d82a1d714f876548ea0.png) 

## 新增工具

&gt; 腾讯行为验证封装工具、阿里云短信接口封装工具、OSS图片上传、UUID工具、AES加密工具、敏感词过滤器


## 页面展示
### 注册页面（使用行为验证）

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/11776af88ba840159362d02dbdbff794.png) 

### 登录页面（使用行为验证）



![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/3d668f97dea148ff8b920534580e60f6.png) 

### 首页展示

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/681d2e87334b4feba0f35c1e09d32500.png) 

### 博客点赞

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/ecfeaf31f9da49a8a3837ae4f0f03ac4.png) 

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/d79df92a98c7456c9bd0af3498ef91a9.png) 
### 评论

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/96ad9217fb0b4814a20d392dc03e02d9.png) 

> 评论新增了回复，即评论里面有评论


![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/4d09be5f64ed4ec79003443a9845025e.png) 
点击回复时会把要回复的姓名显示
![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/6fbfe01ce7094727ba5b7632dfb4339e.png) 
> 然后对于评论还可以点赞

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/eae94c46e36d4a75be25c4daff6f9538.png) 

### 个人主页

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/16d0f79d45aa457e8c8f8e27eaaf8ddd.png) 

> 在个人主页上能显示关注的人数，以及粉丝的人数并且能进入查看

### 关注用户
进入用户主页的时候可以选择关注

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/9debeb9c21144a3f8d94c00848d411ca.png) 
查看用户的粉丝或者用户的关注对象时可以选择关注这个是仿造bilibili的关注功能来做的

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/3a8c5e021c7b492f97278316ca8149e3.png) 

### 私信
私信在“未读消息”框，当有“私信信息”和“系统信息”时则会显示消息的数量

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/d58ff1bc6f6a461ebbff8cdf95b3ce0f.png) 
然后可以点击发送私信

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/4e9be8bf925f4a2ba5fe6e60440e4681.png) 
本来发送私信的时候想要把关注的用户、粉丝给列出来然后选择给其中一个发私信，后来想一想假设发私信的对象即不是粉丝也不是关注的对象，所以直接把这个私信功能改成只要填写对方的用户名即可给其发送私信

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/b27ee067e5194a5fb6e7432bdfc593ec.png) 
进入私信可查看以往的私信内容也可继续发送私信

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/948f5e8791534422a96718e4c6fd1162.png) 

### 系统通知
一旦有系统提示的消息则一定会在未读消息处显示数量

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/e0776bf3209e4e09a9f9b645fd41e6fa.png) 
点击的时候能看到朋友之间的私信通知和系统的通知
![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/aad990c7756c4549a9530bd85daff9ac.png) 
系统的通知是关于点赞了博客或者回复了评论之类的

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/47c79fe19b424017b2d8d2cab6727709.png) 

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/ba61f2f6ff7444b9a73da6d0e9973509.png) 
点击后可详细观看内容！
* 回复的查看


![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/5bf28e3d77d04802ba767a13a5ffa0bd.png) 

* 关注的查看


![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/2d5f8983bbc34af79b6b197b8ba59fa9.png) 


### 博客置顶/加精/删除
然后有一个博客的管理者，这个身份的用户就是负责把一些质量较高的内容置顶或者加精给大家看

![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/a14257142dc44fab86a6c3961fd60869.png) 
然后后台的管理员具有删除博客的权限，这里之所以不开发给用户的原因是因为到时候扩展一个举报的功能
![](https://sontan-big-file.oss-cn-shenzhen.aliyuncs.com/blog/image/79237bca73134a1b9a11bfe35643befc.png) 

