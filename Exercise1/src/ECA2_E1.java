
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import ttp.TTPInstance;
import ttp.TTPSolution;
import ttp.Optimisation.MyOperators;
import ttp.Optimisation.Optimisation;
import ttp.Utils.Configuration;
import ttp.Utils.DataPool;


/**
 * @author Zhuoying Li
 * @author Puzhi Yao
 * @author Xueyang Wang
 * @author Jingwen Wei
 * This class is adopted from original Driver created by @author wagner
 */
public class ECA2_E1 {

	/**
	 * store optimal solutions resulted from RLS
	 */
	private static ArrayList<TTPSolution> RLS ;
	
	/**
	 * store optimal solutions resulted from (1+1)EA
	 */
	private static ArrayList<TTPSolution> EA ;
	
	/**
	 * store optimal solutions resulted from ComplexHeuristic_1
	 */
	private static ArrayList<TTPSolution> CH1 ;
	
	/* The current sequence of parameters is
	 * args[0]  folder with TTP files
	 * args[1]  pattern to identify the TTP problems that should be solved
	 * args[2]  null
	 * args[3]  stopping criterion: number of evaluations without improvement
	 * args[4]  stopping criterion: time in milliseconds (e.g., 60000 equals 1 minute)
	 * 
	 * All parameters are set in Config file
	 */
	public static void main(String[] args) {
		// Initialise the storages
		RLS = new ArrayList<>();
		EA = new ArrayList<>();
		CH1 = new ArrayList<>();
		
		// run configuration
		Configuration config = new Configuration();
		String[] parameters = config.getConfig();
		// Print the parameters read from the Config file
		System.out.println("parameters:"+ parameters[0] + "," + parameters[1] + "," + parameters[3] + "," + parameters[4]);
		
		// fetching previous set parameters from config file
		if(parameters[1].equals("0")) {
			// run all 9 instances
			for(int i = 0; i < 9; ++i) {
				args = new String[]{parameters[0], config.getInstanceName(i),
						parameters[2], parameters[3], parameters[4]};
				// run three algorithms for each instance
				Entry_F(args);
				
			}
		}
		else{
			// run 1 instance
			int instanceIndex = Integer.valueOf(parameters[1]) - 1;
			args = new String[]{parameters[0],config.getInstanceName(instanceIndex),
					parameters[2], parameters[3], parameters[4]};
			// run three algorithms for this instance
			Entry_F(args);
		}
		
		// output result data to a csv file
		try {
			saveSolution(config);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This function is designed to find optimal solutions using the algorithms for the given instances
	 * @param args parameters for running the algorithms
	 */
	public static void Entry_F(String[] args){
		// read file name from arguments
		File[] files = ttp.Utils.Utils.getFileList(args);

		// setup algorithm and parameters from arguments
		int durationWithoutImprovement = Integer.parseInt(args[3]);
		int maxRuntime = Integer.parseInt(args[4]);
		
		// start iterate input files
		for (File f:files) {
			// read the TSP instance
			TTPInstance instance = new TTPInstance(f);

			// generate a Linkern tour (or read it if it already exists)
			int[] tour = Optimisation.linkernTour(instance);
			// Print the name of the current instance
			System.out.println(f.getName()+": ");
			// do the optimisation using the three algorithms
			TTPSolution solution_CH1 = MyOperators.ComplexHeuristic_1(instance, tour, durationWithoutImprovement, maxRuntime);
			// Print the result of CH1 to screen
			System.out.print("CH1: ");
			solution_CH1.println();
			// Store the result of CH1
			CH1.add(solution_CH1);
			TTPSolution solution_EA = Optimisation.hillClimber(instance, tour, 2, durationWithoutImprovement, maxRuntime);
			// Print the result of EA to screen
			System.out.print("EA: ");
			solution_EA.println();
			// Store the result of EA
			EA.add(solution_EA);
			TTPSolution solution_RLS = Optimisation.hillClimber(instance, tour, 1, durationWithoutImprovement, maxRuntime);
			// Print the result of RLS to screen
			System.out.print("RLS: ");
			solution_RLS.println();
			// Store the result of RLS
			RLS.add(solution_RLS);
			System.out.println();
			


		}
	}

	/**
	 * This method will save solutions of each instance to a csv File
	 * @param config configurations read from Config
	 * @throws IOException
	 */
	public static void saveSolution(Configuration config) throws IOException {
		// Initialise the output file name
		String tmpFileName = "E1_results_"+ System.currentTimeMillis();
		String outputFileName = "results/"+tmpFileName.substring(10, tmpFileName.length()-4)+".csv";

		// check output dir exist or not
		File outputFile = new File(outputFileName);
		// check if output file exist or not 
		if(!outputFile.exists()) {
			// check if output file folder exist or not
			if(!outputFile.getParentFile().exists()) {
				// create folder
				outputFile.getParentFile().mkdirs();
			}
			// create file
			outputFile.createNewFile();
		}

		// writing results into file
		FileWriter fw = new FileWriter(outputFileName, true);
		try(  PrintWriter out = new PrintWriter(fw)  ){
			out.println("instanceName,OB-Ref-RLS,OB-Ref-11EA,OB-ComplexHeuristic_1,Time-Ref-RLS,Time-Ref-11EA,Time-ComplexHeuristic_1");
			for(int i = 0; i < CH1.size(); ++i) {
				String instanceName = config.getInstanceName(i);
				out.println(instanceName+","+RLS.get(i).ob+","+EA.get(i).ob+","+CH1.get(i).ob+","+RLS.get(i).computationTime+","+EA.get(i).computationTime+","+CH1.get(i).computationTime);
			}
		}
		
		fw.close();
	}





} 
