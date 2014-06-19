package sim.app.PVP_V2.src.pvp;

import sim.engine.*;
import sim.field.grid.Grid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;
import sim.util.IntBag;
import java.io.*;
/**
 * This class represents the prey implementation of animal.
 * @author rachelfraczkowski
 *
 */
public class Prey extends Animal implements Steppable{

	//Constants from summer not all are used. 
	private static final long serialVersionUID = 1L;
	
	//static data members
	private static double defaultDeathRate;
	private static int deathRandNum = 1000;
//	private static double agingDeathMod;
	private static double hungerDeathMod = .05; //the increase to death likelihood based on hunger
	private static int repAge;//the age at which reproduction is possible
	private static double defaultRepRate = .20;
	private static double actualRepRate;
	private static int lastMealLow = 15;
//	private static int lastMealMed;
//	private static int lastMealHigh;
	private static double foodReduction = .9; //the amount by which food decreases when eaten
	protected static int repRandNum = 1000;
	
	//non-static data members
	private double actualDeathRate;
	protected int eatingChance;
	protected boolean predSeen = false;
	private Bag seen;
	protected double diseaseRecovery = .25;

	/*
	 * Purpose: Constructor for use at beginning of simulation ONLY
	 * Input: State of the world, world, and its assigned ID
	 */
	Prey(SimState state, int num)
	{
	
		direction = state.random.nextInt(3);
		
//		oldAge = 10;
		ID = "R" + num;
		
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
	 * Purpose: Constructor for creation of a child
	 * Input: State of the world, world, assigned ID, parent's learned probabilities
	 */
	Prey(SimState state, SparseGrid2D grid, int num, double[][] parentLearn)
	{
	
		direction = state.random.nextInt(3);
		
//		oldAge = 10;
		learnedProb = parentLearn;
		ID = "R" + num;
		
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
	protected final static void initializePrey(int maxH, int old,
			double dR, int dRN, double agDM, double hDM, int lmL, int lmM, int lmH, int rA, double dRR, int rRN )
	{
	
		maxHunger = maxH;
		//oldAge = old;
		defaultDeathRate = dR;
		deathRandNum = dRN;
	//	agingDeathMod = agDM;
		hungerDeathMod = hDM;
		lastMealLow = lmL;
	//	lastMealMed = lmM;
	//	lastMealHigh = lmH;
		repAge = rA;
		defaultRepRate = dRR;
		repRandNum = rRN;
		
	}
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
		assert(cord.x > 0);
		assert(cord.y > 0);
		
		//Get empty bags to be filled by Moore Neighborhood Method
		Bag result = new Bag();
		IntBag xPos = new IntBag();
		IntBag yPos = new IntBag();
		
		assert(this != null);

		//Fills the bags with the objects in the prey's moore neighborhood
		grid.getMooreNeighbors(cord.x, cord.y, 1, Grid2D.TOROIDAL, result, xPos, yPos);
		
		//Boolean to run away from first seen predator
		boolean first = true;
		
		//Goes through all of the objects in the visual, and sees what every object is,
		//and reacts accordingly.
		for(int i = 0; i < result.numObjs; i++)
		{
			Object temp = result.get(i);
			//write("Result: " + temp);
			
			if(temp instanceof Predator && first == true){
				
				//Get location of predator
				Int2D pred = grid.getObjectLocation(temp);
				//write(pred.x + "," + pred.y + ",");
				predSeen = true;
				//write(deltaX + "," + deltaY + ",");
				//emotions -= eRate;
				
				//write(direction + ",");
				
				Int2D opp = this.find_Opp(cord, pred);
				this.setMovementPref(cord, opp, state);
				first = false;
			}
			else
			{
				if(predSeen == true)
					predOutran++;
				predSeen = false;
			}
			
			
		}
		
		//Bag for multiple foods
		Bag food = new Bag();
		
		for(int i = 0; i < result.numObjs; i++)
		{
			Object temp = result.get(i);
			
			//If the prey sees food and not a predator, add it to change movement
			if(temp instanceof Food && first == true)
			{
				food.add(temp);
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
	 if(this.iReproduce(state)){
		 	//this.updateEmotions();
		   //this.printStats();
		 return;
	 }
	 /*
	 //See & process
	 else 
		 this.vision(state, grid);
		
	  	this.updateEmotions();
	//End of Step, print out tests
			this.printStats();*/
	}
	
	/*
	 * Purpose: Method which determines whether or not the Prey will eat on location.
	 * Input: Grid, and State of the Grid
	 * Output/ Side Effects: Makes the food eaten (or reduced in amount) if on the same location as the prey
	 */
	public boolean willEat(SparseGrid2D grid, SimState state){
		
		//Eating Food on the same location
		assert(grid.getObjectsAtLocationOfObject(this) !=null);
			//write("Num of objects at location: " + grid.getObjectsAtLocationOfObject(this));
		
			int gridNum = grid.getObjectsAtLocationOfObject(this).size();
			
			
			for(int i = 0; i < gridNum; i++){
				Object obj = (grid.getObjectsAtLocationOfObject(this)).get(i);
				if(obj.getClass().equals(Food.class) && obj != null){
					//write("Prey would have eaten");
					this.eat(obj, state);
					return true;
				}// end of if
			}// end of for loop
		
		
		return false;
	}

	/*
	 * Purpose: Method for actually eating/ removing food from the grid.
	 * Input: The object that the prey wants to eat (food) and the state of the world (non-Javadoc)
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
			food.amount = food.amount - foodReduction;
			if(food.amount <0){
				//amount = 0.0;
				//may be a point where it is being removed, but not stopped.
				food.stop.stop();
				grid.remove(food);
				
			}
			lastMeal = 0;
			
			//write("Food is removed");
		
	}
	
	/*
	 * Purpose: Reproduces new prey
	 * Input: State of world
	 * Side Effects: New Prey with learned Probability
	 */
	public void reproduce(SimState state)
	{
		
		Prey p = new Prey(state, grid, numPrey + 1, learnedProb);
		numPrey++;
		grid.setObjectLocation(p, grid.getObjectLocation(this));
		Stoppable stop = state.schedule.scheduleRepeating(p);
		p.makeStoppable(stop);
	}
	
	/*	Chance of death for the animal
	 * Input: SimState
	 * Output: None
	 * 
	 */
	public boolean iDie(SimState state){
		//older = more likely to die
	 	//if(age>oldAge)
	 		//actualDeathRate = actualDeathRate * agingDeathMod;
	 	
	 	//Last meal, more likely to die
	 	if(lastMeal > lastMealLow)
			actualDeathRate = actualDeathRate * (hungerDeathMod);
	
	 	
	 	// Death Rate
		double d = state.random.nextInt(deathRandNum);
		double death = d/deathRandNum;
		
		//write("d: " + d + " death: " + death);
		if(death < actualDeathRate && death != 0){
			this.stop.stop();
			numPrey--;
			grid.remove(this);
			return true;
		}
		return false;
	}
	
	/*
	 * Purpose: Whether or not reproducing
	 * Input: Sim State
	 * Output: None
	 */
	public boolean iReproduce(SimState state){
	// Reproduction Rate
		double r = state.random.nextInt(repRandNum);
		double repo = r/repRandNum;
		//if(repo <= actualRepRate && age >= repAge && numPrey <= maxPrey){
		if(repo <= actualRepRate){
			this.reproduce(state);
			return true;
			}
		return false;
	}
	/*
	 * Purpose: mutator for setting the last meal rate
	 * @param int the value to set lastMealLow to hold
	 */
	public static void setLastMealLow(int i)
	{
		lastMealLow = i;
	}
}


