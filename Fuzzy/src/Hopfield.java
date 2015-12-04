import java.util.ArrayList;

public class Hopfield{
	private int XDIMENSION, YDIMENSION;
	private int[][] gridCoord;																	//input matrix
	private ArrayList<int[][]> trainingList;													//list of data
	private ArrayList<String> trainingChar;														//list of labels
	
	//set values needed for calculation
	private int[][] weights;																	//matrix of weights
    private int[] offsetNeuronWeights;															//used to determine thresholds
    private int[] output;																		//output vector
    private int iterations = 0;
    private int neuronCount = 0;
    private int numberPatterns;
    private double THRESHOLD_CONSTANT = 0.0;
	
	public Hopfield(int x, int y){
		XDIMENSION = x;
		YDIMENSION = y;
	}
	
	public void init(ArrayList<int[][]> matricesList, int[][] myEntry, ArrayList<String> labels){
		gridCoord = new int[XDIMENSION][YDIMENSION];
		gridCoord = myEntry.clone();
		weights = new int[XDIMENSION][YDIMENSION];
		trainingList = new ArrayList<int[][]>(matricesList);
		trainingChar = new ArrayList<String>(labels);
		numberPatterns = trainingList.size();

		if (numberPatterns == 0){																//See if this is the first part of training
			return;
		}
		if (neuronCount == 0){			
			neuronCount = XDIMENSION;															//set the number of neurons to the number of lines
		}
		calculate();
	}
	
	public void calculate(){
		int[][] inputXtransp = new int[XDIMENSION][YDIMENSION];									//input multiplied by its transposed
		int[][] matrixT = new int[YDIMENSION][XDIMENSION];										//transposed of a matrix
		System.out.print(numberPatterns);
		for(int i=0; i<numberPatterns; i++){			
			matrixT = transpose(trainingList.get(i)).clone();									//create the transposed matrix						
			inputXtransp = sum(multiply(trainingList.get(i), matrixT),inputXtransp).clone();	//multiply all matrices with their identities and sum them
		}	
		weights = subtract(inputXtransp,identityXpattern()).clone();							//create the weights matrix

		/*System.out.print("aqui--------------------");
		for(int x = 0; x < XDIMENSION; x++){
			for(int y = 0; y < YDIMENSION; y++){
				System.out.print(matrixT[y][x]);
			}
			System.out.println();
		}
		System.out.print("fim aqui--------------------");*/
	}
	
	public int[][] transpose(int[][] matrix){
		int[][] A = new int[XDIMENSION][YDIMENSION];
		for (int col=0; col<XDIMENSION; col++) {
            for(int row=0; row<YDIMENSION; row++){
            	A[row][col] = matrix[col][row];
            }
        }
		return A;
	}
	
	public int[][] identityXpattern(){
		int[][] A = new int[XDIMENSION][YDIMENSION];
		for (int i=0; i<XDIMENSION; i++) {
            	A[i][i] = numberPatterns;
        }
		return A;
    }
	
	public int[][] multiply(int[][] matrix1, int[][] matrix2){
        int[][] C = new int[XDIMENSION][YDIMENSION];
        for (int i = 0; i < XDIMENSION; i++)
            for (int j = 0; j < XDIMENSION; j++)
                for (int k = 0; k < YDIMENSION; k++)
                    C[i][j] += matrix1[i][k] * matrix2[k][j];
        return C;
	}
	
	public int[][] subtract(int[][] matrix1, int[][] matrix2){
        int[][] A = new int[XDIMENSION][YDIMENSION];
        for (int i = 0; i < XDIMENSION; i++)
            for (int j = 0; j < YDIMENSION; j++)
                A[i][j] = matrix1[i][j] - matrix2[i][j];
        return A;
	}
	
	 public int[][] sum(int[][] matrix1, int[][] matrix2) {
	        int[][] A = new int[XDIMENSION][YDIMENSION];
	        for (int i = 0; i < XDIMENSION; i++)
	            for (int j = 0; j < YDIMENSION; j++)
	                A[i][j] = matrix1[i][j] + matrix2[i][j];
	        return A;
	}
}