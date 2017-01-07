package com.ygsx.debug;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;

public class Test {

	public static void main(String[] args){
		Test test = new Test();
		VirtualMachine vm = test.launchTarget("");
		EventRequestManager mgr = vm.eventRequestManager(); 
		// ע���쳣�¼�
		ExceptionRequest excReq = mgr.createExceptionRequest(null, true, true); 
		excReq.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD); 
		excReq.enable(); 
		// ע��������¼�
		MethodEntryRequest menr = mgr.createMethodEntryRequest(); 
		menr.setSuspendPolicy(EventRequest.SUSPEND_NONE); 
		menr.enable(); 
		// ע��������¼�
		MethodExitRequest mexr = mgr.createMethodExitRequest(); 
		mexr.setSuspendPolicy(EventRequest.SUSPEND_NONE); 
		mexr.enable(); 
		// ע���߳������¼�
		ThreadStartRequest tsr = mgr.createThreadStartRequest(); 
		tsr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD); 
		tsr.enable(); 
		// ע���߳̽����¼�
		ThreadDeathRequest tdr = mgr.createThreadDeathRequest(); 
		tdr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD); 
		tdr.enable();
		
		
		
	}
	
	public void run() { 
	    EventQueue queue = vm.eventQueue(); 
	    while (connected) { 
	        try { 
	            EventSet eventSet = queue.remove(); 
	            EventIterator it = eventSet.eventIterator(); 
	            while (it.hasNext()) { 
	                handleEvent(it.nextEvent()); 
	            } 
	            eventSet.resume(); 
	        } catch (InterruptedException exc) {// Ignore 
	        } catch (VMDisconnectedException discExc) { 
	            handleDisconnectedException(); 
	            break; 
	        } 
	    } 
	} 
	
	LaunchingConnector findLaunchingConnector() {
	    List connectors = Bootstrap.virtualMachineManager().allConnectors();
	    Iterator iter = connectors.iterator();
	    while (iter.hasNext()) {
		    Connector connector = (Connector) iter.next();
		    if ("com.sun.jdi.CommandLineLaunch".equals(connector.name())) {
	            return (LaunchingConnector) connector;
	        }
	    }
	    return null;
	}
	

	
	/**������
	* connectorΪ�嵥1.�л�ȡ��Connector����ʵ��
	* mainArgsΪĿ�����main�������ڵ���
	**/
	Map connectorArguments(LaunchingConnector connector, String mainArgs) {	
	    Map arguments = connector.defaultArguments();	
	    Connector.Argument mainArg = (Connector.Argument) arguments.get("main");	
	    if (mainArg == null) {		
	        throw new Error("Bad launching connector");	
	    }
	    mainArg.setValue(mainArgs);	
	    return arguments;
	}

	
	/**������
	* mainArgsΪĿ�����main�������ڵ���
	**/
	VirtualMachine launchTarget(String mainArgs) {
	    //findLaunchingConnector����ȡ����
	    LaunchingConnector connector = findLaunchingConnector();
	    //connectorArguments���������Ӳ���
	    Map arguments = connectorArguments(connector, mainArgs);
	    try {		
	        return connector.launch(arguments);//��������	
	    } catch (IOException exc) {		
	        throw new Error("Unable to launch target VM: " + exc);	
	    } catch (IllegalConnectorArgumentsException exc) {
	        throw new Error("Internal error: " + exc);	
	    } catch (VMStartException exc) {
	        throw new Error("Target VM failed to initialize: " + exc.getMessage());
	    }
	}
	
	
}

















