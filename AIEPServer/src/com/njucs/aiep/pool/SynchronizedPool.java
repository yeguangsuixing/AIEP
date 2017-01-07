package com.njucs.aiep.pool;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.njucs.aiep.pool.CompareOverEvent.CompareResult;


/**
 * Synchronized Pool
 * 
 * @author ygsx
 * @created 2013Äê6ÔÂ4ÈÕ22:10:26
 * */
public class SynchronizedPool<E extends Serializable> implements ISynchronizedPool<E> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7539007135936232116L;
	//data
	protected List<E> sortedInfo = Collections.synchronizedList(new LinkedList<E>());
	//extra data
	protected List<Compare<E>> compareList = Collections.synchronizedList(new LinkedList<Compare<E>>());
	
	//event drive
	protected transient InsertEventListener<E> insertListener = null;
	protected transient CompareOverListener<E> compareOverListener = null;
	
	protected transient CompareEventListener<E> compareListener = null;
	protected transient LocateEventListener<E> locateEventListener = null;
	
	public SynchronizedPool(){
		insertListener = new SynPoolInsertListener();
		compareOverListener = new SynPoolOverListener();
	}
	/* (non-Javadoc)
	 * @see com.njucs.aiep.pool.ISynchronizedPool#initialize()
	 */
	public void initialize(){
		if( insertListener == null ){
			insertListener = new SynPoolInsertListener();
		}
		if( compareOverListener == null ) {
			compareOverListener = new SynPoolOverListener();
		}
	}
	
	/**
	 * get the new info into the sorted info list
	 * */
	protected E getMiddle( E beginInfo, E endInfo){
		synchronized(sortedInfo){
			int beginIndex = sortedInfo.indexOf(beginInfo), endIndex = sortedInfo.indexOf(endInfo);
			if( beginIndex == -1 ) {
				throw new IllegalArgumentException( "beginInfo is not found!" );
			} else if( endIndex == -1 ) {
				throw new IllegalArgumentException( "endInfo is not found!" );
			}
			if( beginIndex > endIndex ) return null;
			int middle = (beginIndex+endIndex)/2;
			if( middle < 0 || middle >= sortedInfo.size() ) {
				//throw new Exception( "Some unknown fatal error(s) occur(s)!" );
				return null;
			}
			return sortedInfo.get(middle);
		}
	}
	private E getMiddle( int beginIndex, int endIndex){
		//synchronized(sortedAIInfo){
			if( beginIndex == -1 || endIndex == -1 || beginIndex > endIndex ) return null;
			int middle = (beginIndex+endIndex)/2;
			if( middle < 0 || middle >= sortedInfo.size() ) return null;
			return sortedInfo.get(middle);
		//}
	}
	protected AbstractList<E> getMiddleLeft(E beginInfo, E middleInfo){
		synchronized(sortedInfo){
			int beginIndex = sortedInfo.indexOf(beginInfo), midIndex = sortedInfo.indexOf(middleInfo);
			if( beginIndex == -1 ) {
				throw new IllegalArgumentException( "beginInfo is not found!" );
			} else if( midIndex == -1 ) {
				throw new IllegalArgumentException( "middleInfo is not found!" );
			}
			E e =  getMiddle( beginIndex, midIndex - 1 );
			if( e == null ) return null;
			AbstractList<E> t = new ArrayList<E>();
			t.add(beginInfo);//uniform
			t.add( sortedInfo.get(midIndex-1) );
			return t;
		}
	}
	protected AbstractList<E> getMiddleRight(E endInfo, E middleInfo){
		synchronized(sortedInfo){
			int endIndex = sortedInfo.indexOf(endInfo), midIndex = sortedInfo.indexOf(middleInfo);
			if( endIndex == -1 ) {
				throw new IllegalArgumentException( "endInfo is not found!" );
			} else if( midIndex == -1 ) {
				throw new IllegalArgumentException( "middleInfo is not found!" );
			}
			E e = getMiddle( midIndex+1, endIndex );
			if( e == null ) return null;
			AbstractList<E> t = new ArrayList<E>();
			t.add( sortedInfo.get(midIndex+1) );
			t.add( endInfo );//uniform
			return t;
		}
	}
	
	/**
	 * insert a new element into the pool
	 * @param newInfo the elem will be inserted
	 * @return null - if successful when size = 0,  a list with 2 elements 
	 * which are the head elem&tail elem otherwize
	 * */
	protected AbstractList<E> insert(E newInfo){
		if( newInfo == null ){
			throw new IllegalArgumentException( "newInfo is null!" );
		}
		synchronized(sortedInfo){
			if( sortedInfo.size() == 0 ){
				sortedInfo.add(newInfo);
				return null;//successful
			} else {
				AbstractList<E> t = new ArrayList<E>();
				t.add( sortedInfo.get(0) );
				t.add( sortedInfo.get( sortedInfo.size() - 1 )  );
				return t;
			}
		}
	}
	
	protected AbstractList<E> insertBefore( E firstInfo, E relativeInfo, E newInfo ){
		
		if( relativeInfo == null ){
			throw new IllegalArgumentException( "relativeInfo can not be  null!" );
		}
		synchronized(sortedInfo){
			if( firstInfo == null ) {//insert before the old head
				int relativeIndex = sortedInfo.indexOf(relativeInfo);
				if( relativeIndex == -1 ) {
					throw new IllegalArgumentException( "relativeInfo is not found!" );
				}
				E headInfo = sortedInfo.get(0);
				if( headInfo == relativeInfo ) {
					sortedInfo.add(0, newInfo);//insert succeed!
					return null;
				} else {
					AbstractList<E> t = new ArrayList<E>();
					t.add( sortedInfo.get(0) );
					t.add( sortedInfo.get(relativeIndex-1)  );
					return t;
				}
			}
			int firstIndex = sortedInfo.indexOf(firstInfo);
			int relativeIndex = sortedInfo.indexOf(relativeInfo);
			if( firstIndex == -1 ) {
				throw new IllegalArgumentException( "firstInfo is not found!" );
			} else if( relativeIndex == -1 ) {
				throw new IllegalArgumentException( "relativeInfo is not found!" );
			}
			if( firstIndex + 1 < relativeIndex ){//there are some elems between them
				AbstractList<E> t = new ArrayList<E>();
				t.add( sortedInfo.get(firstIndex + 1) );
				t.add( sortedInfo.get(relativeIndex-1)  );
				return t;
			} else {//there are no elem between them
				sortedInfo.add(relativeIndex, newInfo);//insert succeed!
				return null;
			}
		}
	}
	protected AbstractList<E> insertAfter( E lastInfo, E relativeInfo, E newInfo ){
		if( relativeInfo == null ){
			throw new IllegalArgumentException( "relativeInfo can not be  null!" );
		}
		synchronized(sortedInfo){
			if( lastInfo == null ) {//insert after the old tail
				int relativeIndex = sortedInfo.indexOf(relativeInfo);
				if( relativeIndex == -1 ) {
					throw new IllegalArgumentException( "relativeInfo is not found!" );
				}
				E tailInfo = sortedInfo.get(sortedInfo.size()-1);
				if( tailInfo == relativeInfo ) {
					sortedInfo.add(sortedInfo.size(), newInfo);//insert succeed!
					return null;
				} else {
					AbstractList<E> t = new ArrayList<E>();
					t.add( sortedInfo.get(relativeIndex+1) );
					t.add( sortedInfo.get(sortedInfo.size()-1)  );
					return t;
				}
			}
			int relativeIndex = sortedInfo.indexOf(relativeInfo);
			int lastIndex = sortedInfo.indexOf(lastInfo);
			if( lastIndex == -1 ) {
				throw new IllegalArgumentException( "firstInfo is not found!" );
			} else if( relativeIndex == -1 ) {
				throw new IllegalArgumentException( "relativeInfo is not found!" );
			}
			if( relativeIndex + 1 < lastIndex ){//there are some elems between them
				AbstractList<E> t = new ArrayList<E>();
				t.add( sortedInfo.get(relativeIndex+1) );
				t.add( sortedInfo.get( lastIndex-1 )  );
				if( relativeIndex == 0 && lastIndex == 2 )
				System.out.println( "relativeIndex="+relativeIndex+", lastIndex="+lastIndex );
				return t;
			} else {//there are no elem between them
				sortedInfo.add(relativeIndex+1, newInfo);
				return null;//successful
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.njucs.aiep.pool.ISynchronizedPool#clear()
	 */
	public void clear(){
		sortedInfo.clear();
		compareList.clear();
		//compareListener = null;
		//locateEventListener = null;
	}
	
	/* (non-Javadoc)
	 * @see com.njucs.aiep.pool.ISynchronizedPool#getSorted()
	 */
 	public AbstractList<E> getSorted(){
		AbstractList<E> list = new ArrayList<E>();
		synchronized(sortedInfo){
			Iterator<E> iter = sortedInfo.iterator();
			while( iter.hasNext() ) {
				list.add( iter.next() );
			}
		}
		return list;
	}
	/* (non-Javadoc)
	 * @see com.njucs.aiep.pool.ISynchronizedPool#getInsertListener()
	 */
	public InsertEventListener<E> getInsertListener(){
		return this.insertListener;
	}
	/* (non-Javadoc)
	 * @see com.njucs.aiep.pool.ISynchronizedPool#getCompareOverListener()
	 */
	public CompareOverListener<E> getCompareOverListener(){
		return this.compareOverListener;
	}
	/* (non-Javadoc)
	 * @see com.njucs.aiep.pool.ISynchronizedPool#setCompareEventListener(com.njucs.aiep.pool.CompareEventListener)
	 */
	public void setCompareEventListener(CompareEventListener<E> compareListener){
		this.compareListener = compareListener;
	}
	/* (non-Javadoc)
	 * @see com.njucs.aiep.pool.ISynchronizedPool#setLocateEventListener(com.njucs.aiep.pool.LocateEventListener)
	 */
	public void setLocateEventListener(LocateEventListener<E> locateEventListener){
		this.locateEventListener = locateEventListener;
	}
	
	private synchronized void handle( List<E> list, E newElem, E firstElem, E lastElem ){
		E beginElem = list.get(0);
		E endElem = list.get(list.size()-1);
		E midElem = SynchronizedPool.this.getMiddle(beginElem, endElem );
		if( midElem == null ){
			throw new RuntimeException( "midElem == null" );
		}
		Compare<E> com = Compare.newInstance(newElem, midElem);
		com.setBegin( beginElem );
		com.setEnd( endElem );
		com.setFirst(firstElem);
		com.setLast(lastElem);
		compareList.add(com);
		if( compareListener != null ){
			CompareEvent<E> ce = new CompareEvent<E>(this, com.getId(), newElem, midElem);
			compareListener.compare(ce);
		} else {
			System.out.println( "compareListener == null! cannot compare the elems!" );
		}
	}
	
	protected class SynPoolInsertListener implements InsertEventListener<E> {
		@Override
		public void insert(InsertEvent<E> event) {
			E newElem = event.getInsertElement();
			AbstractList<E> list = SynchronizedPool.this.insert( newElem );
			if( list == null ) {
				if( locateEventListener != null ){
					locateEventListener.locate(new LocateEvent<E>(this, newElem ));
				}
				return;//insert succeed
			}
			handle(list, newElem, null, null );
		}
		
	}
	
	protected class SynPoolOverListener implements CompareOverListener<E> {

		@Override
		public void compareOver(CompareOverEvent<E> event) {
			CompareEvent<E> ce = event.getCompareEvent();
			int eventid = ce.getId();
			Compare<E> compare = null;
			synchronized( compareList ) {
				for( Compare<E> com : compareList ){
					if( com.getId() == eventid ){
						compare = com; break;
					}
				}
				if( compare == null ){
					throw new RuntimeException("cannot find the matched compare(eventid="+eventid+")! ");
				}
				if( event.getCompareResult() == null ){
					throw new RuntimeException("the compare result is null! ");
				}
				compareList.remove( compare );
			}//release the compareList
			if( event.getCompareResult() == CompareResult.GREATER ){
				AbstractList<E> list = SynchronizedPool.this.getMiddleLeft(
						compare.getBegin(), compare.getMiddle());
				if( list == null ){
					AbstractList<E>  list2 = SynchronizedPool.this.insertBefore(compare.getFirst(), 
							compare.getBegin(), compare.getHost());
					if( list2 == null ){
						//System.out.println( "insert succeed!" );
						if( locateEventListener != null ){
							locateEventListener.locate(new LocateEvent<E>(this, compare.getHost() ));
						}
					} else {
						//be careful here
						//pay highly attention if you want to change here, and remember to backup
						handle(list2, compare.getHost(), compare.getFirst(), compare.getBegin() );//checked
					}
				} else {
					handle(list, compare.getHost(), compare.getFirst(), compare.getMiddle() );
				}
			} else {//NOT_GREATER
				AbstractList<E> list = SynchronizedPool.this.getMiddleRight(
						compare.getEnd(), compare.getMiddle());
				if( list == null ){
					AbstractList<E>  list2 = SynchronizedPool.this.insertAfter(compare.getLast(), 
							compare.getEnd(), compare.getHost());
					if( list2 == null ){
						//System.out.println( "insert succeed!" );
						if( locateEventListener != null ){
							locateEventListener.locate(new LocateEvent<E>(this, compare.getHost() ));
						}
					} else {
						//be careful here
						//pay highly attention if you want to change here, and remember to backup
						handle(list2, compare.getHost(), compare.getEnd(), compare.getLast() );//checked
					}
				} else {
					handle(list, compare.getHost(), compare.getMiddle(), compare.getLast() );
				}
			}
		}
		
	}
	
	
}

//seen in the package
class Compare<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7112547966841040643L;

	private static int ID = 100;
	
	private int id;
	private T host, middle, first, last, begin, end;
	
	private Compare( int id, T host, T middle ){
		this.id = id;
		this.host = host;
		this.middle = middle;
		this.first = null;
		this.last = null;
	}
	
	public static<T> Compare<T> newInstance( T host, T middle ){
		Compare<T> compare = new Compare<T>( ID++, host, middle );
		return compare;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the e1
	 */
	public T getHost() {
		return host;
	}

	/**
	 * @return the e2
	 */
	public T getMiddle() {
		return middle;
	}

	/**
	 * @return the first
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * @return the last
	 */
	public T getLast() {
		return last;
	}

	/**
	 * @return the begin
	 */
	public T getBegin() {
		return begin;
	}

	/**
	 * @return the end
	 */
	public T getEnd() {
		return end;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param e1 the e1 to set
	 */
	public void setHost(T host) {
		this.host = host;
	}

	/**
	 * @param e2 the e2 to set
	 */
	public void setMiddle(T middle) {
		this.middle = middle;
	}

	/**
	 * @param first the first to set
	 */
	public void setFirst(T first) {
		this.first = first;
	}

	/**
	 * @param last the last to set
	 */
	public void setLast(T last) {
		this.last = last;
	}

	/**
	 * @param begin the begin to set
	 */
	public void setBegin(T begin) {
		this.begin = begin;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(T end) {
		this.end = end;
	}
	
	
}




