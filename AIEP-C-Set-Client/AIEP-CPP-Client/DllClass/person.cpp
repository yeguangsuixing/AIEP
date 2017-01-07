

#include <iostream>
#include "person.h"


CPerson::CPerson()
{

}
CPerson::~CPerson()
{
}
CPerson::CPerson(char *lpName,int nAge)
{
	this->age=nAge;
	if (lpName)
	{
		int len = strlen(lpName);
		if (len>127)
			len=127;
		memcpy(this->szName,lpName,len);
		this->szName[len] = 0;
	}
}
char *CPerson::getName()
{
	return szName;
}
int CPerson::getAge()
{
	return age;
}