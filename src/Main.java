import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.coobird.thumbnailator.Thumbnails;

public class Main {
	
	public static void main(String[] args) {
		
		int[][] matrix1= { {1,0,0},{0,4,8},{0,0,6} };
		int[][] matrix2= { {2,2,2},{0,0,0},{3,3,3} };
		int[][] matrix3= { {0,0,0},{0,4,0},{5,4,1} };
		int[][] matrix4= { {0,0,0,2},{1,0,0,0},{3,9,7,0},{0,7,7,0} };
		int[][] matrix5= { {10,0,-3,-2},{0,15,-1,0},{0,0,20,0},{-1,0,0,50} };
		
//		MCSR  matrix3MCSR=new MCSR(matrix3);
//		matrix3MCSR.set(2, 2, 3);
//		matrix3MCSR.set(0, 3, 4);
//		matrix3MCSR.set(2, 0, -1);
//		matrix3MCSR.set(1, 2, -5);
//		printMatrix(matrix3MCSR.toMatrix());
//		matrix3MCSR.print();
		
		MCSR matrix1MCSR= new MCSR(matrix1);
		printMatrix(matrix1);
		matrix1MCSR.print();
		
		MCSR matrix2MCSR= new MCSR(matrix2);
		printMatrix(matrix2);
		matrix2MCSR.print();
		
		MCSR matrix3MCSR= new MCSR(matrix3);
		printMatrix(matrix3);
		matrix3MCSR.print();
		
		MCSR matrix4MCSR= new MCSR(matrix4);
		printMatrix(matrix4);
		matrix4MCSR.print();
		
		MCSR matrix5MCSR= new MCSR(matrix5);
		printMatrix(matrix5);
		matrix5MCSR.print();
		
	}

	public static void matrixToImage( int[][] matrix, int size) {
		BufferedImage img= new BufferedImage( matrix[0].length , matrix.length, BufferedImage.TYPE_INT_ARGB);
		
		for(int a=0; a<img.getWidth(); a++) {
			for(int b=0; b<img.getHeight(); b++) {
				if( matrix[b][a]==0 ) {
					Color myColor= new Color(255,255,255,255);
					img.setRGB(a,b,myColor.getRGB() );
				}
				if( matrix[b][a]!=0 ) {
					Color myColor= new Color(0,0,0,255);
					img.setRGB(a,b,myColor.getRGB() );
				}
				
			}
		}
		
		double scaleFactor = (double) size/Math.max( img.getHeight(),  img.getWidth() );
		try {
			BufferedImage scaledImg = Thumbnails.of(img).scale(scaleFactor).asBufferedImage();
			
			JFrame myFrame = new JFrame();
			myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			myFrame.setVisible(true);
			myFrame.setTitle("Hello darkness my old friend...");
			
			ImageIcon icon=new ImageIcon(scaledImg);
			JLabel lbl=new JLabel(icon);
			
			myFrame.setSize( scaledImg.getHeight(), scaledImg.getWidth() );
	        myFrame.add(lbl);
	        
	        //ImageIO.write(scaledImg, "png", new File("imageDisplayed.png") );     //To save the image displayed
	        
		} catch (IOException e) {}
	}

	public static double sparsity(int[][] matrix) {
		
		int noOfZeros=0;
		
		for(int row=0; row< matrix.length; row++) {
			for(int col=0; col<matrix[0].length; col++) {
				if( matrix[row][col]==0) {
					noOfZeros++;
				}
			}
		}
		
		double sparsity= (double) noOfZeros/(matrix.length*matrix[0].length);
		
		return sparsity;
	}
	
	public static double density (int[][] matrix) {
		
		double sparsity= sparsity(matrix);
		
		double density=1-sparsity;
		
		return density;
	}

	public static int[][] createMatrixWith0s (int rows, int cols, int noOfZeros){
		
		if(noOfZeros>rows*cols) {
			throw new IllegalArgumentException("noOfZeros cannot be greater than rows*columns");
		}
		
		int[][] matrix=new int[rows][cols];
		
		//Available pairs
		ArrayList<int[]> avPairs= new ArrayList<int[]>();
		for(int r=0; r<rows; r++) {
			for(int c=0; c<cols; c++) {
				int[] pair=new int[2];	//If we did not make a new reference variable in each iteration we would have the same reference variable in every iteration
				pair[0]=r;
				pair[1]=c;
				avPairs.add(pair);
			}
		}
		
		//Put noOfZeros 0s on the array, on random positions
		Random myRandGen=new Random( );
		int index;
		int[] pos;
		int number;
		for(int a=0; a<noOfZeros; a++) {
			index=myRandGen.nextInt( avPairs.size() );	//Get a random index of avPairs
			pos=avPairs.get(index);		//Get a random AVAILABLE position, using the index
			
			matrix[pos[0]][pos[1]]=0;	 	//Put a 0 on the random position
			avPairs.remove( index ); 	//The position is no longer available
		}

		
		//Put random numbers on the rest of the positions
		while( avPairs.size()  != 0 ) {
			pos=avPairs.get(0);		//Get the next available position
			number=myRandGen.nextInt();		//Get a random number
			
			matrix[pos[0]][pos[1]]=number;	 	//Put the random number on the next available position
			avPairs.remove( 0 ); 	//The position is no longer available
		}
		
		return matrix;
	}
	
	public static int[][] MxM(int[][] multiplier, int[][] multiplicand){
		int[][] result=new int[multiplier.length][multiplicand[0].length];
		
		int curProduct=0;	
		for(int multiplierRow=0; multiplierRow<multiplier.length; multiplierRow++) {
		
			for(int multiplicandColumn=0; multiplicandColumn< multiplicand[0].length; multiplicandColumn++) {
				curProduct=0;
				
				for(int multiplierColumn=0; multiplierColumn<multiplier[0].length; multiplierColumn++) {
					curProduct+=multiplier[multiplierRow][multiplierColumn]*multiplicand[multiplierColumn][multiplicandColumn];
				}
				
				result[multiplierRow][multiplicandColumn]=curProduct;
			}
		}
		
		return result;
	}
	
	public static void printMatrix(int[][] matrix) {
		for(int[] row: matrix) {
			System.out.println( Arrays.toString(row) );
		}
	}
	
	public static int[][] transpose(int[][] input) {
		
		int[][] output= new int[input[0].length][input.length];
		
		for(int row=0; row<input.length; row++) {
			for(int col=0; col<input[0].length; col++) {
				
				output[row][col]=input[col][row];
			}
		}
		
		return output;
	}
	
	public static int mostCommonElem(int[][] matrix) {
		ArrayList<int[]> diffElems= new ArrayList<int[]>();		//This will contain 2-tuples of the form: [value, occurences]
		boolean found;
		int[] tuple;
		
		for(int row=0; row<matrix.length; row++) {
			for(int col=0; col<matrix[0].length; col++) {
				found=false;
				
				for(int a=0; a<diffElems.size(); a++) {
					if( diffElems.get(a)[0] == matrix[row][col] ) {
						found=true;
						diffElems.get(a)[1]++;
						break;
					} else {
						continue;
					}
				}
				
				if( ! found ) {
					tuple= new int[2];
					tuple[0]=matrix[row][col];
					tuple[1]=1;
							
					diffElems.add( tuple );
				}
				
			}
		}
		
		tuple=new int[2];
		tuple[0]=diffElems.get(0)[0];
		tuple[1]=diffElems.get(0)[1];
		for(int a=1; a<diffElems.size(); a++) {
			if( diffElems.get(a)[1]>tuple[1] ) {
				tuple[0]=diffElems.get(a)[0];
				tuple[1]=diffElems.get(a)[1];
			}
		}
		
		return tuple[0];
	}
	
}
