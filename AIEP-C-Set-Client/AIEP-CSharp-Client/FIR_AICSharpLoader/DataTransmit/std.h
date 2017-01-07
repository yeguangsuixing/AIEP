/**************************************
standard head file
@author tqc
@time 2013Äê5ÔÂ9ÈÕ18:15:52
****************************************/

#ifndef _STD_H_
#define _STD_H_

#define JAVA_TIME_LEN 22

#define DLL_EXPORT __declspec(dllexport)

enum Lang { CPP, JAVA };
enum Status { OFFENSIVE,  DEFENSIVE, EMPTY };


/*
if you use VS2012 to compiler your program, 
please uncomment the line below
*/
//#define VS2012

#ifndef VS2012
#define VS2010
#endif


#endif