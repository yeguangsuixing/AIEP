

/*
#include <iostream>
#include <string>
#include "json/json.h" 

using namespace std;

int main(void){     
	std::string strValue = "{\"key1\":\"value1\",\"array\":[{\"key2\":\"value2\"},{\"key2\":\"value3\"},{\"key2\":\"value4\"}]}";
	Json::Reader reader;
	
	Json::Value value;
	if(reader.parse(strValue, value))     {
		string value1 = value["key1"].asString();
		cout << value1 << std::endl;
		
		string out;
		string arrayObjString = value["array"].asString();
		cout << arrayObjString << std::endl;
		/*Json::Value arrayObj;
		reader.parse(arrayObjString, arrayObj, false);

		for(int i=0; i<arrayObj.size(); i++) {
			out = arrayObj[i]["key2"].asString();
			std::cout << out;
			if(i != arrayObj.size() - 1)  
				std::cout << std::endl;      
		}  // * / 
	}
	system("pause");
	return 0;
}
//*/

//#pragma comment(lib, "json_mtd.lib") 
 
/*
#include <fstream> 
#include <cassert> 
#include "json/json.h"
using namespace std;
  
int main() 
{ 
	{
		ifstream ifs; 
		ifs.open("testjson.json"); 
		assert(ifs.is_open()); 
	  
		Json::Reader reader; 
		Json::Value root; 
		if (!reader.parse(ifs, root, false)) 
		{ 
			return -1; 
		} 
	  
		std::string name = root["name"].asString(); 
		int age = root["age"].asInt(); 
	  
		std::cout<<name<<std::endl; 
		std::cout<<age<<std::endl;
	}

	{
		//ifstream ifs;
		//ifs.open("testjson2.json"); 
		//assert(ifs.is_open()); 
		string ifs = "[{\"name\" : \"xiaoy\", \"age\" :17} , {\"name\" : \"xiaot\", \"age\" : 20}]";
	  
		Json::Reader reader; 
		Json::Value root; 
		if (!reader.parse(ifs, root, false)) 
		{ 
			return -1; 
		} 
	  
		std::string name; 
		int age; 
		int size = root.size(); 
		for (int i=0; i<size; ++i) 
		{ 
			name = root[i]["name"].asString(); 
			age = root[i]["age"].asInt(); 
	  
			std::cout<<name<<" "<<age<<std::endl; 
		}
	}



  system("pause");
    return 0; 
} 
//*/

/*
#include <fstream> 
#include <cassert> 
#include "json/json.h" 
using namespace std;
  
int main() 
{ 
    Json::Value root; 
    Json::FastWriter writer; 
    Json::Value person; 
  
    person["name"] = "hello world"; 
    person["age"] = 100; 
    root.append(person); 
  
    string json_file = writer.write(root); 
  
  
    ofstream ofs; 
    ofs.open("test1.json"); 
    assert(ofs.is_open()); 
    ofs<<json_file; 
  
    return 0; 
} 
//*/

///*
#include <fstream> 
#include <cassert> 
#include "../JsonLib/json/json.h"
using namespace std;
int main() {

/*数组创建与分析:
{
	string strValue;// = "{\"ldh\":\"001\",\"gfc\":\"002\",\"yyj\":\"003\",\"andy\":[\"005\",\"123\",\"true\"]}";
	Json::Reader read;
	Json::Value value;
	value["ldh"] = "001";
	value["gfc"] = "002";
	value["andy"].append( "005" );
	value["andy"].append( "123" );
	value["andy"].append( "true" );

	if( read.parse( strValue,value ) )
	{
		Json::Value val_array = value["andy"];
		int iSize = val_array.size();
		for ( int nIndex = 0;nIndex < iSize;++ nIndex )
		{
			cout<<val_array[nIndex]<<endl;
		}
	}
}
//*/

///*例子二:
{
	Json::Reader read;
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

	cout<<value.toStyledString()<<endl;
/*
	Json::Value val_array = value["andy"];
	int iSize = val_array.size();
	for ( int nIndex = 0;nIndex < iSize;++ nIndex )
	{
		cout<<val_array[nIndex]<<endl;
		if ( !val_array[nIndex]["andy1"].isNull() )
		{
			cout<<val_array[nIndex]["andy1"]<<endl;
		}
	}//*/
}
//*/

/*例子三:
{
	std::string strValue = "{\"name\":\"json\",\"array\":[{\"cpp\":\"jsoncpp\"},{\"java\":\"jsoninjava\"},{\"php\":\"support\"}]}";  
	Json::Value value;
	Json::Reader read;
	if ( !read.parse( strValue,value ) )
	{
		return -1;
	}
	cout<<value.toStyledString()<<endl;
	Json::Value val_array = value["array"];
	int iSize = val_array.size();

	for ( int nIndex = 0;nIndex < iSize;++ nIndex )
	{
		cout<<val_array[nIndex]<<endl;
		if ( val_array[nIndex].isMember( "cpp" ) )
		{
			cout<<val_array[nIndex]["cpp"]<<endl;
		}
	}
}

//*/

getchar();

return 0;

}

