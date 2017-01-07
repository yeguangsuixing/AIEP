/**************************************
AIEPP FIR Game Tree Head
@author tqc
@time 2013Äê6ÔÂ8ÈÕ14:39:37
****************************************/

#ifndef _FIR_GAMETREE_H_
#define _FIR_GAMETREE_H_

#include "DataTransmit/std.h"

#include <iostream>
#include <list>
using namespace std;

class GameTree {
	
	
private:
	const static int MAX_LEN = 20;
	GameTree* parent;
	list<GameTree*>* children;
	char name[MAX_LEN];
	char value[MAX_LEN];
	Status status;
	
public:
	GameTree(){
		parent = NULL;
		children = new list<GameTree*>();
		status = EMPTY;
	}
	~GameTree(){
		removeAllChildren();
		if( children != NULL ){
			delete children;
			children = NULL;
		}
	}
	
	GameTree(char* name, char* value) {
		parent = NULL;
		children = new list<GameTree*>();
		status = EMPTY;
		this->setName(name);
		this->setValue(value);
	}
	void addChild(GameTree* childTree){
		if( childTree == NULL ) return;
		this->children->push_back(childTree);
		childTree->parent = this;
	}
	
	void removeChild(GameTree* childTree){
		if( childTree == NULL ) return;
		list<GameTree*>* chlist = childTree->children;
		if( chlist != NULL ){
			list<GameTree*>::iterator iter = chlist->begin();
			for( ; iter != chlist->end(); iter ++ ){
				(*iter)->removeAllChildren();
				delete (*iter);
			}
		}
		children->remove(childTree);
		delete childTree;
	}
	void removeAllChildren(){
		if( this->children != NULL && this->children->size() > 0 ){
			list<GameTree*>::iterator iter = this->children->begin();
			for( ; iter != this->children->end(); iter ++ ){
				(*iter)->removeAllChildren();
				delete (*iter);
			}
		}
	}

	list<GameTree*>* getTreeChildren() {
		return children;
	}

	/**
	 * @return the parent
	 */
	GameTree* getParent() {
		return parent;
	}
	
	
	/**
	 * @param status the status to set
	 */
	void setStatus(Status status) {
		this->status = status;
	}
	/**
	 * @return the status
	 */
	Status getStatus() {
		return status;
	}
	/**
	 * @param value the value to set
	 */
	void setValue(char* value) {
		if( value == NULL ){
			this->value[0] = '\0';
			return;
		} else {
			strncpy_s(this->value, value,MAX_LEN);
		}
	}
	/**
	 * @return the value
	 */
	char* getValue() {
		return value;
	}
	/**
	 * @param name the name to set
	 */
	void setName(char* name) {
		if( name == NULL ){
			this->name[0] = '\0';
			return;
		} else {
			strncpy_s(this->name, name,MAX_LEN);
		}
	}
	/**
	 * @return the name
	 */
	char* getName() {
		return name;
	}
	
	int getHeight(){
		return getHeight( this );
	}
	
private:
	static int getHeight(GameTree* node){
		if( node == NULL ) return 0;
		if( node->children->size() == 0 ) return 0;
		int m = 0;
		list<GameTree*>::iterator iter;
		for( iter = node->children->begin(); iter != node->children->end(); iter ++ ){
			int h = getHeight( *iter );
			if( h > m ){
				m = h;
			}
		}
		return m + 1;
	}
	
};


#endif