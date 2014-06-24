/***
* Rachel Fraczkowski
* This is the non-visual version of the simulation. 
***/

package sim.app.PVP_V2;

import java.io.File;
import java.io.IOException;

import sim.engine.*;
import sim.util.*;
import sim.field.grid.*;
import ec.util.*;


@SuppressWarnings("serial")
public class PVP_2 extends SimState{

	//world
	public SparseGrid2D world;
	//dimensions of the world
	private static int gridWidth;
	private static int gridHeight;
	
	//Population Numbers
	private static int numPred; //only holds initial value, Animal data member is updated over time
	private static int numPrey; //only holds initial value, Animal data member is updated over time
	private static int numFood;
	
	//Rates
	//private static double expectationMapDecay;
	static double interval = 500; //how often stats are output; value can be changed by parameter
	private final static double FOOD_POP_RATE = .1;
	
	//Number of Clusters
	private static int clusters; //changed from final so that it could be a parameter
	private static int [][] clust; //should this be a local variable? Is it just used to determine appropriate locations?
	
	//File I/O
	protected static File dir; //changed to argument = new File("." ); //runs/presentation changed to current directory
	protected static String filename; //filename for output, so that it can be used as a parameter
	
	//Sets up the parameters of the world
	public PVP_2(long seed)
	{
		super(seed);
		//clusters = 5;
		//System.out.println("Grid Area: " + gridArea + " numFood: " + numFood);
		if(clusters > 0)
			clust = new int[clusters][2];
	}
	
	public static void initializeUI(int gridW, int gridH, int prey, int pred){
		gridWidth = gridW;
		gridHeight = gridH;
		numPrey = prey;
		numPred = pred;
		int gridArea = (gridWidth*gridHeight);
		numFood = (int) (gridArea * FOOD_POP_RATE);
		//expectationMapDecay = exMap;
	}
	
	//Populates the world with food, prey and predators
	public void start()
	{
		super.start();
		//set up random number generator to get to better random seeds
		for(int i=0; i<6000; i++)
			random.nextInt();
		
		world = new SparseGrid2D(gridWidth, gridHeight);
		//grid.clear();
		Animal.initialize(numPrey, numPred, dir, filename);
		//ONLY RANDOM NUMBER GENERATOR
		//MersenneTwisterFast twister = new MersenneTwisterFast(); //changed to use default SimState generator
		
		
		MutableInt2D loc = new MutableInt2D();
		
		/* create food */
		for(int f = 0; f < (numFood); f++){
			
			loc.x = world.tx(random.nextInt());
			loc.y = world.ty(random.nextInt());
			
			//Placing them at random places around the initial food
			Food p = new Food();
			//int direction = twister.nextInt(7);
				
			world.setObjectLocation(p, loc.x, loc.y);
			Stoppable stop = schedule.scheduleRepeating(p);
			p.makeStoppable(stop);
		} // end of for
		//System.out.println("Clusters: " + clusters);
		
		if(clusters > 0)
			createClusters();

		/* create predators */
		for(int i=0; i<numPred; i++)
		{
			Predator p = new Predator(this);
			
			//Torodial random locations
			MutableInt2D loc2 = new MutableInt2D();
			loc2.x = world.tx(random.nextInt());
			loc2.y = world.ty(random.nextInt());
			
			//System.out.println("loc x : " + loc.x + " loc.y: " + loc.y);
			world.setObjectLocation(p, new Int2D(loc2.x,loc2.y));
			Stoppable stop = schedule.scheduleRepeating(p);
			p.makeStoppable(stop);
			//System.out.println(world.getObjectLocation(p));
			
		}
		
		/* create prey */
		for(int j=0; j<numPrey; j++)
		{
			Prey prey = new Prey(this);
			
			//Torodial random locations
			MutableInt2D loc3 = new MutableInt2D();
			loc3.x = world.tx(random.nextInt());
			loc3.y = world.ty(random.nextInt());
			
			world.setObjectLocation(prey, new Int2D(loc3.x, loc3.y));
			Stoppable stop = schedule.scheduleRepeating(prey);
			prey.makeStoppable(stop);
			
			//System.out.println(world.getObjectLocation(prey));
		}
		
		//create statistics agent to be scheduled after all agents at every timestep at interval
		StatisticsAgent stat = new StatisticsAgent(interval);
		Stoppable stop = schedule.scheduleRepeating(0.0,1, stat,interval); //time, ordering, agent, interval
		//not necessary as only stops when simulation ended: stat.makeStoppable(stop);
		
	}
	
	/**
	 * called automatically by MASON when the simulation ends
	 * closes output stream, and outputs to standard out all learning arrays
	 * @author olsen
	 */
	public void finish()
	{
		super.finish();
		try
		{
			Animal.writer.close(); //close overall file writing stream
			
			//output final learning probabilities for every animal agent still alive
//			Bag objs = world.getAllObjects();
//			for( Object x : objs)
//			{
//				if(! (x instanceof Food))
//				{
//					Animal a = (Animal) x;
//					a.printLPSO(this);
//				}
//			}
		}
		catch(IOException e)
		{
			System.err.print("Failure in closing writer stream from Animal");
		}
	}
	
	/**
	 * Creates clustered food when desired
	 */
	public void createClusters()
	{
		//Clustered Visual Food - FIRST SET
		for(int h = 0; h < clusters; h++){
			
			
			MutableInt2D fLoc = new MutableInt2D();
			fLoc.x = world.tx(random.nextInt());
			fLoc.y = world.ty(random.nextInt());
			
			clust[h][0] = fLoc.x;
			clust[h][1] = fLoc.y;
				
			Food p = new Food();
				
				
			world.setObjectLocation(p, fLoc.x, fLoc.y);
			Stoppable stop = schedule.scheduleRepeating(p);
			p.makeStoppable(stop);
		}
			
		//Expanding on these sets
		for(int l = 0; l < clusters; l++){
				
			//System.out.println("Cluster: " + l);
				
			int xcord = clust[l][0];
			int ycord = clust[l][1];
			
			//System.out.println("NumFood/Clusters: " + numFood/clusters);
			
			for(int f = 0; f < (numFood/ clusters); f++){
				
				//Placing them at random places around the initial food
				Food p = new Food();
				int direction = random.nextInt(3);
				
				if(direction == 0)	
				{
					world.setObjectLocation(p, world.tx(xcord), world.ty(ycord + 1));
					Stoppable stop = schedule.scheduleRepeating(p);
					p.makeStoppable(stop);
					ycord =world.ty(ycord + 1);
				}
				else if(direction == 1)
				{
					world.setObjectLocation(p, world.tx(xcord), world.ty(ycord - 1));
					Stoppable stop = schedule.scheduleRepeating(p);
					p.makeStoppable(stop);
					ycord =world.ty(ycord - 1);
				}
				else if(direction ==2)
				{
					world.setObjectLocation(p, world.tx(xcord + 1), world.ty(ycord));
					Stoppable stop = schedule.scheduleRepeating(p);
					p.makeStoppable(stop);
					xcord =world.tx(xcord + 1);
				}
				else
				{
					world.setObjectLocation(p, world.tx(xcord - 1), world.ty(ycord));
					Stoppable stop = schedule.scheduleRepeating(p);
					p.makeStoppable(stop);
					xcord =world.tx(xcord - 1);
				}
			} // end of for*/
		} // end of clusters
	}
	
	/**
	 * Returns the number of clusters in the simulation
	 * @return int clusters (data member)
	 */
	public int getClusters()
	{
		return clusters;
	}
	/**
	 * Returns the width of the world
	 * @return int gridWidth
	 */
	protected static int getWidth()
	{
		return gridWidth;
	}
	/**
	 * Returns the height of the world
	 * @return int gridHeight
	 */
	protected static int getHeight()
	{
		return gridHeight;
	}
	/**
	 * Runs the simulation using the built in "doLoop" that steps through scheduled agents.
	 * @param args
	 */
	public static void main(String[] args)
	{

		short p = -1; //count of which parameter is being input, so it's easy to modify this list
		//Parameters get assigned here
		dir = new File(args[++p]);
		filename = args[++p];
		//World size
		gridWidth = Integer.parseInt(args[++p]);
		gridHeight = Integer.parseInt(args[++p]);
		//Number of Prey and Predator
		boolean preyLearn = Boolean.parseBoolean(args[++p]);
		boolean predLearn = Boolean.parseBoolean(args[++p]);
		interval = Double.parseDouble(args[++p]);
		numPrey = Integer.parseInt(args[++p]);
		numPred = Integer.parseInt(args[++p]);
		numFood = Integer.parseInt(args[++p]);
		clusters = Integer.parseInt(args[++p]);
		
		Food.setNumFood(numFood);
		
		//Expectation Decay Rate
//		expectationMapDecay = Double.parseDouble(args[++p]);
		
		//Prey Only Parameters
//		int preyMaxHunger = Integer.parseInt(args[++p]);
		//int preyOldAge = Integer.parseInt(args[++p]);
		double preyDeathRate = Double.parseDouble(args[++p]);
//		int preyDeathRandNum = Integer.parseInt(args[++p]);
		//double preyAgingDeathMod = Double.parseDouble(args[++p]);
		//double preyHungerDeathMod = Double.parseDouble(args[++p]);
		int preyLastMealLow = Integer.parseInt(args[++p]);
		//int preyLastMealMed = Integer.parseInt(args[++p]);
		//int preyLastMealHigh = Integer.parseInt(args[++p]);
		int preyRepAge = Integer.parseInt(args[++p]);
		double preyRepRate = Double.parseDouble(args[++p]);
		//int preyRepRandNum = Integer.parseInt(args[++p]);
		
		Prey.setDeathRate(preyDeathRate);
		Prey.setLastMealLow(preyLastMealLow);
		Prey.setrepAge(preyRepAge);
		Prey.setRepRate(preyRepRate);
		Prey.setLearn(preyLearn);
		
		// Predator Only Parameters
//		int predMaxHunger = Integer.parseInt(args[++p]);
		//int predOldAge = Integer.parseInt(args[++p]);
		double predDeathRate = Double.parseDouble(args[++p]);
//		int predDeathRandNum = Integer.parseInt(args[++p]);
		//double predAgingDeathMod = Double.parseDouble(args[++p]);
		//double predHungerDeathMod = Double.parseDouble(args[++p]);
		//int predLastMealLow = Integer.parseInt(args[++p]);
		int predLastMealMed = Integer.parseInt(args[++p]);
		//int predLastMealHigh = Integer.parseInt(args[++p]);
		int predRepAge = Integer.parseInt(args[++p]);
		double predRepRate = Double.parseDouble(args[++p]);
		//int predRepRandNum = Integer.parseInt(args[++p]);
		
		Predator.setDeathRate(predDeathRate);
		Predator.setLastMealLow(predLastMealMed);
		Predator.setrepAge(predRepAge);
		Predator.setRepRate(predRepRate);
		Predator.setLearn(predLearn);
		
//		Prey.initializePrey(preyMaxHunger, preyOldAge, preyDeathRate, preyDeathRandNum, preyAgingDeathMod,
//				preyHungerDeathMod, preyLastMealLow, preyLastMealMed, preyLastMealHigh, preyRepAge,
//				preyDefaultRepRate, preyRepRandNum);
//		
//		Predator.initializePred(predMaxHunger, predOldAge, predDeathRate, predDeathRandNum, predAgingDeathMod,
//				predHungerDeathMod, predLastMealLow, predLastMealMed, predLastMealHigh, predRepAge,
//				predDefaultRepRate, predRepRandNum);

		
		doLoop(PVP_2.class, args);
		System.exit(0);
	}
}

