/***
* Rachel Fraczkowski
* This is the non-visual version of the simulation. 
***/

package sim.app.PVP_V2.src.pvp;

import java.io.File;

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
	private static int gridArea = 0;
	//Rates and Numbers
	private final static double FOOD_POP_RATE = .1;
	private static int numPred;
	private static int numPrey;
	//private static double expectationMapDecay;
	private static int numFood;
	protected static File dir; //changed to argument = new File("." ); //runs/presentation changed to current directory
	protected static String filename; //filename for output, so that it can be used as a parameter

	//Number of Clusters
	private final int clusters;
	private final int [][] clust;
	
	//Sets up the parameters of the world
	public PVP_2(long seed)
	{
		super(seed);
		clusters = 5;
		//System.out.println("Grid Area: " + gridArea + " numFood: " + numFood);
		clust = new int[clusters][2];
	}
	
	public static void initializeUI(int gridW, int gridH, int prey, int pred){
		gridWidth = gridW;
		gridHeight = gridH;
		numPrey = prey;
		numPred = pred;
		gridArea = (gridWidth*gridHeight);
		numFood = (int) (gridArea * FOOD_POP_RATE);
		//expectationMapDecay = exMap;
	}
	
	//Populates the world with food, prey and predators
	public void start()
	{
		super.start();
		world = new SparseGrid2D(gridWidth, gridHeight);
		//grid.clear();
		Animal.initialize(numPrey, numPred, dir, filename);
		//ONLY RANDOM NUMBER GENERATOR
		MersenneTwisterFast twister = new MersenneTwisterFast();
		
		
		MutableInt2D loc = new MutableInt2D();
		
	
		for(int f = 0; f < (numFood); f++){
			
			loc.x = world.tx(twister.nextInt());
			loc.y = world.ty(twister.nextInt());
			
			//Placing them at random places around the initial food
			Food p = new Food();
			//int direction = twister.nextInt(7);
				
			world.setObjectLocation(p, loc.x, loc.y);
			Stoppable stop = schedule.scheduleRepeating(p);
			p.makeStoppable(stop);
		} // end of for
		//System.out.println("Clusters: " + clusters);
		
		createClusters(twister);

		for(int i=0; i<numPred; i++)
		{
			Predator p = new Predator(this, i);
			
			//Torodial random locations
			MutableInt2D loc2 = new MutableInt2D();
			loc2.x = world.tx(twister.nextInt());
			loc2.y = world.ty(twister.nextInt());
			
			//System.out.println("loc x : " + loc.x + " loc.y: " + loc.y);
			world.setObjectLocation(p, new Int2D(loc2.x,loc2.y));
			Stoppable stop = schedule.scheduleRepeating(p);
			p.makeStoppable(stop);
			//System.out.println(world.getObjectLocation(p));
			
		}
		
		for(int j=0; j<numPrey; j++)
		{
			Prey prey = new Prey(this, j);
			
			//Torodial random locations
			MutableInt2D loc3 = new MutableInt2D();
			loc3.x = world.tx(twister.nextInt());
			loc3.y = world.ty(twister.nextInt());
			
			world.setObjectLocation(prey, new Int2D(loc3.x, loc3.y));
			Stoppable stop = schedule.scheduleRepeating(prey);
			prey.makeStoppable(stop);
			
			//System.out.println(world.getObjectLocation(prey));
		}
	}
	
	public void createClusters(MersenneTwisterFast twister)
	{
		//Clustered Visual Food - FIRST SET
		for(int h = 0; h < clusters; h++){
			
			
			MutableInt2D fLoc = new MutableInt2D();
			fLoc.x = world.tx(twister.nextInt());
			fLoc.y = world.ty(twister.nextInt());
			
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
				int direction = twister.nextInt(3);
				
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
		//gridWidth = Integer.parseInt(args[++p]);
		//gridHeight = Integer.parseInt(args[++p]);
		//Number of Prey and Predator
		numPrey = Integer.parseInt(args[++p]);
		numPred = Integer.parseInt(args[++p]);
		numFood = Integer.parseInt(args[++p]);
		//Expectation Decay Rate
//		expectationMapDecay = Double.parseDouble(args[++p]);
		
		//Prey Only Parameters
		int preyMaxHunger = Integer.parseInt(args[++p]);
		//int preyOldAge = Integer.parseInt(args[++p]);
		double preyDeathRate = Double.parseDouble(args[++p]);
		int preyDeathRandNum = Integer.parseInt(args[++p]);
		//double preyAgingDeathMod = Double.parseDouble(args[++p]);
		//double preyHungerDeathMod = Double.parseDouble(args[++p]);
		//int preyLastMealLow = Integer.parseInt(args[++p]);
		int preyLastMealMed = Integer.parseInt(args[++p]);
		//int preyLastMealHigh = Integer.parseInt(args[++p]);
		int preyRepAge = Integer.parseInt(args[++p]);
		double preyDefaultRepRate = Double.parseDouble(args[++p]);
		int preyRepRandNum = Integer.parseInt(args[++p]);
		
		
		// Predator Only Parameters
		int predMaxHunger = Integer.parseInt(args[++p]);
		//int predOldAge = Integer.parseInt(args[++p]);
		double predDeathRate = Double.parseDouble(args[++p]);
		int predDeathRandNum = Integer.parseInt(args[++p]);
		//double predAgingDeathMod = Double.parseDouble(args[++p]);
		//double predHungerDeathMod = Double.parseDouble(args[++p]);
		//int predLastMealLow = Integer.parseInt(args[++p]);
		int predLastMealMed = Integer.parseInt(args[++p]);
		//int predLastMealHigh = Integer.parseInt(args[++p]);
		int predRepAge = Integer.parseInt(args[++p]);
		double predDefaultRepRate = Double.parseDouble(args[++p]);
		int predRepRandNum = Integer.parseInt(args[++p]);
		
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

