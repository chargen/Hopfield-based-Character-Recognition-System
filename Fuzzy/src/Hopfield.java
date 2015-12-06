import java.util.ArrayList;
import java.io.IOException;

import Jama.Matrix;

public class Hopfield{
	private ArrayList<double[][]> trainingList;														//list of data
	private ArrayList<String> trainingChar;															//list of labels
	private int XDIMENSION, YDIMENSION;																//dimensions
	private double[][] entryCoord;																		//input matrix
	private int[] classificationList;	
	//percentage of classification for each entry  
	
	QuickSort<int[]> newSort = new QuickSort<int[]>();
	
	protected int[] matchList = null;
	protected int[] posList = null;
	
	//set values needed for calculation
	private double[][] weights;																		//matrix of weights
    private double[][] thresholds;																		//used to determine thresholds
    private int numberSamples = 0;
    private int numberPatterns;
    private double THRESHOLD_CONSTANT = 0.0;
	
	public Hopfield(int multiplier){
		XDIMENSION = 7 * multiplier;
		YDIMENSION = 9 * multiplier;
	}
	
	public void init(ArrayList<double[][]> matricesList, double[][] myEntry, ArrayList<String> labels){
		numberSamples = XDIMENSION*YDIMENSION;														//set the number of neurons		
		numberPatterns = matricesList.size();
		
		classificationList = new int[numberPatterns];
		weights = new double[numberSamples][numberSamples];											//create the weights matrix        
        thresholds = new double[numberSamples][1];														//create the offset Neuron weights (used to determine thresholds)
        for(int i=0; i<numberSamples; i++){
        	thresholds[i][0] = THRESHOLD_CONSTANT;
        }
        //inputs
		entryCoord = new double[XDIMENSION*YDIMENSION][1];	
		trainingChar = new ArrayList<String>();
		trainingList = new ArrayList<double[][]>();
		
		makeInput(matricesList, myEntry, labels);													//takes the inputs and transforms them into arrays.
		
		calculate();																				//start calculating
	}
	
	public void calculate(){
		double[][] inputXtransp = new double[numberSamples][numberSamples];								//input multiplied by its transposed
		for(int i=0; i<trainingList.size(); i++){
			double[][] matrixT = new double[1][numberSamples];											//transposed of a matrix
			
			matrixT = transpose(trainingList.get(i));												//create the transposed matrices
			inputXtransp = sum((multiply(trainingList.get(i), matrixT)),inputXtransp);				//sum of multiplied matrices
		}
		weights = subtract(inputXtransp,identityXpattern());
		activateFunction();
		
		/*for(int a=0; a<numberSamples; a++){
			for(int b=0; b<numberSamples; b++){
				System.out.print(weights[a][b]);
			}
			System.out.println();
		}*/
	}
		
	
	//transpose the input matrix
	public double[][] transpose(double[][] matrix){
		Matrix A = new Matrix(matrix).transpose();
		return A.getArray();
	}
	 
	//multiply 2 matrices
	public double[][] multiply(double[][] matrix1, double[][] matrix2){	       
		Matrix C = new Matrix(matrix1).times(new Matrix(matrix2));
		return C.getArray();
	}

	//sum 2 matrices
	public double [][] sum(double[][] matrix1, double[][] matrix2) {	
		Matrix C = new Matrix(matrix1).plus(new Matrix(matrix2));
		return C.getArray();
	}
	
	//subtract 2 matrices
	public double[][] subtract(double[][] matrix1, double[][] matrix2){  
        Matrix C = new Matrix(matrix1).minus(new Matrix(matrix2));
		return C.getArray();
	}	
	
	public double[][] identityXpattern(){
		double[][] A = new double[numberSamples][numberSamples];
		for (int i=0; i<numberSamples; i++) {
            	A[i][i] = numberPatterns;
        }
		return A;
    }
	
	public double[][] sign(double[][] output){
		for (int i=0; i<numberSamples; i++) {
			if (output[i][0]>=0)
				output[i][0]=1;
		    else
		    	output[i][0]=-1;
        }
		return output;
    }
	
	//==================================================== didn't test ===================================================
	
	public void activateFunction(){
	
		boolean run = true;
		int count = 0, inc = 0;
		while(run){
			double[][] output = sign(subtract(multiply(weights, entryCoord), thresholds));
			
			for(int i = 0; i < trainingList.size(); i++){
				for(int j = 0; j < (XDIMENSION * YDIMENSION); j++){
					double tLval = trainingList.get(i)[j][0];
					if(tLval == output[j][0]){
						count++;
					}
				}
				matchList[i] = count;
				posList[i] = i;
				
				
				if(count == output.length)
					run = false;
			}	
		}
		newSort.setSortArray(posList, matchList);	
		int[] returnArray = newSort.sortArray(0, posList.length);
		
		
		
	}
		
		
	
	
	 //takes the inputs and transforms them into arrays.
	 public void makeInput(ArrayList<double[][]> matricesList, double[][] myEntry, ArrayList<String> labels){
		 int i,j,k,l=0;		 
		 
		 for(i=0;i<matricesList.size();i++){
			 double[][] aux = new double[XDIMENSION*YDIMENSION][1];
			 for(j=0;j<YDIMENSION;j++){
				 for(k=0;k<XDIMENSION;k++){				 
					 aux[l++][0] = matricesList.get(i)[j][k];
				 }
			 }			 
			 trainingChar.add(labels.get(i));	 
			 trainingList.add(aux);
			 l=0;	 		 
		 }
		 
		 for(j=0;j<YDIMENSION;j++){
			 for(k=0;k<XDIMENSION;k++){
				 entryCoord[l++][0] = myEntry[j][k];
			 }
		 }
	 }
	 
	//example of usage
	public static void main(String[] args){
			@SuppressWarnings("unused")
			Hopfield hp = new Hopfield(CharacterRecog.multiplier);
			ArrayList<double[][]> matricesList = new ArrayList<double[][]>(2);
			ArrayList<String> labels = new ArrayList<String>(2);
			double[][] matrix1 = {{1,1},{1,1},{1,1}};
			//int[][] matrix1 = {{2,1},{4,1},{6,1}};
			double[][] matrix2 = {{-1,-1},{-1,-1},{-1,-1}};
			double[][] myEntry = {{-1,-1},{-1,-1},{-1,1}};
			
			matricesList.add(matrix1);
			labels.add("A");
			matricesList.add(matrix2);
			labels.add("B");
			
			hp.init(matricesList, myEntry, labels);
		}
}
