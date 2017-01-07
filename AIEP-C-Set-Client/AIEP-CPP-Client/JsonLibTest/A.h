
#ifndef  _A_H_
#define _A_H_

#include <fstream> 
#include <cassert> 

using namespace std;
#include "../JsonLib/json/json.h"



#ifdef _DEBUG
#pragma comment(lib, "../Debug/JsonLib.lib")
#else
#pragma comment(lib, "../Release/JsonLib.lib")
#endif

class A{
public:
	A(){}
	~A(){}
	void show(){
		Json::Value value;
		value["ldh"] = "001";
		value["gfc"] = "002";
		Json::Value item;
		Json::Value array;
		item["andy1"] = "005";
		array.append( item );
		item["andy1"] = "123";
		array.append( item );
		item["andy1"] = "true";
		array.append( item );
		value["andy"] = array;

		printf( "infoValueString=%s\n", value.asCString() );
	}
};


#endif //_A_H_