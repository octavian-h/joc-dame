package checkers.ai;

import checkers.core.GameRepresentation;

public class TEst {
	public static void main(String[] args){
		GameRepresentation game=new GameRepresentation();
		Computer comp=new Computer(game);
		game.switchTurn();
		int[][] matr={{2,-1,2,-1,2,-1,2,-1},
				{-1,2,-1,2,-1,2,-1,2},
				{2,-1,2,-1,2,-1,2,-1},
				{0,2,0,2,0,2,0,2},
				{2,0,2,0,2,0,2,0},
				{1,2,1,2,1,2,1,2},
				{2,1,2,1,2,1,2,1},
				{1,2,1,2,1,2,1,2}};
		game.matrix=matr;
		int[] aux;
		aux=comp.getNextMove();
		System.out.println();
		for(int i=0;i<aux.length;i++)
			System.out.print(aux[i]+" ");
	}
}
