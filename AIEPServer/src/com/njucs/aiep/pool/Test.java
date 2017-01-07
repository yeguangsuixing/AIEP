package com.njucs.aiep.pool;

import java.util.AbstractList;

import com.njucs.aiep.pool.CompareOverEvent.CompareResult;

public class Test {

	static ISynchronizedPool<Integer> pool = new RoundRobinPool<Integer>();
	
	public static void main(String[] args){
		final Integer[] intList = { 5,7,2,8,3,1,9,0, 12, 76, 58, 47, 83, 4};
		
		pool.setCompareEventListener(new CompareEventListener<Integer>(){
			@Override
			public void compare(final CompareEvent<Integer> event) {
				System.out.println( "Now compare: "+event.getFirstElement()+" and "+event.getSecondElement() );
				
				//*
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						CompareOverEvent<Integer> overEvent = null;
						if( event.getFirstElement() > event.getSecondElement() ){
							overEvent =  new CompareOverEvent<Integer>(this, event, CompareResult.GREATER );
						} else {
							overEvent =  new CompareOverEvent<Integer>(this, event, CompareResult.NOT_GREATER );
						}
						pool.getCompareOverListener().compareOver(overEvent);
					}
				}).start();//*/
			}
		});
		pool.setLocateEventListener(new LocateEventListener<Integer>(){
			@Override
			public void locate(LocateEvent<Integer> locateEvent) {
				AbstractList<Integer> sortedList = pool.getSorted();
				StringBuilder sb = new StringBuilder();
				sb.append( "insert: "+locateEvent.getLocateElement()+", sorted: " );
				//if( sortedList.size() < intList.length ) return;
				for( Integer i : sortedList ){
					sb.append( i +", " );
				}
				System.out.println( sb.toString() );
			}
			
		});
		InsertEventListener<Integer> insertListener = pool.getInsertListener();
		for( Integer i : intList ){
			InsertEvent<Integer> event = new InsertEvent<Integer>(pool, i );
			insertListener.insert(event);
		}
	}
	
}
