import java.util.ArrayList;
import java.util.Arrays;

public class MCSR {
	private ArrayList<Integer> values;
	private ArrayList<Integer> columnsOfValues;
	private ArrayList<Integer> indexOfNextWithSameRow;
	private int[] indexOfFirstWithRow;
	
	private int noOfRows;			//Could be extracted from StartingIndexInValuesOfRow. We use it here for convenience and symmetry
	private int noOfColumns;		//Necessary to know how many columns the matrix has
	
	public MCSR (int[][] matrix) {
		values=new ArrayList<Integer>();
		columnsOfValues=new ArrayList<Integer>();
		indexOfNextWithSameRow=new ArrayList<Integer>();
		
		indexOfFirstWithRow=new int[matrix.length];
		Arrays.fill(indexOfFirstWithRow, -1);
		
		for(int row=0; row<matrix.length; row++) {
			
			for(int col=0; col<matrix[0].length; col++) {
				this.set(row, col, matrix[row][col] );
			}
			
		}
		
		noOfRows=matrix.length;
		noOfColumns=matrix[0].length;
		
	}
	
	public ArrayList<Integer> getValues(){
		return values;
	}
	
	public ArrayList<Integer> getColumnsOfValues(){
		return columnsOfValues;
	}
	
	public ArrayList<Integer> getIndexOfNextWithSameRow(){
		return indexOfNextWithSameRow;
	}
	
	public int[] getIndexOfFirstWithRow() {
		return indexOfFirstWithRow;
	}
	
	public int getNoOfRows() {
		return noOfRows;
	}
	
	public int getNoOfColumns() {
		return noOfColumns;
	}
	
	public void set(int row, int column, int value) {
		
		if( indexOfFirstWithRow[row] == -1) {
			if(value==0) { //Subcase of case iv)
				return;
			}
			else { //Subcase of case i)
				indexOfFirstWithRow[row]=indexOfNextWithSameRow.size();
				
				values.add(value);
				columnsOfValues.add(column);
				indexOfNextWithSameRow.add(-1);
				return;
			}
			
		}else {
			int i=indexOfFirstWithRow[row];
			
			while(true) {
				if( columnsOfValues.get(i)==column) {

					if(value!=0) {	//Case iii)
						values.set(i, value);
						return;
					} 
					else {	//Case ii)
						values.remove(i);
						columnsOfValues.remove(i);
						indexOfNextWithSameRow.remove(i);
						
						for(int a=0; a<indexOfNextWithSameRow.size(); a++) {
							if( indexOfNextWithSameRow.get(a) > i) {
								indexOfNextWithSameRow.set(a, indexOfNextWithSameRow.get(a)-1 );
							}
						}
						
						for(int a=0; a<indexOfFirstWithRow.length; a++) {
							if( indexOfFirstWithRow[a]> i) {
								indexOfFirstWithRow[a]--;
							}
						}
						
						return;
					}	
				}
				
				else if( indexOfNextWithSameRow.get(i) == -1) {		
					
					if(value==0) {		//Subcase of case iv)
						return;
					}
					else {		//Subcase of case i)
						indexOfNextWithSameRow.set(i, indexOfNextWithSameRow.size() ); 
						
						values.add(value);
						columnsOfValues.add(column);
						indexOfNextWithSameRow.add(-1);
						return;
					}
				
				}else {
					i = indexOfNextWithSameRow.get(i);
				}

			}
			
			
			
		}
			
	}
	
	public int[][] toMatrix(){
		int[][] matrix=new int[noOfRows][noOfColumns];
		//Initialize the matrix
		for(int[] row: matrix) {
			Arrays.fill(row, 0);
		}
		
		int i;
		for(int row=0; row<noOfRows; row++) {
			i=indexOfFirstWithRow[row];
			while(i != -1) {
				matrix[row][columnsOfValues.get(i)]=values.get(i);
				
				i=indexOfNextWithSameRow.get(i);
			}
		}
		
		return matrix;
	}
	
	public void print() {
		
		System.out.print("Values: ");
		System.out.println( Arrays.toString( values.toArray() ) );
		
		System.out.print("columnsOfValues: ");
		System.out.println( Arrays.toString( columnsOfValues.toArray() ) );
		
		System.out.print("indexOfNextWithSameRow: ");
		System.out.println( Arrays.toString( indexOfNextWithSameRow.toArray() ));
		
		System.out.print("indexOfFirstWithRow: ");
		System.out.println( Arrays.toString( indexOfFirstWithRow));
		
	}

	public static int[][] MCSRxM( MCSR multiplier, int[][] multiplicand){
		int[][] result=new int[multiplier.getNoOfRows()][multiplicand[0].length];
		
		int curProduct;
		int i;
		for(int multiplierRow=0; multiplierRow<multiplier.getNoOfRows(); multiplierRow++) {
			
			for(int multiplicandColumn=0; multiplicandColumn< multiplicand[0].length; multiplicandColumn++) {
				curProduct=0;
				
				i=multiplier.getIndexOfFirstWithRow()[multiplierRow];
				while(i != -1) {
					curProduct+= multiplier.getValues().get(i)* multiplicand[multiplier.getColumnsOfValues().get(i)][multiplicandColumn];	
					i=multiplier.getIndexOfNextWithSameRow().get(i);
				}
				
				result[multiplierRow][multiplicandColumn]=curProduct;
			}
		}
		
		return result;
	}
	
	public static int[][] transpose( MCSR input ){
		
		int[][] output=new int[input.getNoOfColumns()][input.getNoOfRows()];
		//Initialize the matrix
		for(int[] row: output) {
			Arrays.fill(row, 0);
		}
		
		int i;
		for(int row=0; row<input.getNoOfRows(); row++) {
			i=input.getIndexOfFirstWithRow()[row];
			while(i != -1) {
				output[input.getColumnsOfValues().get(i)][row]=input.getValues().get(i);
				
				i=input.getIndexOfNextWithSameRow().get(i);
			}
		}
		
		return output;
	}
	
}
