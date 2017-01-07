package com.njucs.aiep.pool;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.njucs.aiep.pool.CompareOverEvent.CompareResult;



public class RoundRobinPool<E extends Serializable>  implements ISynchronizedPool<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5752639532365726095L;

	//data
	protected Set<PartInfo<E>> sortedPartInfo = 
		Collections.synchronizedSet(new TreeSet<PartInfo<E>>());
	
	//extra data
	protected List<Compare<E>> compareList = Collections.synchronizedList(new LinkedList<Compare<E>>());
	
	//event drive
	protected transient InsertEventListener<E> insertListener = null;
	protected transient CompareOverListener<E> compareOverListener = null;
	
	protected transient CompareEventListener<E> compareListener = null;
	protected transient LocateEventListener<E> locateEventListener = null;
	
	public RoundRobinPool(){
		insertListener = new RoundRobinInsertListener();
		compareOverListener = new RoundRobinOverListener();
	}
	
	@Override
	public void initialize() {
		if( insertListener == null ){
			insertListener = new RoundRobinInsertListener();
		}
		if( compareOverListener == null ) {
			compareOverListener = new RoundRobinOverListener();
		}
	}
	
	@Override
	public void clear() {
		sortedPartInfo.clear();
		compareList.clear();
	}
	


	@Override
	public CompareOverListener<E> getCompareOverListener() {
		return this.compareOverListener;
	}

	@Override
	public InsertEventListener<E> getInsertListener() {
		return this.insertListener;
	}

	@Override
	public AbstractList<E> getSorted() {
		updateSortedInfoList();
		AbstractList<E> list = new ArrayList<E>();
		synchronized(sortedPartInfo){
			Iterator<PartInfo<E>> iter = sortedPartInfo.iterator();
			while( iter.hasNext() ) {
				list.add( iter.next().elem );
			}
		}
		return list;
	}

	@Override
	public void setCompareEventListener(CompareEventListener<E> compareListener) {
		this.compareListener = compareListener;
	}

	@Override
	public void setLocateEventListener(
			LocateEventListener<E> locateEventListener) {
		this.locateEventListener = locateEventListener;
	}
	
	private boolean checkSortedInfo(PartInfo<E> partInfo){
		synchronized( sortedPartInfo ){
			for( PartInfo<E> info : sortedPartInfo ){
				if( ! partInfo.getCompareList().contains(info) ){
					partInfo.getCompareList().add(info);
					partInfo.getCompareCountList().add(0);
					info.getCompareList().add(partInfo);
					info.getCompareCountList().add(0);
					Compare<E> c = new Compare<E>(info, partInfo);
					Compare<E> c2 = new Compare<E>(partInfo, info);
					c.setOutside(partInfo);
					c2.setOutside(partInfo);
					compareList.add(c);
					compareList.add(c2);
					if( compareListener != null ){
						CompareEvent<E> event = new CompareEvent<E>(this, c.id, info.elem, partInfo.elem );
						CompareEvent<E> event2 = new CompareEvent<E>(this, c2.id, partInfo.elem, info.elem );
						compareListener.compare(event);
						compareListener.compare(event2);
					}
					return false;//no break
				}
			}
		}
		return true;
	}

	private void updateSortedInfoList(){
		synchronized( sortedPartInfo ){
			List<PartInfo<E>> t = new ArrayList<PartInfo<E>>();
			t.addAll( sortedPartInfo );
			sortedPartInfo.clear();
			sortedPartInfo.addAll(t);
		}
	}
	
	private class RoundRobinInsertListener implements InsertEventListener<E> {

		@Override
		public void insert(InsertEvent<E> event) {
			E newElem = event.getInsertElement();
			PartInfo<E> info = new PartInfo<E>( newElem );
			//partInfoList.add(info);
			if(checkSortedInfo( info )){
				sortedPartInfo.add(info);
				updateSortedInfoList();
				if( locateEventListener != null ){
					locateEventListener.locate(new LocateEvent<E>(this, newElem ));
				}
			};
		}
		
	}
	private class RoundRobinOverListener implements CompareOverListener<E> {

		@Override
		public void compareOver(CompareOverEvent<E> event) {
			CompareEvent<E> ce = event.getCompareEvent();
			int eventid = ce.getId();
			Compare<E> compare = null;
			synchronized( compareList ) {
				for( Compare<E> com : compareList ){
					if( com.id == eventid ){
						compare = com; break;
					}
				}
				if( compare == null ){
					throw new RuntimeException("cannot find the matched compare(eventid="+eventid+")! ");
				}
				if( event.getCompareResult() == null ){
					throw new RuntimeException("the compare result is null! ");
				}
				//compareList.remove( compare );
			}//release the compareList
			int c;
			synchronized( compare.outside ){
				compare.setResult(event.getCompareResult());
				compare.first.addTotalCount();
				compare.second.addTotalCount();
				if( compare.getResult() == CompareResult.GREATER ){
					compare.first.addWinCount();
				} else if ( compare.getResult() == CompareResult.NOT_GREATER ){
					compare.second.addWinCount();
				}
				PartInfo<E> info = compare.outside==compare.first?compare.second:compare.first;
				int index = compare.outside.getCompareList().indexOf(info);
				c = compare.outside.getCompareCountList().remove(index)+1;
				compare.outside.getCompareCountList().add(index, c);
			}
			if( c == 2 ){
				if(checkSortedInfo(compare.outside)){
					System.out.println( "[pool]sortedPartInfo.size()="+sortedPartInfo.size() );
					if( ! sortedPartInfo.add(compare.outside)){
						System.out.println("[pool]add failed!");
					}
					System.out.println( "[pool]sortedPartInfo.size()="+sortedPartInfo.size() );
					updateSortedInfoList();
					if( locateEventListener != null ){
						locateEventListener.locate(new LocateEvent<E>(this, compare.outside.elem ));
					}
				}
			}
		}
		
	}

	static class Compare<E extends Serializable> implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8416782165962471024L;
		
		private static int ID = 100;
		public int id;
		public PartInfo<E> first, second, outside;

		private CompareResult result;


		public Compare(PartInfo<E> first, PartInfo<E> second){
			id = ID++;
			this.first = first;
			this.second = second;
		}
		/**
		 * @return the result
		 */
		public CompareResult getResult() {
			return result;
		}

		/**
		 * @param result the result to set
		 */
		public void setResult(CompareResult result) {
			this.result = result;
		}
		/**
		 * @return the outside
		 */
		public PartInfo<E> getOutside() {
			return outside;
		}
		/**
		 * @param outside the outside to set
		 */
		public void setOutside(PartInfo<E> outside) {
			this.outside = outside;
		}
		
	}
}


class PartInfo<E extends Serializable> implements  Serializable, Comparable<PartInfo<E>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1338550071807424462L;
	E elem;
	private AbstractList<PartInfo<E>> compareList = new ArrayList<PartInfo<E>>();
	private AbstractList<Integer> compareCountList = new ArrayList<Integer>();
	private int totalCount = 0, winCount = 0;
	private Date created = new Date();
	
	public PartInfo(E elem){
		this.elem = elem;
	}

	/**
	 * @return the elem
	 */
	public E getElem() {
		return elem;
	}

	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * @return the winCount
	 */
	public int getWinCount() {
		return winCount;
	}

	/**
	 * @param elem the elem to set
	 */
	public void setElem(E elem) {
		this.elem = elem;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public synchronized void addTotalCount() {
		this.totalCount++;
	}

	/**
	 * @param winCount the winCount to set
	 */
	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	/**
	 * @param winCount the winCount to set
	 */
	public synchronized void addWinCount() {
		this.winCount ++;
	}

	/**
	 * @param compareList the compareList to set
	 */
	public void setCompareList(AbstractList<PartInfo<E>> compareList) {
		this.compareList = compareList;
	}

	/**
	 * @return the compareList
	 */
	public AbstractList<PartInfo<E>> getCompareList() {
		return compareList;
	}

	/**
	 * @return the compareCountList
	 */
	public AbstractList<Integer> getCompareCountList() {
		return compareCountList;
	}

	/**
	 * @param compareCountList the compareCountList to set
	 */
	public void setCompareCountList(AbstractList<Integer> compareCountList) {
		this.compareCountList = compareCountList;
	}

	@Override
	public int compareTo(PartInfo<E> o) {
		float a = (this.winCount)/(float)(this.totalCount);
		float b = (o.winCount)/(float)(o.totalCount);
		return b>a?1:( b<a?-1 : 
			o.created.before(created)?1:-1
		);
	}

	
	
}







