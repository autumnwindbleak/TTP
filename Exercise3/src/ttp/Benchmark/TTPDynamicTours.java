package ttp.Benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import ttp.TTPInstance;
import ttp.Optimisation.Optimisation;

/**
 * This class is designed to generate dynamic tours/read dynamic tours  from/to a benchmark data set.   
 * @author Zhuoying Li (a1675725)
 * @author Puzhi Yao (a1205593)
 * @author Xueyang Wang (a1690260)
 * @author Jingwen Wei (a1671836)
 *
 */
public class TTPDynamicTours {
	
	
	/**
	 * this method take the current instance and original linkern tour to generate dynamic tours
	 * @param instance: current TTPInstance
	 * @param tour: original linkern tour
	 * @param tourSetVersion: 1 for using randomly chosen exchange operation. 2 for using randomly chosen 2-opt step 
	 * @param totalGeneration: total number fo generation
	 * @throws IOException
	 */
	public void generateDynamicTours(TTPInstance instance, int[] tour, int tourSetVersion, int totalGeneration) throws IOException{
		// setup which target version folder of 
		// dynamic benchmark
		String version = "";
		String benchmark1 = "benchmark1/";
		String benchmark2 = "benchmark2/";
		//set the tour change frequency
		int generationleap = 0;
		
		// setup benchmark version
		// tour change every 50 generation for randomly chosen exchange operation
		if(tourSetVersion == 1) {
			version = benchmark1;
			generationleap = 50;
		}
		// tour change every 500 generation for randomly chosen 2-opt step
		else if (tourSetVersion == 2) {
			version = benchmark2;
			generationleap = 500;
		}
		else {
			// print out error message
			System.out.println("Invalid tour set version number, Please choose from 1 or 2.");
			System.exit(0); 
		}
		
		// dynamic tour
		
		//setup the data file which store the dynamic tours
		String inputDataName = String.valueOf(instance.file);
		inputDataName = inputDataName.substring(10, inputDataName.length()-4);
		String outputFileName = "benchmarks/dynamicTours/"+inputDataName+"/"+version+ "tours.txt";
		File f = new File(outputFileName);
		//if the folder and/or the file not exists, create them.
		if(!f.exists()){
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdirs();
			}
			f.createNewFile();
		}
		FileWriter out = new FileWriter(f);
		//according to the changing frequency, create new tours by chosen tourSetVersion
		for(int generation = 0; generation <= totalGeneration; generation= generation + generationleap){
			//keep the original tour for the first several generations 
			Random ran = new Random();
			//randomly selected two position(without the start city)
			int position1 = ran.nextInt(tour.length-2) + 1;
			int position2 = ran.nextInt(tour.length-2) + 1;
			//make two position different
			while(position1 == position2){
				position2 = ran.nextInt(tour.length-2) + 1;
			}
			//put the two random position in order
			if(position1 > position2){
				int tmp = position1;
				position1 = position2;
				position2 = tmp;
			}
			// save tour's operation in benchmark
			out.write(generation + " " + position1 + " " + position2 +"\n");
			out.flush();
		}
		out.close();
	}
	
		
	
	/**
	 * this method take the current instance to find the dynamic operators that created before
	 * @param instance: current TTPInstance
	 * @param tourSetVersion: 1 for using randomly chosen exchange operation. 2 for using randomly chosen 2-opt step 
	 * @return null: if there is no dynamic tours created before. 
	 * @return HashMap<Generation, changed tour>: return a hashmap contains the tour according to the generation
	 * @throws FileNotFoundException
	 */
	public HashMap<Integer,int[]> getPositions(TTPInstance instance, int tourSetVersion) throws FileNotFoundException{
		HashMap<Integer,int[]> positions = new HashMap<Integer,int[]>(); 
		
		// setup which version of pre-generated dynamic benchmark
		// will be read in this generation.
		String version = "";
		String benchmark1 = "benchmark1/";
		String benchmark2 = "benchmark2/";
		
		// setup benchmark version
		if(tourSetVersion == 1) {
			version = benchmark1;
		}
		else if (tourSetVersion == 2) {
			version = benchmark2;
		}
		else {
			// print out error message
			// terminate program
			System.out.println("Invalid tour set version number, Please choose from 1 or 2.");
			System.exit(0); 
		}
		
		// setup new item file name
		String inputDataName = String.valueOf(instance.file);
		inputDataName = inputDataName.substring(10, inputDataName.length()-4);
		String readFileName = "benchmarks/dynamicTours/"+inputDataName+"/"+version+ "tours.txt";
		
		File readfile = new File(readFileName);
		//if file is not exist, return null
		if(!readfile.exists()){
			return null;
		}
		//read the Benchmark set and put into the hashmap and match generation with exchange positions
		Scanner in = new Scanner(readfile);
		while(in.hasNextLine()){
			String line = in.nextLine();
			String[] tmp = line.split(" ");
			//find generation index
			int generation = Integer.parseInt(tmp[0]);
			//get positions
			int[] position = new int[2];
			position[0] = Integer.parseInt(tmp[1]);
			position[1] = Integer.parseInt(tmp[2]);
			//put into hashmap
			positions.put(generation, position);
		}
		return positions;
		
	}
		
	/**
	 * this method is to create a new tour according to the input tour with randomly chosen exchanged operation
	 * @param tour: the tour need to do the operation
	 * @param position1: change point
	 * @param position2: change point
	 * @return int[]: the changed tour
	 */
	public int[] RandomExchange(int[] tour,int position1, int position2){
		//create the new tour
		int[] newtour = tour;
		//exchange two position
		int tmp = newtour[position1];
		newtour[position1] = newtour[position2];
		newtour[position2] = tmp;
		return newtour;
	}
	/**
	 * this method is to create a new tour according to the input tour with randomly chosen 2-opt step
	 * @param tour: the tour need to do the operation
	 * @return int[]: the changed tour
	 */
	public int[] TwoOptExchange(int[] tour,int position1, int position2){
		//created the new tour
		int[] newtour = new int[tour.length];
		//initial the start city
		newtour[0] = 0;
		newtour[newtour.length-1] = 0;
		//start create the new tour after the first city
		int index = 1;
		//put the tour before position 1 into the new tour
		if(position1 != 1){
			for(int i = 1; i < position1; i++){
				newtour[index] = tour[i];
				index++;
			}
		}
		//put position1 into the new tour
		newtour[index] = tour[position1];
		index++;
		//reverse the tour between position 1 and position 2 then put it into the new tour
		for(int i = position2; i >position1; i--){
			newtour[index] = tour[i];
			index++;
		}
		//put the tour after position 2 into the new tour
		if(position2 != newtour.length-2){
			for(int i = position2 + 1; i < newtour.length-1; i++){
				newtour[index] = tour[i];
				index++;
			}
		}
		return newtour;
	}
}
