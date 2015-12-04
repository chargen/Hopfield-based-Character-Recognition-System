import java.util.ArrayList;
import java.io.IOException;

public class Hopfield{
	private ArrayList<int[][]> trainingList;														//list of data
	private ArrayList<String> trainingChar;															//list of labels
	private int XDIMENSION, YDIMENSION;																//dimensions
	private int[][] entryCoord;																		//input matrix
	private int[] classificationList;																//percentage of classification for each entry  
	
	//set values needed for calculation
	private int[][] weights;																		//matrix of weights
    private double[][] thresholds;																		//used to determine thresholds
    private double[][] output;																			//output vector
    private int numberSamples = 0;
    private int numberPatterns;
    private double THRESHOLD_CONSTANT = 0.0;
	
	public Hopfield(int x, int y){
		XDIMENSION = x;
		YDIMENSION = y;
	}
	
	public void init(ArrayList<int[][]> matricesList, int[][] myEntry, ArrayList<String> labels){
		numberSamples = XDIMENSION*YDIMENSION;														//set the number of neurons		
		numberPatterns = matricesList.size();
		
		classificationList = new int[numberPatterns];
		weights = new int[numberSamples][numberSamples];											//create the weights matrix        
        output = new double[numberSamples][1];															//create the output matrix        
        thresholds = new double[numberSamples][1];														//create the offset Neuron weights (used to determine thresholds)
        for(int i=0; i<numberSamples; i++){
        	thresholds[i][0] = THRESHOLD_CONSTANT;
        }
        //inputs
		entryCoord = new int[XDIMENSION*YDIMENSION][1];	
		trainingChar = new ArrayList<String>();
		trainingList = new ArrayList<int[][]>();
		
		makeInput(matricesList, myEntry, labels);													//takes the inputs and transforms them into arrays.
		
		calculate();																				//start calculating
	}
	
	public void calculate(){
		int[][] inputXtransp = new int[numberSamples][numberSamples];								//input multiplied by its transposed
		for(int i=0; i<trainingList.size(); i++){
			int[][] matrixT = new int[1][numberSamples];											//transposed of a matrix
			
			matrixT = transpose(trainingList.get(i));												//create the transposed matrices
			inputXtransp = sum((multiply(trainingList.get(i), matrixT)),inputXtransp);				//sum of multiplied matrices
		}
		weights = subtractInt(inputXtransp,identityXpattern());
		activateFunction();
		
		/*for(int a=0; a<numberSamples; a++){
			for(int b=0; b<numberSamples; b++){
				System.out.print(weights[a][b]);
			}
			System.out.println();
		}*/
	}
		
	
	//transpose the input matrix
	public int[][] transpose(int[][] matrix){
		int[][] A = new int[1][numberSamples];
		for (int col=0; col<numberSamples; col++)
            	A[0][col] = matrix[col][0];
		
		return A;
	}
	 
	//multiply 2 matrices
	public int[][] multiply(int[][] matrix1, int[][] matrix2){	       
		if (matrix1[0].length != matrix2.length){
			throw new IllegalArgumentException("matrix1: columns " + matrix1[0].length + " did not match matrix2:Rows " + matrix2.length + ".");
            
		}
		int[][] C = new int[matrix1.length][matrix2[0].length];
        for (int i = 0; i < C.length; i++){
            for (int j = 0; j < C[0].length; j++)
         	   for(int k=0; k<matrix1[0].length;k++)
                 	C[i][j] += matrix1[i][k] * matrix2[k][j];            	
       }
       return C;
	}
	/*public int[][] multiply(int[][] matrix1, int[][] matrix2){		
		int[][] C = new int[numberSamples][numberSamples];
       for (int i = 0; i < numberSamples; i++){
           for (int j = 0; j < numberSamples; j++)
           	C[i][j] = matrix1[i][0] * matrix2[0][j];            	
       }
       return C;
	}*/
		
	public int[][] sum(int[][] matrix1, int[][] matrix2) {
	       int[][] A = new int[numberSamples][numberSamples];
	       for (int i=0; i<numberSamples; i++)
	           for (int j=0; j<numberSamples; j++)
	               A[i][j] = matrix1[i][j] + matrix2[i][j];
	       return A;
	}
	
	public double[][] subtractDouble(int[][] matrix1, double[][] matrix2){
        double[][] A = new double[matrix1.length][matrix1[0].length];
        for (int i = 0; i < matrix1.length; i++)
            for (int j = 0; j < matrix1[0].length; j++)
                A[i][j] = matrix1[i][j] - matrix2[i][j];
        return A;
	}
	
	public int[][] subtractInt(int[][] matrix1, int[][] matrix2){
        int[][] A = new int[matrix1.length][matrix1[0].length];
        for (int i = 0; i < matrix1.length; i++)
            for (int j = 0; j < matrix1[0].length; j++)
                A[i][j] = matrix1[i][j] - matrix2[i][j];
        return A;
	}
	
	public int[][] identityXpattern(){
		int[][] A = new int[numberSamples][numberSamples];
		for (int i=0; i<numberSamples; i++) {
            	A[i][i] = numberPatterns;
        }
		return A;
    }
	
	//==================================================== didn't test ===================================================
	
	public void activateFunction(){	
		output = subtractDouble(multiply(weights,entryCoord),thresholds);
		
		for(int i=0; i<numberPatterns; i++){
			for(int j=0; j<numberSamples; j++){
				if(trainingList.get(i)[j][0] == output[j][0])
					classificationList[j] += 10;
			}
		}
		
		
		for(int s=0; s<classificationList.length; s++){
				System.out.println(classificationList[s]);
		}
		
		System.out.println("-----------------");
		for(int a=0; a<numberSamples; a++){
			for(int b=0; b<numberSamples; b++){
				System.out.print(weights[a][b]);
			}
			System.out.println();
		}
		System.out.println("-----------------");
		
		for(int a=0; a<numberSamples; a++){			
			System.out.println(entryCoord[a][0]);
		}
		
		System.out.println("-----------------");
		for(int a=0; a<numberSamples; a++)
			System.out.println(output[a][0]);
		//System.out.println();
		
	}
	 
	 //takes the inputs and transforms them into arrays.
	 public void makeInput(ArrayList<int[][]> matricesList, int[][] myEntry, ArrayList<String> labels){
		 int i,j,k,l=0;		 
		 
		 for(i=0;i<matricesList.size();i++){
			 int[][] aux = new int[XDIMENSION*YDIMENSION][1];
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
			Hopfield hp = new Hopfield(2,3);
			ArrayList<int[][]> matricesList = new ArrayList<int[][]>(2);
			ArrayList<String> labels = new ArrayList<String>(2);
			int[][] matrix1 = {{1,1},{1,1},{1,1}};
			//int[][] matrix1 = {{2,1},{4,1},{6,1}};
			int[][] matrix2 = {{-1,-1},{-1,-1},{-1,-1}};
			int[][] myEntry = {{-1,-1},{-1,-1},{-1,1}};
			
			matricesList.add(matrix1);
			labels.add("A");
			matricesList.add(matrix2);
			labels.add("B");
			
			hp.init(matricesList, myEntry, labels);
		}
}