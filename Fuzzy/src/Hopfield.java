import java.util.ArrayList;
import java.io.IOException;
import java.text.DecimalFormat;

import Jama.Matrix;

public class Hopfield implements Runnable{
	private ArrayList<double[][]> trainingList;														//list of data
	private ArrayList<String> trainingChar;															//list of labels
	private int XDIMENSION, YDIMENSION;																//dimensions
	private double[][] entryCoord;																		//input matrix
	private int[] classificationList;	
	protected int[] activatedPixels;
	protected int matchingPixels = 0;
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
    
    Thread hopThread;
	
	public Hopfield(int multiplier){
		hopThread = new Thread(this);
		hopThread.start();
		XDIMENSION = 7 * multiplier;
		YDIMENSION = 9 * multiplier;
	}
    
    public Hopfield(int x, int y){
		hopThread = new Thread(this);
		hopThread.start();
    	XDIMENSION = x;
    	YDIMENSION = y;
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

		calculate();
	}
	
	public void calculate(){
		double[][] inputXtransp = new double[numberSamples][numberSamples];								//input multiplied by its transposed
		for(int i=0; i<trainingList.size(); i++){
			double[][] matrixT = new double[1][numberSamples];											//transposed of a matrix
			
			matrixT = transpose(trainingList.get(i));												//create the transposed matrices
			inputXtransp = sum((multiply(trainingList.get(i), matrixT)),inputXtransp);				//sum of multiplied matrices
		}
		weights = subtract(inputXtransp,identityXpattern());
		//CharacterRecog.pixelPad.setCoord(activateFunction());
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
		    else if(output[i][0]< 0)
		    	output[i][0]=-1;
        }
		return output;
    }
	
	//==================================================== didn't test ===================================================
	
	public double[][] activateFunction(){
		activatedPixels = new int[XDIMENSION * YDIMENSION];
		boolean run = true;
		int count = 0;
		double[][] output = null;
		double[][] outputOld = null;
		int printval = 0;
		while(run){
			output = sign(subtract(multiply(weights, entryCoord), thresholds));
			int c2 = 0;
			if(outputOld != null){
				for(int k = 0; k < output.length; k++){
					if(output[k][0] == outputOld[k][0]){
						c2++;
					}
				}
			}
			outputOld = output;
				for(int i = 0; i < trainingList.size(); i++){
					for(int j = 0; j < (XDIMENSION * YDIMENSION); j++){
						if(trainingList.get(i)[j][0] == output[j][0]){
							count++;
						}
						if(count == (XDIMENSION * YDIMENSION)){
							run = false;
						}
						if(count > printval){
							printval = count;

							System.out.println(printval);
						}
						if(output[j][0] == 1)
							matchingPixels++;
					}
					activatedPixels[i] = matchingPixels;
					count = 0;
					matchingPixels = 0;
				}
				
			if(c2 == (XDIMENSION * YDIMENSION)){
				run = false;
			}
	
		}
		System.out.print("Finished.");
		return makeGrid(output);
	}
		
	public void sort(){

		//activatedPixels = new int[XDIMENSION * YDIMENSION];
		matchList = new int[trainingList.size()];
		posList = new int[trainingList.size()];
		int count = 0;
		for(int i = 0; i < trainingList.size(); i++){
			for(int j = 0; j < (XDIMENSION * YDIMENSION); j++){
				if(trainingList.get(i)[j][0] == entryCoord[j][0] && entryCoord[j][0] == 1){
					count++;
				}else if(entryCoord[j][0] == 1 && count > 0){
					count--;
				}
				/*if(trainingList.get(i)[j][0] == 1)
					matchingPixels++;*/
				
				System.out.println("count: " + count + "\nMatchingPixels: " + matchingPixels);
			}

			//activatedPixels[i] = matchingPixels;
			matchList[i] = count;
			posList[i] = i;
			//matchingPixels = 0;
			count = 0;
		}
		newSort.setSortArray(posList, matchList);	
		int[] returnArray = newSort.sortArray(0, matchList.length - 1);
		int size = returnArray.length - 1;
		int[] countArray = newSort.getRankArray();
		try{
			CharacterRecog.matchScreen.setText("Best Guess: "+ trainingChar.get(returnArray[size]) + "\nwith " +
				new DecimalFormat("#0.00").format(((double)(countArray[size])/activatedPixels[posList[size]])*100) + "% Certainty\n\n");
			if(CharacterRecog.tOutput)CharacterRecog.pixelPad.setCoord(makeGrid(trainingList.get(posList[size])));
			for(int i = size - 1; i >= 0; i--){
				CharacterRecog.matchScreen.append("\t"+ trainingChar.get(returnArray[i]) + "-" +
						new DecimalFormat("#0.00").format(((double)(countArray[i])/activatedPixels[posList[i]])*100) + "%\n");
			}
		}catch(Exception e){}
		returnArray = null;
		countArray = null;
	}
	
	

	public double[][] makeGrid(double[][] myEntry){
		double[][] grid = new double[YDIMENSION][XDIMENSION];
		int y,x,k=0;
		for(y=0;y<YDIMENSION;y++){
			for(x=0;x<XDIMENSION;x++){
				grid[y][x]=(int)myEntry[k][0];
				k++;
			}
		}
		return grid;
	}
	
	 //takes the inputs and transforms them into arrays.
	 public void makeInput(ArrayList<double[][]> matricesList, double[][] myEntry, ArrayList<String> labels){
		 int l=0;		 
		 
		 for(int i=0;i<matricesList.size();i++){
			 double[][] aux = new double[XDIMENSION*YDIMENSION][1];
			 for(int y=0;y<YDIMENSION;y++){
				 for(int x=0;x<XDIMENSION;x++){				 
					 aux[l++][0] = matricesList.get(i)[y][x];
				 }
			 }			 
			 trainingChar.add(labels.get(i));	 
			 trainingList.add(aux);
			 l=0;	 		 
		 }
		 
		 for(int y=0;y<YDIMENSION;y++){
			 for(int x=0;x<XDIMENSION;x++){
				 entryCoord[l++][0] = myEntry[y][x];
			 }
		 }
	 }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
