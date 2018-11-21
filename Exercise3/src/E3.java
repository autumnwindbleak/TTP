

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import ttp.Optimisation.MyOperators;
import ttp.Optimisation.Optimisation;

import java.util.HashMap;
import java.util.Vector;
import ttp.TTPInstance;
import ttp.TTPSolution;
import ttp.Benchmark.TTPDynamicTours;
import ttp.Utils.Configuration;


/**
 * @author Zhuoying Li (a1675725)
 * @author Puzhi Yao (a1205593)
 * @author Xueyang Wang (a1690260)
 * @author Jingwen Wei (a1671836)
 * This class is adopted from original Driver created by @author wagner
 */
public class E3 {
	/*
	 * Vector container for storing best objective
	 * value in current run
	 */
	public static Vector<double[]> bestOB;
	/*
	 * Vector container for storing computation time of 
	 * best objective value in current run
	 */
	public static Vector<long[]> bestCT;
	/* The current sequence of parameters is
	 * args[0]  folder with TTP files
	 * args[1]  pattern to identify the TTP problems that should be solved
	 * args[2]  optimization approach chosen (not used, in exercise3 the best algorithm and self designed algorithm need to be used in the same time 
	 * args[3]  stopping criterion: number of evaluations without improvement (not used in Exercise3, two algorithm here all use up the limit time)
	 * args[4]  stopping criterion: time in milliseconds (e.g., 60000 equals 1 minute)
	 * args[5]	Dynamic Item Set Mode: 1 = Generate New benchmark, 2 = Read from pre-generated benchmark
	 * args[6]	target version of benchmark: 1 = benchmark created by the Randomly Chosen Exchange, 1 = benchmark created by Randomly Chosen 2-opt step 
	 * args[7]  number of generations
	 * args[8]  number of runs
	 * 
	 * All parameters are set in Config file
	 */
	public static void main(String[] args) throws IOException {
		// run configuration
		Configuration config = new Configuration();
		String[] parameters = config.getConfig();
		
		// fetching previous set parameters from config file
		if(parameters[1].equals("0")) {
			// run all 9 instances
			for(int i = 0; i < 9; ++i) {
				args = new String[]{parameters[0], config.getInstanceName(i),
						parameters[2], parameters[3], parameters[4],parameters[5],parameters[6],parameters[7],parameters[8]};
				// run dynamic items
				if(parameters[6].equals("0")) {
					// run both two benchmarks
					args[6] = "1";
					DynamicTours(args);
					args[6] = "2";
					DynamicTours(args);
				}
				else if(parameters[6].equals("1")) {
					// run benchmark version 1
					DynamicTours(args);
				}
				else if(parameters[6].equals("2")) {
					// run benchmark version 2
					DynamicTours(args);
				}
			}
		}
		else {
			// run 1 instance
			int instanceIndex = Integer.valueOf(parameters[1]) - 1;
			args = new String[]{parameters[0],config.getInstanceName(instanceIndex),
					parameters[2], parameters[3], parameters[4],parameters[5],parameters[6],parameters[7],parameters[8]};
			// run dynamic items
			if(parameters[6].equals("0")) {
				// run both two benchmarks
				args[6] = "1";
				DynamicTours(args);
				args[6] = "2";
				DynamicTours(args);
			}
			else if(parameters[6].equals("1")) {
				// run benchmark version 1
				DynamicTours(args);
			}
			else if(parameters[6].equals("2")) {
				// run benchmark version 2
				DynamicTours(args);
			}
		}
	}

	
	/**
	 * DynamicTours can process several files sequentially with
	 * dynamic tour sets
	 * @param args
	 * @throws IOException 
	 */
	public static void DynamicTours(String[] args) throws IOException {
		// read file name from arguments
		File[] files = ttp.Utils.Utils.getFileList(args);

		// setup algorithm and parameters from arguments
		int algorithm = Integer.parseInt(args[2]);
		int durationWithoutImprovement = Integer.parseInt(args[3]);
		int maxRuntime = Integer.parseInt(args[4]);

		// setup which version of generated dynamic tour set
		// will be used.(Choose 1 or 2)
		int tourSetVersion = Integer.parseInt(args[6]);

		// setup which mode of dynamic tour will be used
		// 1 for generating new dynamic tour sets
		// 2 for reading old dynamic tour sets
		int DynaTourMode = Integer.parseInt(args[5]);

		// setup number of generations
		// compute 100,000 generations with multiple algorithms
		int totalGeneration = Integer.parseInt(args[7]);
		// setup number of runs
		int totRuns = Integer.parseInt(args[8]);;

		// setup record of average solution
		bestOB = new Vector<double[]>();
		bestCT = new Vector<long[]>();
		// start iterate input files
		for (File f:files) {
			//output instance name to know which instance is alive right now
			System.out.println(f.getName());
			// read the TTP instance
			TTPInstance instance = new TTPInstance(f);

			// setup TTP dynamic items from instance
			TTPDynamicTours DynaTours = new TTPDynamicTours();
			
			// generate a Linkern tour (or read it if it already exists)
			int[] tour = Optimisation.linkernTour(instance);
			// create a space to store the data in the benchmark (benchmark set store exchange positions)
			HashMap<Integer,int[]> positions = null;
			
			// Validate Dynamic tour Mode				
			if(DynaTourMode != 1 && DynaTourMode != 2){
				// Invalid dynamic tour mode
				// program terminated
				System.out.println("Invalid Dynamic tour mode, Please choose from 1 or 2.");
				System.exit(0); 
			}
			
			//validate tourSetVersion
			if(tourSetVersion != 1 && tourSetVersion != 2){
				// Invalid dynamic tour set
				// program terminated
				System.out.println("Invalid Dynamic tour set, Please choose from 1 or 2.");
				System.exit(0); 
			}

			// Repeat runs on each instances for each each algorithm
			for (int currentTimeOfRun = 1; currentTimeOfRun <= totRuns; ++currentTimeOfRun) {
				// setup for best solution quality container
				//ComplexHeuristic_2 is the best algorithm in Exercise 2
				double bestOB_ComplexHeuristic_2 = Double.NEGATIVE_INFINITY;
				long bestComputationTime_ComplexHeuristic_2 = 0;
				//Algorithm designed for Exercise 3
				double bestOB_ComplexHeuristic_3 = Double.NEGATIVE_INFINITY;
				long bestComputationTime_ComplexHeuristic_3 = 0;
				

				// start generation iteration
				
				for (int currentGeneration = 0; currentGeneration <= totalGeneration; 
						currentGeneration=currentGeneration + 1) {        	
					
					//check the tour mode to know if a new benchmark set need to be created
					//create new dynamic tours for current instance
					if(DynaTourMode == 1) {
						// generate new dynamic tours
						DynaTours.generateDynamicTours(instance, tour, tourSetVersion,totalGeneration);
					}
					// read pre-generated dynamic tours file
					positions = DynaTours.getPositions(instance, tourSetVersion);
					//check the dynamic tours file status. if not exist, create a new one and read it
					if(positions == null){
						DynaTours.generateDynamicTours(instance, tour, tourSetVersion,totalGeneration);
						positions = DynaTours.getPositions(instance, tourSetVersion);
					}
					
					// setup start computing time
					long startTime = System.currentTimeMillis();
					
					//setup and find the current tour by different algorithm
					//generationOfTour is the index of the changing point of every several generation of tour, for example: 50, 100.....
					int generationOfTour = 0;
					int[] currenttour = tour;
					
					// if use Randomly chosen exchange benchmark set (50 generation a change)
					if(tourSetVersion == 1){
						generationOfTour = currentGeneration - (currentGeneration % 50);
						if(generationOfTour != 0){
							currenttour = DynaTours.RandomExchange(tour, positions.get(generationOfTour)[0], positions.get(generationOfTour)[1]);
						}
					}
					//if use Randomly chosen 2-opt benchmark set (500 generation a change)
					else{
						generationOfTour = currentGeneration - (currentGeneration % 500);
						if(generationOfTour != 0){
							currenttour = DynaTours.TwoOptExchange(tour, positions.get(generationOfTour)[0], positions.get(generationOfTour)[1]);
						}
					}
					
					
					


					// Run the best algorithm in Exercise 2: ComplexHeuristic_2 algorithm
					TTPSolution solution_ComplexHeuristic_2 = MyOperators.ComplexHeuristic_2(instance, currenttour,
							durationWithoutImprovement, maxRuntime);
					// check if solution is valid
					// weight remain must larger than zero
					if(solution_ComplexHeuristic_2.wend > 0) {
						// check best solution quality
						if(solution_ComplexHeuristic_2.ob > bestOB_ComplexHeuristic_2) {
							bestOB_ComplexHeuristic_2 = solution_ComplexHeuristic_2.ob;
							bestComputationTime_ComplexHeuristic_2 = solution_ComplexHeuristic_2.computationTime;
						}
					}
					// Run the new algorithm designed in Exercise 3: ComplexHeuristic_3 algorithm
					TTPSolution solution_ComplexHeuristic_3 = MyOperators.ComplexHeuristic_3(instance, currenttour,
							durationWithoutImprovement, maxRuntime);
					//check if the solution is valid
					//weight remain must larger than zero
					if(solution_ComplexHeuristic_3.wend > 0){
						//check best solution quality
						if(solution_ComplexHeuristic_3.ob > bestOB_ComplexHeuristic_3){
							bestOB_ComplexHeuristic_3 = solution_ComplexHeuristic_3.ob;
							bestComputationTime_ComplexHeuristic_3 = solution_ComplexHeuristic_3.computationTime;
						}
					}
						
					// print complete percentage
					double completeRate = ((double)currentGeneration / (double) totalGeneration) * 100;
					// Round a completeRate to 3 significant figures
					BigDecimal bd = new BigDecimal(completeRate);
					bd = bd.round(new MathContext(3));
					double roundRatio= bd.doubleValue();
					// setup end time
					long tmpEndTime = System.currentTimeMillis();
					long estimateTotalTime = ((tmpEndTime - startTime) * (totalGeneration - currentGeneration)) / (long)1000;
					//print out the current running state
					System.out.println("Complete Rate: "+currentGeneration+"/"+totalGeneration+" ("+roundRatio+" %)"+" Time Remain: "+estimateTotalTime+" s");
				}

				// complete a series of generation
				// store best result from this generation
				double[] tmpOB = {bestOB_ComplexHeuristic_2,bestOB_ComplexHeuristic_3};
				long[] tmpCT = {bestComputationTime_ComplexHeuristic_2,bestComputationTime_ComplexHeuristic_3};

				// store solution quality of current run
				bestOB.add(tmpOB);
				bestCT.add(tmpCT);
				//write the result to the file each run time
				saveAverageSolution(f,tourSetVersion);
			}
		}
	}

	
	
	/**
	 * this function is to write the result to a csv file
	 * @param f: the file name of the instance
	 * @param benchmark: benchmark set number
	 * @throws IOException
	 */
	public static void saveAverageSolution(File f, int benchmark) throws IOException {
		//get the instance name and generate the name of the result file
		String tmpFileName = f.toString();
		String outputFileName = "results/"+tmpFileName.substring(10, tmpFileName.length()-4)+"-benchmark"+benchmark+".csv";
		File outputFile = new File(outputFileName);
		//check the file and the folder's states, if not exists, create them
		if(!outputFile.exists()){
			if(!outputFile.getParentFile().exists()){
				outputFile.getParentFile().mkdirs();
			}
			outputFile.createNewFile();
		}
		//write the result to the csv file
		FileWriter fw = new FileWriter(outputFileName);
		try(  PrintWriter out = new PrintWriter(fw)  ){
			out.println("Run,OB-ComplexHeuristic_2,OB-ComplexHeuristic_3,Time-ComplexHeuristic_2,Time-ComplexHeuristic_3");
			for(int i = 0; i < bestOB.size(); ++i) {
				int tmpIndex = i + 1;
				out.println(tmpIndex+","+bestOB.get(i)[0]+"," + bestOB.get(i)[1] + "," +bestCT.get(i)[0] + "," +bestCT.get(i)[1]);
			}
		}
	}
}
