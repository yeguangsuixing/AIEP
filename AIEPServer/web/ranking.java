package com.twzcluster.webserver.jsp;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.twzcluster.webserver.Request;
import com.twzcluster.webserver.Response;
import java.util.AbstractList;
import com.njucs.aiep.plugin.*;
import com.njucs.aiep.*;
import com.njucs.aiep.frame.*;


public class ranking/*class name*/ extends HttpServlet  {
	protected void  service( HttpServletRequest request, HttpServletResponse response )
	throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		((Response)response).send200Header();
		out.print( "\r\n" );
		out.print("");
		out.print("");
		out.print("");
		out.print("");
		out.print("");
		out.print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.print("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.print("<head>");
		out.print("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gbk\" />");
		out.print("<title>Ranking List");
		out.print("</title>");
		out.print("<!-- paste this code into your webpage -->");
		out.print("<link href=\"tablecloth/tablecloth.css\" rel=\"stylesheet\" type=\"text/css\" media=\"screen\" />");
		out.print("<script type=\"text/javascript\" src=\"tablecloth/tablecloth.js\">");
		out.print("</script>");
		out.print("<!-- end -->");
		out.print("<style>body { margin:0; padding:0; background:#f1f1f1; font:70% Arial, Helvetica, sans-serif; color:#555; line-height:150%; text-align:left; }a { text-decoration:none; color:#057fac; }a:hover { text-decoration:none; color:#999; }h1 { font-size:140%; margin:0 20px; line-height:80px; }h2 { font-size:120%; }#container { margin:0 auto; width:980px; background:#fff; padding-bottom:20px; }#content { margin:0 20px; }p.sig { margin:0 auto; width:680px; padding:1em 0; }form { margin:1em 0; padding:.2em 20px; background:#eee; }");
		out.print("</style>");
		out.print("</head>");
		out.print("<body>");
		out.print("<div id=\"container\">  ");
		out.print("<div id=\"content\">	");
		out.print("<h2>");
		out.print( AIEP.aiep.getAbout().replace("\n", "<br />"));
		out.print("	");
		out.print("</h2>	");
		out.print("<hr />	");
		out.print("<span>");
		out.print("<a href=\"index.jsp\">Index");
		out.print("</a>");
		out.print("</span>    ");
		out.print("<span style=\"font-weight:600;font-size:16px\">Ranking List");
		out.print("</span>	");
		out.print("<span>");
		out.print("<a href=\"competition.jsp\">Competition List");
		out.print("</a>");
		out.print("</span>");
		 
AbstractList<AIInfo> list = AIEP.aiep.getRankingList();
if( list != null ) {
	int a = 1;
	
		out.print("	    ");
		out.print("<table cellspacing=\"0\" cellpadding=\"0\">      ");
		out.print("<tr>		");
		out.print("<th>Index");
		out.print("</th>        ");
		out.print("<th>ID");
		out.print("</th>        ");
		out.print("<th>Nickname");
		out.print("</th>        ");
		out.print("<th>Version");
		out.print("</th>        ");
		out.print("<th>Source");
		out.print("</th>        ");
		out.print("<th>Upload Time");
		out.print("</th>      ");
		out.print("</tr>	");
		
	for( AIInfo info : list ) {
	
		out.print("		  ");
		out.print("<tr>			");
		out.print("<td>");
		out.print(a++);
		out.print("");
		out.print("</td>			");
		out.print("<td>");
		out.print( info.getId() );
		out.print("");
		out.print("</td>			");
		out.print("<td>");
		out.print( info.getNickname() );
		out.print("");
		out.print("</td>			");
		out.print("<td>");
		out.print( info.getVersion() );
		out.print("");
		out.print("</td>			");
		out.print("<td>");
		out.print( info.getSrcFileName() );
		out.print("");
		out.print("</td>			");
		out.print("<td>");
		out.print( info.getFileRecvTime() );
		out.print("");
		out.print("</td>		  ");
		out.print("</tr>	");
		 
	} 
} else {
	
		out.print(" ");
		out.print("<p>Nothing to Display! ");
		out.print("</p>");
		
}

		out.print("    ");
		out.print("</table>  ");
		out.print("</div>");
		out.print("</div>");
		out.print("</body>");
		out.print("</html>");
		out.close();
	}
}
