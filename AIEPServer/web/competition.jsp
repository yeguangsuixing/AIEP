<%@  page import="java.util.*" %>
<%@ page import="com.njucs.aiep.plugin.*" %>
<%@ page import="com.njucs.aiep.*" %>
<%@ page import="com.njucs.aiep.frame.*" %>
<%@ page import="com.njucs.aiep.frame.Competition" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
<title>Competition List</title>
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
	<span><a href="index.jsp">Index</a></span>
     <span><a href="ranking.jsp">Ranking List</a></span> 
	 <span style="font-weight:600;font-size:16px">Competition List</span>

<% 
List<Competition> list = AIEP.aiep.getCompetitionList();
if( list != null ) {
	int a = 1;
	%>
	
    <table cellspacing="0" cellpadding="0">
      <tr>
		<th>Index</th>
        <th>Offensive</th>
        <th>Defensive</th>
        <th>Winner</th>
        <th>Time</th>
        <th>Result</th>
        <th>State</th>
      </tr>
	<%
	for( Competition competition : list ) {
		StringBuilder sb = new StringBuilder(), sb2 = new StringBuilder();
		if( competition.getStartTime() != null ){
			sb.append(Language.STR_TIME_FORMAT.format(competition.getStartTime()));
		} else {
			sb.append(Language.TABLE_UNIT_HIATUS);
		}
		sb.append("--");
		if( competition.getEndTime() != null ){
			sb.append(Language.STR_TIME_FORMAT.format(competition.getEndTime()));
		} else {
			sb.append(Language.TABLE_UNIT_HIATUS);
		}

		if( competition.getReuslt() != null && competition.getReuslt().getResultCode() != null ){
			sb2.append( competition.getReuslt().getResultCode() );
		} else {
			sb2.append( Language.TABLE_UNIT_HIATUS );
		}
		if( competition.getReuslt() != null && competition.getReuslt().getRealTimeResult() !=null ){
			sb2.append("(");
			sb2.append(competition.getReuslt().getRealTimeResult());
			sb2.append(")");
		}
	%>
		  <tr>
			<td><%=a++%></td>
			<td><%= competition.getOffensiveInfo().getNickname()+"("+competition.getOffensiveInfo().getVersion()+")" %></td>
			<td><%= competition.getDefensiveInfo().getNickname()+"("+competition.getDefensiveInfo().getVersion()+")" %></td>
			<td><%= competition.getWinner() != null?competition.getWinner().getNickname()+"("+competition.getWinner().getVersion()+")":null %></td>
			<td><%= sb.toString() %></td>
			<td><%= sb2.toString() %></td>
			<td><%= competition.getState() %></td>
		  </tr>
	<% 
	} 
} else {
	%> <p>Nothing to Display! </p><%
}
%>

    </table>

  </div>
</div>



</body>
</html>
