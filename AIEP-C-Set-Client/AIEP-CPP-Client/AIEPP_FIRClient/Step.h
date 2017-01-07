/**************************************
Step  Head
@author tqc
@time 2013Äê5ÔÂ9ÈÕ20:51:37
****************************************/

#ifndef _STEP_H_
#define _STEP_H_

#include "AIEPP_FIR.h"


class DLLEXPORT Step {
private:
	int x, y;
	Status status;
	int usedTime;


public:
	Step(){
		Step(EMPTY, 0, 0);
	}
	Step(Status status, int x, int y){
		this->status = status;
		this->x = x;
		this->y = y;
	}
	/**
	 * @return the x
	 */
	int getX() const {
		return x;
	}
	/**
	 * @return the y
	 */
	int getY() const {
		return y;
	}
	/**
	 * @return the status
	 */
	Status getStatus() const {
		return status;
	}
	/**
	 * @param x the x to set
	 */
	void setX(int x) {
		this->x = x;
	}
	/**
	 * @param y the y to set
	 */
	void setY(int y) {
		this->y = y;
	}
	/**
	 * @param status the status to set
	 */
	void setStatus(Status status) {
		this->status = status;
	}

	/**
	 * @param usedTime the usedTime to set
	 */
	void setUsedTime(int usedTime) {
		this->usedTime = usedTime;
	}

	/**
	 * @return the usedTime
	 */
	int getUsedTime() const {
		return usedTime;
	}


};











#endif