<%@  page import="java.util.AbstractList" %>
<%@ page import="com.njucs.aiep.plugin.*" %>
<%@ page import="com.njucs.aiep.*" %>
<%@ page import="com.njucs.aiep.frame.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
<title>Index</title>
<!-- paste this code into your webpage -->
<link href="tablecloth/tablecloth.css" rel="stylesheet" type="text/css" media="screen" />
<script type="text/javascript" src="tablecloth/tablecloth.js"></script>
<!-- end -->
<style>
body { margin:0; padding:0; background:#f1f1f1; font:70% Arial, Helvetica, sans-serif; color:#555; line-height:150%; text-align:left; }
a { text-decoration:none; color:#057fac; }
a:hover { text-decoration:none; color:#999; }
h1 { font-size:140%; margin:0 20px; line-height:80px; }
h2 { font-size:120%; }
#container { margin:0 auto; width:980px; background:#fff; padding-bottom:20px; }
#content { margin:0 20px; }
p.sig { margin:0 auto; width:680px; padding:1em 0; }
form { margin:1em 0; padding:.2em 20px; background:#eee; }
</style>
</head>
<body>
<div id="container">
  <div id="content">
	<h2>
<%= AIEP.aiep.getAbout().replace("\n", "<br />")%>
	</h2>
	<hr />
    <span style="font-weight:600;font-size:16px">Index</span>
	<span><a href="ranking.jsp">Ranking List</a></span>
	<span><a href="competition.jsp">Competition List</a></span>
<p>
鉴于英语能力会捉鸡，下面就用中文了~~
</p>
<p>
首先呢， 感谢大家对这个平台的无限支持！正是大家的支持，才有了这个平台的不断健全。而版本的不断变动，从最开始的v0.1beta到v0.1.1，到v0.1.2beta，到v0.1.2，到v0.1.2fixed，
再到现在的v0.1.3，给大家带来了很多麻烦，在此表示抱歉！
</p>

<p>刚开始下手之时，我以为这是一个so easy的工程。后来才发现，每一个版本，都是一部血泪史啊>_<。</p>
<h4>AIEPv0.1beta</h4>
<p>应该很少人知道，AIEPv0.1beta的图标是一个主调为蓝色的文件夹。v0.1beta中，“角色”只分“服务器”和“客户端”（而且“客户端”单选按钮还是灰的），也就是现在的“Judge”和“Site”，根本就没有什么“Arena”概念。但这个版本已经能够“人机对战”和“机机对战”了，而且同时支持Java与C++对战。说起这两门语言，“翻译”真是件蛋疼的事情。平台本身用Java写的（理由很简单，Java不用delete，大一下大作业的时候被MFC吓着了，一直不敢碰），但是PPT上“允许使用任意一种语言”，C++必定是重要的角色，不可能不支持C++吧~但是Java调用C++……这是在坑我的吧。。。没办法，既然答应做平台了，而且黎老师“只许成功，不许失败”还一直回旋在耳边，只要埋头于各种资料之中……好吧，Socket通信是个好东西（网络实验没白写），双方压根不知道对方到底是个什么东西，就好像你不知道你的队友到底是不是几岁的小P孩一样。Socket通信确实是个好东西，coding的时候还可以打开一个Client Socket连到Server Socket上来进行调试。太棒了！Socket不仅解决了语言不通的问题，还能够给大家在编程的时候进行调试。</p>
<p>既然如此，那就开始动手吧……噔噔噔……噔噔噔……慢着，Java里的Socket通信可以传输对象，但是要求另一方必须也是Java才能反序列化啊~sigh！怎么办！？简单一点的办法：字符串。好，那就用字符串直接传递对象信息，接收方再解析字符串。字符串保存信息，那当然是JSON最划算（简洁易读）。于是立刻百度之（对于常用的东西，度娘还是很给力的）：json简单说就是javascript中的对象和数组，所以这两种结构就是对象和数组2种结构，通过这两种结构可以表示各种复杂的结构。。。难道要我自己写个类么，才不干！再看看百度搜索，排第二的便是JSON官网，进去一看，还是挺激动的：各种语言版本的JSON库~download之后，Java版本的很顺畅，C++版就没那么简单了，各种奇葩使用（太久不用C艹了）………………漫长的DEBUG…………………………OK！皇天不负有心人呐，总算会用了~~但是但是，要在大家的工程里边添加这么多.h和.CPP么，太不专业了吧吧，于是，一种叫做动态链接库的东西冒出来了。继续百度之，图书馆借书之~~按照书上的一步步来~一步又一步~然而这网上下下来的JSONC++库就像那个倔强的小红军啊~始终不让通过编译~~于是转向“静态链接库”，shit，这又是个什么东西~~度之书之~又一个新名词塞到了这臃肿不堪的脑子的里~这回这该死的终于听话了~皇天不负我，我必不负皇天！JSON导出静态库成功，后面应该不会还有什么大坑了吧！这时候，该设计上层协议了~~突然觉得网络实验确实是个好东西啊~~~easy的事情就直接跳过了~~~~~~~~顺畅的路~~~~~好了，Java跟C++交流没问题了~~~“服务器”进入“机机模式”（名字听起来不错），然后开两个生成的jar或exe，完美啊~~~</p>
<p>
咳咳，回到v0.1beta上来。这个版本是我还没有细看大作业的PPT上的要求弄出来的。当时弄好之后，我以为这就完事了，顶多把多语言和多样式的功能补上！（确实是的这么认为的！）所以v0.1beta出来之后，就把这个平台闲置一边，赶软工去了。记得这个版本从5月3号做到了5月9号，加上之后小修小改的几天，花了大概一个星期的时间。对于当时来说，一个星期真的很长很长啊。“不过，还好，做好了~”，这是当时的想法。
</p>

<h4>AIEPv0.1.1</h4>
<p>
1.  修正边缘五子连珠不判胜问题。 <br />
2.  修正博弈树显示重叠问题。 <br />
3.  添加Javascript 以及Python版本。 <br />
</p>
<h4>AIEPv0.1.2beta</h4>
<p>
1.  完善上传模式，支持Java/Javascript/Python，尚未支持C/C++； <br />
2.  改善DLL路径问题，启动DLL直接使用绝对地址。<br />
</p>
<h4>AIEPv0.1.2</h4>
<p>
1.  进一步完善“上传”模式，支持C/C++； <br />
2.  修正服务器路径包含空格无法创建比赛的问题；<br />
</p>
<h4>AIEPv0.1.2fixed</h4>
<p>
1.  修复64位Java 虚拟机无法启动问题； <br />
2.  修复FIR-AI返回空博弈树出现异常问题。 <br />
3.  Arena角色类名重复出现提示窗口。<br />
</p>
<h4>AIEPv0.1.3</h4>
<p>
1.  修复v0.1.2fixedServer目录下无安装文件(install.bat、uninstall.bat)问题。<br />
2.  添加C#以及VB.NET（非官方）版本。<br />
3.  添加网页浏览排行榜的支持。<br />
</p>

<br /><br />
<p>
ygsx<br />
2013年7月8日4:46:55</p>
<br /><br /><br />
  </div>
</div>

</body>
</html>
