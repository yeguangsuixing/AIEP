package com.njucs.aiep.game;

import java.util.AbstractList;
import java.util.LinkedList;


/**
 * @author ygsx
 * @created 2013Äê6ÔÂ2ÈÕ16:02:59
 * */
public class GameTree {
	
	private GameTree parent = null;
	private AbstractList<GameTree> children = new LinkedList<GameTree>();
	

	private String name;
	private String value;
	private Status status = Status.EMPTY;
	
	public GameTree(){}
	
	public GameTree(String name, String value) { 
		this.setName(name);
		this.setValue(value);
	}
	public void addChild(GameTree childTree){
		if( childTree == null ) return;
		this.children.add(childTree);
		childTree.parent = this;
	}
	
	public void removeChild(GameTree childTree){
		children.remove(childTree);
	}

	public AbstractList<GameTree> getTreeChildren() {
		return children;
	}

	/**
	 * @return the parent
	 */
	public GameTree getParent() {
		return parent;
	}
	
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	protected int getHeight(){
		return getHeight( this );
	}
	
	protected int getWidth(){
		return getWidth(this);
	}
	
	private static int getWidth(GameTree node){
		if( node == null ) return 0;
		if( node.children.size() == 0 ) return 1;
		int m = 0;
		for( GameTree child : node.children ){
			int w = getWidth( child );
			if( w > m ){
				m = w;
			}
		}
		return m*node.children.size();
	}
	
	private static int getHeight(GameTree node){
		if( node == null ) return 0;
		if( node.children.size() == 0 ) return 0;
		int m = 0;
		for( GameTree child : node.children ){
			int h = getHeight( child );
			if( h > m ){
				m = h;
			}
		}
		return m + 1;
	}
	
}
