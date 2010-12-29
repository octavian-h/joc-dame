package checkers.ai;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

import checkers.core.GameRepresentation;

public class LegalJump extends RecursiveAction{
	
	private ArrayList<ComputerMove> legal;
	private GameRepresentation game;
	
	public LegalJump(GameRepresentation g){
		game=g;
		legal=new ArrayList<ComputerMove>();
	}
	
	public void compute(){
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
				if(game.getSlotState(i, j)==GameRepresentation.BLACK||game.getSlotState(i, j)==GameRepresentation.BLACK_KING){
					if(game.canJump(i, j, i+1, j-1,i+2,j-2))
						legal.add(new ComputerMove(i,j,i+2,j-2));
					if(game.canJump(i, j, i+1, j+1,i+2,j+2))
						legal.add(new ComputerMove(i,j,i+2,j+2));
					if(game.canJump(i, j, i-1, j-1,i-2,j-2))
						legal.add(new ComputerMove(i,j,i-2,j-2));
					if(game.canJump(i, j, i-1, j+1,i-2,j+2))
						legal.add(new ComputerMove(i,j,i-2,j+2));
				}
	}
	
	public ArrayList<ComputerMove> getLegalJumps(){
		if(legal.isEmpty())
			return null;
		else return legal;
	}
	
	
}
