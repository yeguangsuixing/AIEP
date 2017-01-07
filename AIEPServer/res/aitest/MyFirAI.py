
from com.njucs.aiep.plugin.fir.frame import Step
from com.njucs.aiep.game import Status
from com.njucs.aiep.game import GameTree
from com.njucs.aiep.net.DataTransmit import StudentInfo
from com.njucs.aiep.plugin.fir.ai import FIR_AI


class MyFirAI(FIR_AI):

	def itsmyturn(self, step):
		'''
		* <p> The first heuristic evaluation function.  This method will be called when 
		 * the server chooses the "Heriustic1" Item.</p>
		 * <p>This is just a sample.</p>
		 * 
		 * @param opponentStep the last step taked by the opponent
		 * @return the step you will take
		 * @see #itsmyturn2(Step)
		'''
		print '"itsmyturn" is called by the system.';
		return Step(step.getX()+1, step.getY());

	def itsmyturn2(self, step):
		'''
		* <p> The second heuristic evaluation function. This method will be called when 
		 * the server chooses the "Heriustic2" Item.</p>
		 * <p>This is just a sample.</p>
		 * 
		 * @param opponentStep the last step taked by the opponent
		 * @return the step you will take
		 * @see #itsmyturn(Step)
		'''
		return self.itsmyturn(step);

	def isPrintInfo(self):
		'''
		* Do u want to print the receive info? Please return false if not. 
		 * 
		 * @return <code>true</code>, if u want to print the recv info, 
		 * <code>false</code> othersize
		'''
		return 1;# 0 - false, 1 - true

	def setInningInfo(self, myStatus, limitedTime, opponentInfo, piecesArray):
		'''
		 * Set the innings info.
		 * 
		 * @param myStatus my status
		 * @param limitedTime the limited time
		 * @param opponentInfo the opponent info
		 * @param piecesArray the innings pieces array, length = 15*15
		'''
		if (myStatus == Status.OFFENSIVE):
			print '1"setInningInfo" is called by the system.';
		else:
			print '2"setInningInfo" is called by the system.';
		print 'limitedTime + 1 = ', limitedTime+1;
		print str(  opponentInfo.getId() );
		print str(  opponentInfo.getName() );
		print str(  opponentInfo.getNickname() );
		print piecesArray[1];
		return;

	def getId(self):
		return 'b101220003';

	def getName(self):
		return 'DDDD';

	def getNickname(self):
		return 'dddd';

	def getVersion(self):
		return 'v0.1';

	def getLastGameTree(self):
		'''
		 * <p>Return the root of the game tree at the last step. </p>
		 * <p>It will be called when you run the AIEP in the "Site" mode, connecting with the Judge. 
		 * Othersize, it doesn't work if you execute the FIRLoader.</p>
		 * <p>There is ONLY a sample below. Maybe you save the root of the game tree via class member, and
		 * return it in the method.</p>
		 * 
		 * @since AIEPv0.1
		'''
		root = GameTree( "S", ">=3" );
		child1 = GameTree( "A", "<0" );
		child2 = GameTree( "B", ">0" );
		root.addChild(child1);
		root.addChild(child2);
		
		 # be careful here,
		 # a game tree will be seen as a pruning, whose name is None.
		childc = GameTree( None, "<0" );
		childd = GameTree( "D", ">0" );
		child1.addChild(childc);
		child1.addChild(childd);

		childe = GameTree( "E", "<0" );
		childf = GameTree( "F", ">0" );
		childc.addChild(childe);
		childc.addChild(childf);

		childe = GameTree( "EG", "<0" );
		childf = GameTree( "FH", ">0" );
		childd.addChild(childe);
		childd.addChild(childf);
		
		return root;
	