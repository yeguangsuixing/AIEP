
#include <iostream>
#include "../DllClass/person.h"

#ifdef _DEBUG
#pragma comment(lib, "../Debug/DllClass.lib")
#else
#pragma comment(lib, "../Release/DllClass.lib")
#endif

using namespace std;

int main(int argc, char* argv[])
{
	
	CPerson person("zhang", 23);
	cout<<person.getName()<<endl;
	getchar();
	return 0;
}