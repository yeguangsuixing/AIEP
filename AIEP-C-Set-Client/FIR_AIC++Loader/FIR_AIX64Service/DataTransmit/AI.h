/**************************************
AIEP AI Head
@author tqc
@time 2013Äê5ÔÂ9ÈÕ20:51:37
****************************************/


#ifndef _AI_H_
#define _AI_H_

class AI {
public:
	/**
	 *  <p>Return your AI id.</p>
	 * @since AIEPv0.1beta
	 * */
	virtual char* getId() = 0;
	
	/**
	 *  <p>Return your AI name.</p>
	 * @since AIEPv0.1beta
	 * */
	virtual char* getName() = 0;

	/**
	 *  <p>Return your AI nickname.</p>
	 * @since AIEPv0.1beta
	 * */
	virtual char* getNickname() = 0;

	
	/**
	 *  <p>Return your AI version.</p>
	 * @since AIEPv0.1
	 * */
	virtual char* getVersion() = 0;
	
	/**
	 * Do u want to print the receive info? Please return false if not. 
	 * 
	 * @return <code>true</code>, if u want to print the recv info, 
	 * <code>false</code> othersize 
	 * @since AIEPv0.1beta
	 * */
	virtual bool isPrintInfo() = 0;
};


#endif //_AI_H_