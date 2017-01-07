/**************************************
AIEPP FIR  Head
@author tqc
@time 2013Äê5ÔÂ9ÈÕ20:51:37
****************************************/

#ifndef _AIEPP_FIR_H_
#define _AIEPP_FIR_H_

#define DLLEXPORT __declspec(dllexport)

enum TagType { ACROSS_INFO, RUNNING, GAME_OVER };
enum WIN_TYPE { NORMAL, POSITION_ERROR, TIME_OUT };

//class AIEPP_FIRClient;

class DLLEXPORT AIEPP_FIR {
public:
	const static int MAX_LEN = 20;
	const static int DIMENSION = 15;
	const static char TAG_TYPE[MAX_LEN];
	const static char TAG_ACROSS_ID[MAX_LEN];
	const static char TAG_ACROSS_NAME[MAX_LEN];
	const static char TAG_ACROSS_NICKNAME[MAX_LEN];
	const static char TAG_TIME[MAX_LEN];
	const static char TAG_OFFENSIVE[MAX_LEN];
	const static char TAG_POSITION_X[MAX_LEN];
	const static char TAG_POSITION_Y[MAX_LEN];
	const static char TAG_TIME_USED[MAX_LEN];
	const static char TAG_WINNER[MAX_LEN];
	const static char TAG_WIN_TYPE[MAX_LEN];
	const static char TAG_HEURISTIC[MAX_LEN];
	const static char TAG_CHESS[MAX_LEN];
};




#endif