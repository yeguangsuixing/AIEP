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
����Ӣ��������׽�����������������~~
</p>
<p>
�����أ� ��л��Ҷ����ƽ̨������֧�֣����Ǵ�ҵ�֧�֣����������ƽ̨�Ĳ��Ͻ�ȫ�����汾�Ĳ��ϱ䶯�����ʼ��v0.1beta��v0.1.1����v0.1.2beta����v0.1.2����v0.1.2fixed��
�ٵ����ڵ�v0.1.3������Ҵ����˺ܶ��鷳���ڴ˱�ʾ��Ǹ��
</p>

<p>�տ�ʼ����֮ʱ������Ϊ����һ��so easy�Ĺ��̡������ŷ��֣�ÿһ���汾������һ��Ѫ��ʷ��>_<��</p>
<h4>AIEPv0.1beta</h4>
<p>Ӧ�ú�����֪����AIEPv0.1beta��ͼ����һ������Ϊ��ɫ���ļ��С�v0.1beta�У�����ɫ��ֻ�֡����������͡��ͻ��ˡ������ҡ��ͻ��ˡ���ѡ��ť���ǻҵģ���Ҳ�������ڵġ�Judge���͡�Site����������û��ʲô��Arena�����������汾�Ѿ��ܹ����˻���ս���͡�������ս���ˣ�����ͬʱ֧��Java��C++��ս��˵�����������ԣ������롱���Ǽ����۵����顣ƽ̨������Javaд�ģ����ɺܼ򵥣�Java����delete����һ�´���ҵ��ʱ��MFC�����ˣ�һֱ��������������PPT�ϡ�����ʹ������һ�����ԡ���C++�ض�����Ҫ�Ľ�ɫ�������ܲ�֧��C++��~����Java����C++���������ڿ��ҵİɡ�����û�취����Ȼ��Ӧ��ƽ̨�ˣ���������ʦ��ֻ��ɹ�������ʧ�ܡ���һֱ�����ڶ��ߣ�ֻҪ��ͷ�ڸ�������֮�С����ðɣ�Socketͨ���Ǹ��ö���������ʵ��û��д����˫��ѹ����֪���Է������Ǹ�ʲô�������ͺ����㲻֪����Ķ��ѵ����ǲ��Ǽ����СP��һ����Socketͨ��ȷʵ�Ǹ��ö�����coding��ʱ�򻹿��Դ�һ��Client Socket����Server Socket�������е��ԡ�̫���ˣ�Socket������������Բ�ͨ�����⣬���ܹ�������ڱ�̵�ʱ����е��ԡ�</p>
<p>��Ȼ��ˣ��ǾͿ�ʼ���ְɡ��������⡭�������⡭�����ţ�Java���Socketͨ�ſ��Դ�����󣬵���Ҫ����һ������Ҳ��Java���ܷ����л���~sigh����ô�죡����һ��İ취���ַ������ã��Ǿ����ַ���ֱ�Ӵ��ݶ�����Ϣ�����շ��ٽ����ַ������ַ���������Ϣ���ǵ�Ȼ��JSON��㣨����׶������������̰ٶ�֮�����ڳ��õĶ��������ﻹ�Ǻܸ����ģ���json��˵����javascript�еĶ�������飬���������ֽṹ���Ƕ��������2�ֽṹ��ͨ�������ֽṹ���Ա�ʾ���ָ��ӵĽṹ�������ѵ�Ҫ���Լ�д����ô���Ų��ɣ��ٿ����ٶ��������ŵڶ��ı���JSON��������ȥһ��������ͦ�����ģ��������԰汾��JSON��~download֮��Java�汾�ĺ�˳����C++���û��ô���ˣ���������ʹ�ã�̫�ò���Cܳ�ˣ�������������������DEBUG��������������������OK�����첻���������ţ����������~~���ǵ��ǣ�Ҫ�ڴ�ҵĹ�����������ô��.h��.CPPô��̫��רҵ�˰ɰɣ����ǣ�һ�ֽ�����̬���ӿ�Ķ���ð�����ˡ������ٶ�֮��ͼ��ݽ���֮~~�������ϵ�һ������~һ����һ��~Ȼ����������������JSONC++������Ǹ���ǿ��С�����~ʼ�ղ���ͨ������~~����ת�򡰾�̬���ӿ⡱��shit�������Ǹ�ʲô����~~��֮��֮~��һ����������������ӷ�ײ��������ӵ���~��������������������~���첻���ң��ұز������죡JSON������̬��ɹ�������Ӧ�ò��ỹ��ʲô����˰ɣ���ʱ�򣬸�����ϲ�Э����~~ͻȻ��������ʵ��ȷʵ�Ǹ��ö�����~~~easy�������ֱ��������~~~~~~~~˳����·~~~~~���ˣ�Java��C++����û������~~~�������������롰����ģʽ��������������������Ȼ���������ɵ�jar��exe��������~~~</p>
<p>
�ȿȣ��ص�v0.1beta����������汾���һ�û��ϸ������ҵ��PPT�ϵ�Ҫ��Ū�����ġ���ʱŪ��֮������Ϊ��������ˣ�����Ѷ����ԺͶ���ʽ�Ĺ��ܲ��ϣ���ȷʵ�ǵ���ô��Ϊ�ģ�������v0.1beta����֮�󣬾Ͱ����ƽ̨����һ�ߣ�����ȥ�ˡ��ǵ�����汾��5��3��������5��9�ţ�����֮��С��С�ĵļ��죬���˴��һ�����ڵ�ʱ�䡣���ڵ�ʱ��˵��һ��������ĺܳ��ܳ����������������ã�������~�������ǵ�ʱ���뷨��
</p>

<h4>AIEPv0.1.1</h4>
<p>
1.  ������Ե�������鲻��ʤ���⡣ <br />
2.  ������������ʾ�ص����⡣ <br />
3.  ���Javascript �Լ�Python�汾�� <br />
</p>
<h4>AIEPv0.1.2beta</h4>
<p>
1.  �����ϴ�ģʽ��֧��Java/Javascript/Python����δ֧��C/C++�� <br />
2.  ����DLL·�����⣬����DLLֱ��ʹ�þ��Ե�ַ��<br />
</p>
<h4>AIEPv0.1.2</h4>
<p>
1.  ��һ�����ơ��ϴ���ģʽ��֧��C/C++�� <br />
2.  ����������·�������ո��޷��������������⣻<br />
</p>
<h4>AIEPv0.1.2fixed</h4>
<p>
1.  �޸�64λJava ������޷��������⣻ <br />
2.  �޸�FIR-AI���ؿղ����������쳣���⡣ <br />
3.  Arena��ɫ�����ظ�������ʾ���ڡ�<br />
</p>
<h4>AIEPv0.1.3</h4>
<p>
1.  �޸�v0.1.2fixedServerĿ¼���ް�װ�ļ�(install.bat��uninstall.bat)���⡣<br />
2.  ���C#�Լ�VB.NET���ǹٷ����汾��<br />
3.  �����ҳ������а��֧�֡�<br />
</p>

<br /><br />
<p>
ygsx<br />
2013��7��8��4:46:55</p>
<br /><br /><br />
  </div>
</div>

</body>
</html>
