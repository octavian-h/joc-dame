package checkers.ai;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import checkers.core.GameRepresentation;

public class Computer extends RecursiveAction{
	
	private static final long serialVersionUID = 3622694984953650533L;
	private LegalJump legalJ;
	private LegalMove legalM;
	private GameRepresentation game;
	private ForkJoinPool compute;
	
	public Computer(GameRepresentation g){
		game=g;
		legalM=new LegalMove(game);
		legalJ=new LegalJump(game);
		
	}
	public void compute(){
		invokeAll(legalJ,legalM);
	}
	public int[] getNextMove(){
		
		Random generator = new Random();
		ArrayList<ComputerMove> moves;
		ArrayList<ComputerMove> jumps;
	
		compute=new ForkJoinPool();
		compute.invoke(this);
		
		jumps=legalJ.getLegalJumps();
		if(jumps!=null){
			int index=generator.nextInt(jumps.size());
			return jumps.get(index).getMove();
		}
		
		moves=legalM.getLegalMoves();
		if(moves!=null){
			int index=generator.nextInt(moves.size());
			return moves.get(index).getMove();
		}
		return null;
		
			
	}
}
