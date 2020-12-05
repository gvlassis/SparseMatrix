import java.util.ArrayList;
import java.util.Arrays;

public class CSR {
	private ArrayList<Integer> values;
	private ArrayList<Integer> columnsOfValues;
	private int[] startingIndexInValuesOfRow;
	
	private int noOfRows;			//Could be extracted from StartingIndexInValuesOfRow. We use it here for convenience and symmetry
	private int noOfColumns;		//Necessary to know how many columns the matrix has
	
	private int impliedElem;
	
	public CSR(int[][] matrix) {		
		this(matrix, 0);		//Default impliedElem: 0
	}
	
	public CSR(int[][] matrix, int passedImpliedElem) {
		impliedElem= passedImpliedElem;
		
		values= new ArrayList<Integer>();
		columnsOfValues= new ArrayList<Integer>();
		startingIndexInValuesOfRow= new int[matrix.length+1];
		
		int counter=0;
		for(int row=0; row<matrix.length; row++) {
			startingIndexInValuesOfRow[row]=counter;
			
			for(int col=0; col<matrix[0].length; col++) {
				
				if(matrix[row][col]!=impliedElem) {
					values.add(matrix[row][col]);
					columnsOfValues.add(col);
					counter++;
				}
			}
		}
		
		startingIndexInValuesOfRow[matrix.length]=counter;
		
		noOfRows=matrix.length;
		noOfColumns=matrix[0].length;
		
	}
	
	public CSR( ArrayList<Integer> passedValues, ArrayList<Integer> passedColumnsOfValues, int[] passedStartingIndexInValuesOfRow, int passedNoOfColumns, int passedImpliedElem) {
		impliedElem=passedImpliedElem;
		
		values=passedValues;
		columnsOfValues=passedColumnsOfValues;
		startingIndexInValuesOfRow=passedStartingIndexInValuesOfRow;
		
		noOfRows=startingIndexInValuesOfRow.length-1;
		noOfColumns=passedNoOfColumns;		//Number of rows cannot be infered otherwise
	}
	
	public ArrayList<Integer> getValues() {
		return values;
	}
	
	public ArrayList<Integer> getColumnsOfValues() {
		return columnsOfValues;
	}
	
	public int[]  getStartingIndexInValuesOfRow() {
		return startingIndexInValuesOfRow;
	}
	
	public int getNoOfRows() {
		return noOfRows;
	}
	
	public int getNoOfColumns() {
		return noOfColumns;
	}
	
	public int getImpliedElem() {
		return impliedElem;
	}
	
	public int[][] toMatrix() {
		int[][] matrix=new int[noOfRows][noOfColumns];
		
		//Initialize the matrix
		for(int[] row: matrix) {
			Arrays.fill(row, impliedElem);
		}
		
		
		for(int row=0; row<noOfRows; row++) {
			for(int a=startingIndexInValuesOfRow[row]; a<startingIndexInValuesOfRow[row+1]; a++) {
				matrix[row][columnsOfValues.get(a)]=values.get(a);
			}
		}
		
		return matrix;
	}
		
	public void print() {
		
		System.out.print("values: ");
		System.out.println( Arrays.toString( values.toArray() ) );
		
		System.out.print("columnsOfValues: ");
		System.out.println( Arrays.toString( columnsOfValues.toArray() ) );
		
		System.out.print("startingIndexInValuesOfRow: ");
		System.out.println( Arrays.toString( startingIndexInValuesOfRow ));
		
		System.out.print("impliedElem: ");
		System.out.println( impliedElem );
	}
	
	public void set(int row, int column, int value) {
		
		int assumedIndex=startingIndexInValuesOfRow[row];
		for(int a=startingIndexInValuesOfRow[row]; a<startingIndexInValuesOfRow[row+1]; a++) {
			if(columnsOfValues.get(a)<column) {
				assumedIndex++;
				continue;
			}
			else if(columnsOfValues.get(a)>column) {
				break;
			}
			else if(columnsOfValues.get(a)==column) {
				
				if(value!=impliedElem) {		//Case ii)
					values.set(assumedIndex, value);
					return;
				}
				else if ( value==impliedElem ) {  //Case iii)
					values.remove(assumedIndex);
					columnsOfValues.remove(assumedIndex);
					
					for(int b=(row+1); b<=noOfRows; b++) {
						startingIndexInValuesOfRow[b]--;
					}
					
					return;
				}

				
			}
		}
		
		
		if(value==impliedElem) {		//Case iv)
			return;
		} else {	//Case i)
			values.add(assumedIndex, value);
			columnsOfValues.add(assumedIndex, column);
			
			for(int a=(row+1); a<=noOfRows; a++) {
				startingIndexInValuesOfRow[a]++;
			}
		}

	}
	
	public int get(int row, int column) {
		for(int a=startingIndexInValuesOfRow[row]; a<startingIndexInValuesOfRow[row+1]; a++) {
			if(columnsOfValues.get(a)<column) {
				continue;
			}
			else if(columnsOfValues.get(a)==column) {
				return values.get(a);
			}
			else if(columnsOfValues.get(a)>column) {		
				break;
			}
		}
		
		return impliedElem;
	}
	
	static CSR CSRxCSR(CSR multiplier, CSR multiplicand) {		//ONLY WORKS CORRECTLY IF impliedElem: 0 FOR THE MULTIPLIER
		//product(CSR)=multiplier(CSR)*multiplicand(CSR). Converting the multiplicand to a normal matrix, 
		//,using CSRxM to find the product(normal) and then converting it to CSR gives better results (faster) BUT, 
		//assumes that we have sufficient size to first simultaneously store the multiplicand and the product 
		//in normal matrix format.
		//In most cases, THIS FUNCTION SHOULD BE AVOIDED!
		
		//This function creates the product row by row, from left to right (the same way we traverse the given matrix in 
		//the CSR construction)
		ArrayList<Integer> values= new ArrayList<Integer>();
		ArrayList<Integer> columnsOfValues= new ArrayList<Integer>();
		int[] startingIndexInValuesOfRow= new int[multiplier.getNoOfRows()+1];
		int counter=0;
		
		int curProduct;
		
		//In this loop we determine the product matrix row by row and we instantly convert it to CSR
		for(int multiplierRow=0; multiplierRow<multiplier.getNoOfRows(); multiplierRow++) {
			startingIndexInValuesOfRow[multiplierRow]=counter;
			
			for(int multiplicandColumn=0; multiplicandColumn< multiplicand.getNoOfColumns(); multiplicandColumn++) {
				curProduct=0;
				
				//Find the product of this cell by adding to curProduct the contribution of each non zero sub product.
				for(int a=multiplier.getStartingIndexInValuesOfRow()[multiplierRow]; a<multiplier.getStartingIndexInValuesOfRow()[multiplierRow+1]; a++) {
					curProduct+=multiplier.getValues().get(a)*multiplicand.get( multiplier.getColumnsOfValues().get(a), multiplicandColumn);
				}
				
				if(curProduct==0) {
					continue;
				}
				else if(curProduct!=0) {
					values.add( curProduct );
					columnsOfValues.add( multiplicandColumn );
					counter++;
				}
			}
		}
		startingIndexInValuesOfRow[multiplier.getNoOfRows()]=counter;
		
		return new CSR( values, columnsOfValues, startingIndexInValuesOfRow, multiplicand.getNoOfColumns(), 0);

	}
	
	public static int[][] CSRxM(CSR multiplier, int[][] multiplicand){     //ONLY WORKS CORRECTLY IF impliedElem: 0 FOR THE MULTIPLIER
		int[][] result=new int[multiplier.getNoOfRows()][multiplicand[0].length];
		
		int curProduct;	
		for(int multiplierRow=0; multiplierRow<multiplier.getNoOfRows(); multiplierRow++) {
			
			for(int multiplicandColumn=0; multiplicandColumn< multiplicand[0].length; multiplicandColumn++) {
				curProduct=0;
				
				for(int a=multiplier.getStartingIndexInValuesOfRow()[multiplierRow]; a<multiplier.getStartingIndexInValuesOfRow()[multiplierRow+1]; a++) {
					curProduct+=multiplier.getValues().get(a)*multiplicand[multiplier.getColumnsOfValues().get(a)] [multiplicandColumn];
				}
				
				result[multiplierRow][multiplicandColumn]=curProduct;
			}
		}
		
		return result;
	}
	
	public static int[][] transpose(CSR input) {
		int[][] output=new int[input.getNoOfColumns()][input.getNoOfRows()];
		
		//Initialize the output
		for(int[] row: output) {
			Arrays.fill(row, input.getImpliedElem() );
		}
				
		for(int row=0; row<input.getNoOfRows(); row++) {
			for(int a=input.getStartingIndexInValuesOfRow()[row]; a<input.getStartingIndexInValuesOfRow()[row+1]; a++) {
				output[input.getColumnsOfValues().get(a)][row]=input.getValues().get(a);
			}
		}
				
		return output;
	}
	
}
