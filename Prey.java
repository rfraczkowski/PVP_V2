package sim.app.PVP_V2;

import sim.engine.*;
import sim.field.grid.Grid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;
import sim.util.IntBag;
//import java.io.*;
/**
 * This class represents the prey implementation of animal.
 * @author rachelfraczkowski
 *
 */
public class Prey extends Animal implements Steppable{

	//Constants from summer not all are used. 
	private static final long serialVersionUID = 1L;
	
	//static data members
	private static int IDCounter = 0;
	private static double defaultDeathRate;
	private static int deathRandNum = 10000;
//	private static double agingDeathMod;
	private static double hungerDeathMod = 2; //the increase to death likelihood based on hunger
	private static int repAge;//the age at which reproduction is possible
//	private static double defaultRepRate = .20;
	private static double actualRepRate;
	private double actualDeathRate; //can't be static because may change agent by agent due to hunger
	private static int lastMealLow = 15;
//	private static int lastMealMed;
//	private static int lastMealHigh;
	private static double foodReduction = .2; //the amount by which food decreases when eaten
	protected static int repRandNum = 1000;
	private static boolean learn = true; //by default prey use learning
	
	//used for statistics only
	protected static int reproductionCollectPrey = 0;
	protected static int deathCollectPrey = 0;
	protected static int predOutran = 0; //increased in a step if there are fewer predators in sight than last time
	protected int predSeen = 0; //how many predators were seen in prior timestep, to determine if any were outrun 
	protected static int eatCount=0; //how often prey eat
	protected static int hunger=0; //total hunger level

//	protected int eatingChance;
//	private Bag seen;
//	protected double diseaseRecovery = .25;

	/**
	 * Purpose: Constructor for use at beginning of simulation ONLY
	 * @param State of the world
	 */
	Prey(SimState state)
	{
		
		direction = state.random.nextInt(3);
		
//		oldAge = 10;
		ID = "R" + IDCounter;
		IDCounter++; //increase for next agent
		actualDeathRate = defaultDeathRate;
		/***************Makes separate files for each prey agent**************************************/
		/*dir.mkdirs();
		
		try
		{
			outputFile = new File(dir, ID + ".csv");
			
			writer = new FileWriter(outputFile);
			write("AgentPosX, AgentPosY, FoodX, FoodY, DeltaX, DeltaY, Direction, Slope, Slope, Before Position: 0, 1, 2, 3, 4, 5, 6, 7, 8, Sum, After Pos: 0, 1, 2, 3, 4, 5, 6, 7, Sum, EmotionRate");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		//eatingChance = 1;*/
		
	//	System.out.println(ID +" " + actualDeathRate + " " + hungerDeathMod+ " " + repAge+ " " +eatingChance+ " " +actualRepRate+ " " +learn
	//			+ " " +maxHunger+ " " +lastRep+ " " +maxRep+ " " +prevLoc+ " " +age); //for testing initialization
	}
	
	/**
	 * Purpose: Constructor for creation of a child
	 * @param State of the world
	 * @param world
	 * @param parent's learned probabilities
	 */
	Prey(SimState state, SparseGrid2D grid, double[][] parentLearn)
	{
	
		direction = state.random.nextInt(3);
		
//		oldAge = 10;
		if(learn)
			copyLearnedProb(parentLearn);
		ID = "R" + IDCounter;
		IDCounter++; //increase for next prey
		actualDeathRate = defaultDeathRate; //initialize to parameter value
		numPrey++; //increase count of prey in simulation
		/***************Makes separate files for each prey agent**************************************/
		/*dir.mkdirs();
		
		try
		{
			outputFile = new File(dir, ID + ".csv");
			
			writer = new FileWriter(outputFile);
			write("AgentPosX, AgentPosY, FoodX, FoodY, DeltaX, DeltaY, Direction, Slope, Slope, Before Position: 0, 1, 2, 3, 4, 5, 6, 7, 8, Sum, After Pos: 0, 1, 2, 3, 4, 5, 6, 7, Sum, EmotionRate");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		//eatingChance = 1;*/
	}
	
	/*
	 * Purpose: Initializes prey given set of parameters 
	 * Input: Parameters
	 * Output:None
	 */
//	protected final static void initializePrey(int maxH, int old,
//			double dR, int dRN, double agDM, double hDM, int lmL, int lmM, int lmH, int rA, double dRR, int rRN )
//	{
//	
//		maxHunger = maxH;
//		//oldAge = old;
//	//	defaultDeathRate = dR;
//		deathRandNum = dRN;
//	//	agingDeathMod = agDM;
//		hungerDeathMod = hDM;
//		lastMealLow = lmL;
//	//	lastMealMed = lmM;
//	//	lastMealHigh = lmH;
//		repAge = rA;
//	//	defaultRepRate = dRR;
//		repRandNum = rRN;
//		
//	}
	/*
	 * Purpose: Needs to be stoppable in order for the simulation to end.
	 * Fixed this error by making this method.
	 * Input: Stoppable object
	 * Output: None
	 */
	public void makeStoppable(Stoppable stopper)
	{
		stop = stopper;
	}
	
	/*
	 * Purpose: This is a heart of the code/ movement for prey
	 * Input: State of the world
	 * Output: None -- minus visual update
	 */
	@Override
	public void step(SimState state) 
	{
	 super.step(state);
	 
	 /*********************Chance of Disease recovery*************************************/
	/*if(this.isDiseased && ((state.schedule.getTime() - diseaseTimestep) > lastMealLow)){
		 	double d = state.random.nextInt(diseaseRandomNum);
			double disease = d/diseaseRandomNum; 
			
			if(disease < diseaseRecovery)
				this.isDiseased = false;
	 }
		
	 */

	 
	 /**Learning for Prey**/

	 	
	 	//Location of this prey
		Int2D cord = grid.getObjectLocation(this);
		assert(cord != null);
		assert(cord.x >= 0);
		assert(cord.y >= 0);
		assert(cord.x < grid.getWidth());
		assert(cord.y < grid.getHeight());
		
		//Get empty bags to be filled by Moore Neighborhood Method
		Bag neighbors = new Bag();
		IntBag xPos = new IntBag();
		IntBag yPos = new IntBag();
		
		assert(this != null);

		//Fills the bags with the objects in the prey's moore neighborhood
		grid.getMooreNeighbors(cord.x, cord.y, 1, Grid2D.TOROIDAL, neighbors, xPos, yPos);
		neighbors.shuffle(state.random);
		
		//Boolean to run away from first seen predator
		boolean first = true;
		int countPredators = 0;
		
		//Goes through all of the objects in the visual, and sees what every object is,
		//and reacts accordingly.
		for(int i = 0; i < neighbors.numObjs; i++)
		{
			Object temp = neighbors.get(i);
			//write("Result: " + temp);
			
			if(temp instanceof Predator){
				countPredators++;
				if(learn && first) //all contents of this if statement are for learning based on first predator seen
				{
					//Get location of predator
					Int2D pred = grid.getObjectLocation(temp);
					//write(pred.x + "," + pred.y + ",");
					//predSeen = true;
					//write(deltaX + "," + deltaY + ",");
					//emotions -= eRate;
					
					//write(direction + ",");
					
					Int2D opp = this.find_Opp(cord, pred);
					this.setMovementPref(cord, opp, state);
					first = false;
				}
			}	
		}
		
		//determine if there are fewer neighboring predators this turn
		//if so, the difference is how many were outrun
		if(countPredators < predSeen)
			predOutran += predSeen - countPredators;
		predSeen = countPredators; //set to predators from this timestep for use in next timestep's calculations
		
		//Bag for multiple foods
		Bag food = new Bag();
		
		//System.out.println("---" + cord.x + " " + cord.y + "---");
		for(int i = 0; i < neighbors.numObjs; i++)
		{
			Object temp = neighbors.get(i);
			
			//If the prey sees food and not a predator, add it to change movement
			if(temp instanceof Food && first)
			{
				food.add(temp);
		//		System.out.println(grid.getObjectLocation(temp).x + " " + grid.getObjectLocation(temp).y);
				//emotions += 1;
			}
			
		}
		//If the prey sees more than one food, choose one food at random to be
		//the goal state.
		if(food.size()>0)
		{
			int random = state.random.nextInt(food.size());
			Food goal = (Food)food.remove(random);
			Int2D foodLoc = grid.getObjectLocation(goal);
			if (learn)
				this.setMovementPref(cord, foodLoc, state);
		}
		
	 
	 
	 //Chance of Eating if food is on this spot
	 if(this.willEat(grid, state)){
		 	//this.updateEmotions();
			//this.printStats();
		return;
	 }
	 
	 //write("," + emotions);
	 
	 
	 //Death Chance
	 if(this.iDie(state)){
		 	//this.updateEmotions();
			//this.printStats();
		 return;
	 }
	 
	 //Reproduction Chance
	 this.iReproduce(state);
	 hunger += lastMeal; //add to sum of predator hunger for this interval
	 /*
	 //See & process
	 else 
		 this.vision(state, grid);
		
	  	this.updateEmotions();
	//End of Step, print out tests
			this.printStats();*/
	}
	
	/**
	 * Purpose: Method which determines whether or not the Prey will eat on location.
	 * @param Grid of objects
	 * @param State of the simulation
	 * Side Effects: Makes the food eaten (or reduced in amount) if on the same location as the prey
	 */
	public boolean willEat(SparseGrid2D grid, SimState state){
		
		//Eating Food on the same location
		assert(grid.getObjectsAtLocationOfObject(this) !=null);
			//write("Num of objects at location: " + grid.getObjectsAtLocationOfObject(this));
		
		int gridNum = grid.getObjectsAtLocationOfObject(this).size();
		
		
		for(int i = 0; i < gridNum; i++){
			Object obj = (grid.getObjectsAtLocationOfObject(this)).get(i);
			if(obj != null && obj.getClass().equals(Food.class)){
				//write("Prey would have eaten");
				this.eat(obj, state);
				return true;
			}// end of if
		}// end of for loop
		
		
		return false;
	}

	/**
	 * Purpose: Method for actually eating/ removing food from the grid.
	 * @param The object that the prey wants to eat (food) 
	 * @param The state of the world 
	 * @see sim.app.PVP_V2.src.pvp.Animal#eat(java.lang.Object, sim.engine.SimState)
	 */
	public void eat(Object p, SimState state){
		
		//write(p);
		Food food = (Food) p;
		assert(food != null);
		//emotions += eRate;
		//write("Prey ate food");
		
		/***********This was when food was diseased *******************************/
		/*if(food.isDiseased()){
			this.setDisease(true);
			this.diseaseTimestep = state.schedule.getTime();
		}*/
		//write(this + " ate " + p);
		//the below code was removed in favor of using the identical code in Food class
//			food.amount = food.amount - foodReduction;
//			if(food.amount <0){
//				//amount = 0.0;
//				//may be a point where it is being removed, but not stopped.
//				food.stop.stop();
//				grid.remove(food);
//				
//			}
		food.eat(foodReduction);
		lastMeal = 0;
		eatCount++;
//		System.out.println("ate");
		//write("Food is removed");
		
	}
	
	/**
	 * Purpose: Reproduces new prey
	 * @param State of world
	 * Side Effects: New Prey with learned Probability
	 */
	public void reproduce(SimState state)
	{
		
		Prey p = new Prey(state, grid, learnedProb);
		reproductionCollectPrey++; //statistics only
		grid.setObjectLocation(p, grid.getObjectLocation(this));
		Stoppable stop = state.schedule.scheduleRepeating(p);
		p.makeStoppable(stop);
	//	System.out.println(lastMeal + " " + lastMealLow + " " + actualRepRate);
	}
	
	/**
	 * Chance of death for the animal
	 * @param SimState
	 * Output: None
	 */
	public boolean iDie(SimState state){
		
		if(lastMeal <= lastMealLow) //can only die if they are hungry
			return false;
		//older = more likely to die
	 	//if(age>oldAge)
	 		//actualDeathRate = actualDeathRate * agingDeathMod;
	 	
	 	//Last meal, more likely to die
	 	if(lastMeal > lastMealLow)
			actualDeathRate = actualDeathRate * (hungerDeathMod);
	 	else
	 		actualDeathRate = defaultDeathRate; //otherwise, how does it ever reset?
	
	 	
	 	// Death Rate
		double d = state.random.nextInt(deathRandNum);
		double death = d/deathRandNum;
		
		//write("d: " + d + " death: " + death);
		if(death < actualDeathRate){// && death != 0){
			this.stop.stop();
			numPrey--;
			deathCollectPrey++; //statistics only
			grid.remove(this);
			return true;
		}
		return false;
	}
	
	/**
	 * Purpose: Whether or not reproducing
	 * @param Sim State
	 * Output: None
	 */
	public boolean iReproduce(SimState state){
	// Reproduction Rate
		double r = state.random.nextInt(repRandNum);
		double repo = r/repRandNum;
		//if(repo <= actualRepRate && age >= repAge && numPrey <= maxPrey){
		if(repo <= actualRepRate && age >=repAge && lastMeal <= lastMealLow){
			this.reproduce(state);
			return true;
		}
		return false;
	}
	/**
	 * Purpose: mutator for setting the last meal rate
	 * @param int the value to set lastMealLow to hold
	 */
	public static void setLastMealLow(int i)
	{
		lastMealLow = i;
	}
	/**
	 * Purpose: mutator for setting the age at which reproduction is possible
	 * @param int the new value
	 */
	public static void setrepAge(int i)
	{
		repAge = i;
	}
	/**
	 * Purpose: mutator for setting the reproduction rate
	 * @param double the new value
	 */
	public static void setRepRate(double i)
	{
		actualRepRate = i;
	}
	/**
	 * Purpose: mutator for setting the death rate
	 * @param double the new value
	 */
	public static void setDeathRate(double i)
	{
		defaultDeathRate = i;
	}
	/**
	 * Purpose: mutator for setting whether or not the species learns
	 * @param boolean the new value
	 */
	public static void setLearn(boolean i)
	{
		learn = i;
	//	System.out.println(learn);//testing
	}
	/**
	 * Set hungerdeathmod
	 * @param i new value
	 */
	public static void setHungerMod(double i)
	{
		hungerDeathMod = i;
	}
}


