package com.njucs.aiep;

import java.text.SimpleDateFormat;




/**
 * AIEP Language Class
 * 
 * @author ygsx
 * 
 * @version 0.1
 * 
 * @time 2013Äê5ÔÂ3ÈÕ21:01:56
 * 
 * */
public class Language {
	

	public final static String MAIN_TITLE = "NJUCS - Articial Intelligence Experiment Platform";
	public final static String MAIN_TITLE_WITH_EXP = "NJUCS - AIEP - {expname}";

	public final static String MSGBOX_TITLE = "Attention  ";
	public final static String MSGBOX_NO_OFFER = "This plugin doesn't offer this item!";
	public final static String MSGBOX_SET_FAILED = "Setting failed!";
	public final static String MSGBOX_UPLOAD_FAILED = "Uploading failed!";
	public final static String MSGBOX_UPLOAD_FAILED_AS_EXIST = "Uploading failed! The AI has already existed! \nTwo AIs are seen as the same with the same id&version!";
	public final static String MSGBOX_UPLOAD_FAILED_AS_JAVA_CLASS_SAME = "Uploading failed! The same java class!\nPlease make the AI Class Name unique!";
	public final static String MSGBOX_LOAD_FAILED_BY_TWICE = "Loading the AI File failed!\n( Each execution is allowed to load once, please reexecute the program! )";
	public final static String MSGBOX_UPLOAD_FAILED_AS_AI = "Uploading failed!( Cannot find the ai class ) ";
	public final static String MSGBOX_UPLOAD_OK = "Uploading succeed!";

	public final static String EXECUTE_SUCCEED = "Executing succeed!";
	public final static String EXECUTE_FAILED = "Executing failed!";
	
	public final static String MENU_SYS = "   System ";
	public final static String MENU_SYS_ROLE = "Choose Role";
	public final static String MENU_SYS_ROLE_AIA = "Arena ";
	public final static String MENU_SYS_ROLE_AIJ = "Judge ";
	public final static String MENU_SYS_ROLE_AIS = "Site  ";
	public final static String MENU_SYS_CHOOSE = "Choose Exp";
	public final static String MENU_SYS_EXIT = "Exit";
	public final static String MENU_SET = "   Set    ";
	public final static String MENU_SET_START = "(Re)Start ";
	public final static String MENU_SET_STOP = "Stop  ";
	public final static String MENU_SET_BASIC = "Basic Set  ";
	public final static String MENU_SET_EXT = "Ext Set  ";
	public final static String MENU_VIEW = "   View   ";
	public final static String MENU_VIEW_ARENA_RANKING_LIST = "Arena Ranking List";
	public final static String MENU_VIEW_ARENA_STATE_LIST = "Arena State List";
	public final static String MENU_VIEW_STYLE = "Style";
	public final static String MENU_VIEW_WIND = "Window";
	public final static String MENU_HELP = "   Help   ";

	public final static String MENU_HELP_HELP = "Help     ";
	public final static String MENU_HELP_ABOUT = "About    ";

	public final static String MENU_VIEW_WIND_STATUSBAR = "Status Bar   ";

	public static String MENU_VIEW_WIND_PROMPT_DLG = "Prompt Dialog   ";

	public static String STATUS_CHOOSE = "You chose <%s>!";

	//public final static String TIP_DLG_SET_IP = "IP Set(Remember to restart!)";
	//public final static String TIP_DLG_SET_PORT = "Port Set(Remember to restart!)";
	public final static String DLG_BASIC_AIA_TITLE = "AI Arena Basic Conf";
	public final static String DLG_BASIC_AIS_TITLE = "AI Site Basic Conf";
	public final static String DLG_BASIC_AIJ_TITLE = "AI Judge Basic Conf";
	public final static String DLG_EXT_AIA_TITLE = "AI Arena Extending Conf";
	public final static String DLG_EXT_AIS_TITLE = "AI Site Extending Conf";
	public final static String DLG_EXT_AIJ_TITLE = "AI Judge Extending Conf";

	//BC - Basic Conf
	public final static String DLG_BC_CONN_AIJ = "Conn to AI Judge        ";
	public final static String DLG_BC_CONN_AIA = "Conn to AI Arena         ";
	public final static String DLG_BC_AIJ_IP_LABEL = "Judge IP          ";
	public final static String DLG_BC_AIJ_PORT_LABEL = "Judge Port          ";
	public final static String DLG_BC_AIA_IP_LABEL = "Arena       IP    ";
	public final static String DLG_BC_AIA_ONLINE_PORT_LABEL = "Arena Online Port   ";
	public final static String DLG_BC_AIA_RELEASE_PORT_LABEL = "Arena Release Port ";
	public final static String DLG_BC_AIA_UPLOAD_PORT_LABEL = "AI File Upload Port ";
	public final static String DLG_BC_AIA_UPLOAD_PATH_LABEL = "AI File Upload Path ";
	public final static String DLG_BC_AIA_RECEIVE_PORT_LABEL = "Judge Report Port ";
	public final static String DLG_BC_RUN_IN_SANDBOX = "         Run in Sanbox                                                            ";
	public final static String DLG_BC_UPLOAD = "Upload Mode";
	public final static String DLG_BC_ONLINE = "Online Mode";
	public final static String DLG_BC_RELEASE = "Release the Competitions";
	public final static String DLG_BC_AI_FILE = "AI File";
	public final static String DLG_BC_SCAN_AI_FILE = "Scan";
	public final static String DLG_BC_BTN_START = "(Re)Start";
	public final static String DLG_BC_BTN_APPLY = "Apply";
	public final static String DLG_BC_BTN_UPLOAD = "Upload";
	public final static String DLG_BC_BTN_CANCEL = "Cancel";

	public final static String DLG_RANK_TITLE = "Ranking List";
	public final static String DLG_PROMPT_TITLE = "Prompt List";
	public final static String DLG_COMPETITION_TITLE = "Competition State";
	public final static String STR_UPDATE_TIME = "update time: %s";
	public final static SimpleDateFormat STR_TIME_FORMAT = 
		new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	
	public final static String TABLE_UNIT_HIATUS = "[HIATUS]";
	
	public final static String MSG_HELP_TITLE = "Help";
	public final static String MSG_HELP_CONTENT = 
		"How to start an experiment?\n" +
		"1. Choose a role: [MENU]System->Choose Role->Arena/Judge/Site\n" +
		"2. Choose an exp: [MENU]System->Choose Exp->your Exp\n" +
		"3. Start the exp: [MENU]Set->Start\n" +
		"\n" +
		"For more, please read the experiment manual.\n" +
		"\n";
	
	public final static String MSG_ABOUT_TITLE = "About";
	public final static String MSG_ABOUT_CONTENT = 
		"\n" +
		"Welcome to NJUCS-AIEP(Department of Computer Science and Technology @Nanjing University, Aritifical Intelligency Experiment Platform)!\n" +
		"\n" +
		"Version: 0.1.4\n" +
		"Developer: ygsx\n" +
		"E-mail: njucs_aiep@163.com\n" +
		"\n" +
		"Plugin Version:\n" +
		"%s\n";
	
	
	/**
	 * prohibit instantiating
	 * */
	private Language(){}
	
}






