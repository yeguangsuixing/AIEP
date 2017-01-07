
importClass (com.njucs.aiep.plugin.fir.frame.Step)
importClass (com.njucs.aiep.game.Status)
importClass (com.njucs.aiep.game.GameTree)
importClass (java.util.AbstractList)

function MyFirAI() {
	
	/**
	 * <p> The first heuristic evaluation function.  This method will be called when 
	 * the server chooses the "Heriustic1" Item.</p>
	 * <p>This is just a sample.</p>
	 * 
	 * @param opponentStep the last step taked by the opponent
	 * @return the step you will take
	 * @see #itsmyturn2(Step) 
	 * @since AIEPv0.1beta
	 * */
	/*Step*/ this.itsmyturn = function(/*Step*/ opponentStep){
		var x = opponentStep.getX();
		var y = opponentStep.getY();
		var t = 2/0;
		while( true ) println("while(true)");
		return new Step( x + 1, y );
	}

	/**
	 * <p> The second heuristic evaluation function. This method will be called when 
	 * the Judge chooses the "Heriustic2" Item.</p>
	 * <p>This is just a sample.</p>
	 * 
	 * @param opponentStep the last step taked by the opponent
	 * @return the step you will take
	 * @see #itsmyturn(Step)	  
	 * @since AIEPv0.1beta
	 * */
	/*Step*/
	this.itsmyturn2 = function(/*Step*/ opponentStep){
		return this.itsmyturn(opponentStep);
	}
	
	/**
	 * Do u want to print the receive info? Please return false if not. 
	 * 
	 * @return <code>true</code>, if u want to print the recv info, 
	 * <code>false</code> othersize 
	 * @since AIEPv0.1beta
	 * */
	/*boolean*/
	this.isPrintInfo = function() {
		return true;
	}
	
	/**
	 * Set the inning info.When the data transmitter receives an info message, it will be called!
	 * 
	 * @param myStatus my status
	 * @param limitedTime the limited time by millisecond
	 * @param opponentInfo the opponent info
	 * @param piecesArray the inning pieces array, length = 15*15 
	 * @since AIEPv0.1beta
	 * @changed AIEPv0.1
	 * */
	/*void*/ 
	this.setInningInfo = function(/*Status*/ myStatus,/*int*/ limitedTime, 
			/*StudentInfo*/ opponentInfo, /*Status[]*/ piecesArray){
		if( myStatus == Status.OFFENSIVE ){
			println( "1myStatus="+myStatus )
		} else {
			println( "2myStatus="+myStatus );
		}
		println( "limitedTime+1="+(limitedTime+1) );
		println( "piecesArray[0] ="+piecesArray[0] );
	}

	/**
	 *  <p>Return your AI id.</p>
	 * @since AIEPv0.1beta
	 * */
	/*String*/ 
	this.getId = function(){
		return "b101220002(error)";
	}


	/**
	 *  <p>Return your AI name.</p>
	 * @since AIEPv0.1beta
	 * */
	/*String*/
	this.getName = function(){
		return "CCCC";
	}

	/**
	 *  <p>Return your AI nickname.</p>
	 * @since AIEPv0.1beta
	 * */
	/*String*/
	this.getNickname = function(){
		return "cccc";
	}

	/**
	 *  <p>Return your AI version.</p>
	 * @since AIEPv0.1
	 * */
	/*String*/
	this.getVersion = function(){
		return "v0.1";
	}


	/**
	 * <p>Return the root of the game tree at the last step. </p>
	 * <p>It will be called when you run the AIEP in the "Site" mode, connecting with the Judge. 
	 * Othersize, it doesn't work if you execute the FIRLoader.</p>
	 * <p>There is ONLY a sample below. Maybe you save the root of the game tree via class member, and
	 * return it in the method.</p>
	 * 
	 * @since AIEPv0.1
	 * */
	 /* GameTree */
	this.getLastGameTree = function(){
		var root = new GameTree( "S", ">=3" );
		var child1 = new GameTree( "A", "<0" );
		var child2 = new GameTree( "B", ">0" );
		root.addChild(child1);
		root.addChild(child2);

		var childc = new GameTree( null, "<0" );
		var childd = new GameTree( "D", ">0" );
		child1.addChild(childc);
		child1.addChild(childd);

		var childe = new GameTree( "E", "<0" );
		var childf = new GameTree( "F", ">0" );
		childc.addChild(childe);
		childc.addChild(childf);
		
		return root;
	}
}

/*
//Here below is the prototype of the class Step & Status & GameTree
function Status(){
	this.OFFENSOVE = 0;
	this.DEFENSIVE = 1;
	this.EMPTY = 2;
}
function Step(){
	this.ctor = function(int, int){}
	this.ctor = function(Status, int, int){}

	this.getX = function(){ return int; }
	this.getY = function(){ return int; }
	this.getStatus = function(){ return Status; }
	this.getUsedTime = function(){ return int; }

	this.setX = function(int){ return; }
	this.setY = function(int){ return; }
	this.setStatus = function(Status){ return; }
}
function GameTree(name, value){
	this.ctor = function(){}
	this.ctor = function(String,String){}

	this.addChild = function(GameTree){ return; }
	this.removeChild = function(GameTree){ return; }

	this.getParent = function(){ return GameTree; }
	this.getTreeChildren = function(){ return AbstractList<GameTree>; }

	this.getStatus = function(){ return Status; }
	this.getName = function(){ return String; }
	this.getValue = function(){ return String; }

	
	this.setStatus = function(Status){ return; }
	this.setName = function(String){ return; }
	this.setValue = function(String){ return; }

	this.getHeight = function(){ return int; }

}

//*/