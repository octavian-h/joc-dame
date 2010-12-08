package checkers.ai;

public class ComputerMove {
	private int fromRow;
	private int fromColumn;
	private int toRow;
	private int toColumn;
	
	public ComputerMove(int fi,int fj, int ti,int tj){
		fromRow=fi;
		fromColumn=fj;
		toRow=ti;
		toColumn=tj;
	}
	
	public int[] getMove(){
		int[] a;
		if(isJump()){
			a=new int[6];
			a[0]=fromRow;
			a[1]=fromColumn;
			a[2]=Math.max(fromRow,toRow)-1;
			a[3]=Math.max(fromColumn, toColumn)-1;	
			a[4]=toRow;
			a[5]=toColumn;
		}
		else{
			a=new int[4];
			a[0]=fromRow;
			a[1]=fromColumn;
			a[2]=toRow;
			a[3]=toColumn;
		}
		return a;
	}
	
	public boolean isJump(){
		if(Math.abs(fromRow-toRow)!=1) return true;
		return false;
	}
	
}
