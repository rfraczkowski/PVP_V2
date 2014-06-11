package sim.app.PVP_V2.src.pvp;

/**
 * Purpose: This class is the Predator implementation of
 * 		the Animal Class
 * @rachelfraczkowski
 */
import java.io.*;

import java.io.FileWriter;

import sim.engine.*;
import sim.field.grid.Grid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;
import sim.util.IntBag;

public class Predator extends Animal implements Steppable{

	//Constants, may or may not be currently used.
	private static double defaultDeathRate;
	private double actualDeathRate;
	private static int deathRandNum = 1000;
	private static double agingDeathMod;
	private static double hungerDeathMod;
	private static int lastMealLow = 30;
	private static int lastMealMed;
	private static int lastMealHigh;
	private static int repAge;
	protected static int repRandNum = 100000;
	protected static int defaultRepRandNum;
	protected int eatingChance;
	private static double actualRepRate;
	private static double defaultRepRate = .1;
	private Bag seen;
	protected double diseaseRecovery = .25;
	private boolean caught = false;



	/*
	 * Constructor
	 * Input: state of the world, world, and ID number
	 * Output: None
	 */
	Predator(SimState state, SparseGrid2D grid, int num, double[][] parentLearn){
		
		int directionNum= state.random.nextInt(3);
		if(directionNum == 0)
			direction = 0;
		else if(directionNum == 1)
			direction = 1;
		else if (directionNum == 2)
			direction = 2;
		else
			direction = 3;
		
		oldAge = 20;
		learnedProb = parentLearn;

		ID = "F" + num;
		
		/******************For writing new files for each predator in testing ***************/
		//dir.mkdirs();
		
		/*
		try
		{
			outputFile = new File(dir, ID + ".csv");
			writer = new FileWriter(outputFile);
			write("AgentPosX, AgentPosY, FoodX, FoodY, DeltaX, DeltaY, Direction, Slope, Slope, Before Position: 0, 1, 2, 3, 4, 5, 6, 7, 8, Sum, After Pos: 0, 1, 2, 3, 4, 5, 6, 7, Sum, EmotionRate");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}*/
	}

	/*
	 * Purpose: Initializes predator given set of parameters 
	 * Input: Parameters
	 * Output:None
	 */
	protected final static void initializePred(int maxH, int old,
			double dR, int dRN, double agDM, double hDM, int lmL, int lmM, int lmH, int rA, double dRR, int rRN ){
	
		maxHunger = maxH;
		//oldAge = old;
		defaultDeathRate = dR;
		deathRandNum = dRN;
		agingDeathMod = agDM;
		hungerDeathMod = hDM;
		lastMealLow = lmL;
		lastMealMed = lmM;
		lastMealHigh = lmH;
		repAge = rA;
		defaultRepRate = dRR;
		defaultRepRandNum = rRN;
		
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
		// TODO Auto-generated method stub
		super.step(state);
		
		//Boolean to see if caught prey
		caught = false;
		
		/* //Chance of Disease recovery
		 if(this.isDiseased && ((state.schedule.getTime() - diseaseTimestep) > lastMealLow)){
			 	double d = state.random.nextInt(diseaseRandomNum);
				double disease = d/diseaseRandomNum; 
				
				if(disease < diseaseRecovery)
					this.isDiseased = false;
		 }
		 
		// Timesteps since last social interaction
		//write("Last Meal: " + lastMeal + " timesteps");
		*/

		
		/****** LEARNING/ VISION ********************/
		
		//Empty bags to be filled with objects 
		Int2D cord = grid.getObjectLocation(this);
		Bag result = new Bag();
		IntBag xPos = new IntBag();
		IntBag yPos = new IntBag();
		
		//Get moore neighbors of the predators and fill them in appropriate bags
		grid.getMooreNeighbors(cord.x, cord.y, 1, Grid2D.TOROIDAL, result, xPos, yPos);
		
		//Used to determine first prey ever seen
		boolean first = true;
		
		//Iterates through all the objects, determines finds the first prey
		//seen, then updates movement based on that probability.
		for(int i = 0; i < result.numObjs; i++)
		{
			Object temp = result.get(i);
			//write("Result: " + temp);
			
			if(temp instanceof Prey && first == true){
				
				//write("Found Food! : ");
				Int2D prey = grid.getObjectLocation(temp);
				//write(prey.x + "," + prey.y + ",");
				int deltaX = prey.x - cord.x;
				int deltaY = prey.y - cord.y;
				
				int direction = deltaX + deltaY;
				//write(deltaX + "," + deltaY + ",");
				
				//write(direction + ",");
				
				this.setMovementPref(cord, prey, state);
				first = false;
			}
			
			
		}
		
		//Sees if there is a prey on current location, then eats it/ removes
		//it from grid if it is.
		if(this.willEat(grid, state))
		{
			return;
		}
		
		
		
		//Death Calculations -- sees if predator will die this timestep,
		//then removes it if so.
		if(this.iDie(state))
		{
			return;
		}
		
		//Reproduction Calculations -- sees if predator will reproduce this timestep,
		//reproduces the predator if so
		else if(this.iReproduce(state))
		{
			return;
		}
		

	}

	/*
	 * Purpose: Determines whether the predator will eat or not
	 * Input: Grid and State of World
	 * Side Effects: Calls eat method if necessary
	 */
	public boolean willEat(SparseGrid2D grid, SimState state)
	{
			
		//Reduced chance of eating. 
		
		/*if(lastMeal < lastMealLow)
			actualRepRate = actualRepRate * 1.5;
		
		if(state.schedule.getTime()%eatingChance != 0)
			return false;
		*/
		//Eating Prey on the same location
		assert(grid.getObjectsAtLocationOfObject(this) !=null);
		
		
		//write(grid.getObjectsAtLocationOfObject(this).size());
		int gridNum = grid.getObjectsAtLocationOfObject(this).size();
			
		assert(gridNum != 0);
			
			for(int i = 0; i < gridNum; i++){
				Object obj = (grid.getObjectsAtLocationOfObject(this)).get(i);
				
				if(obj.getClass().equals(Prey.class)){
					//write("\nPredator Ate");
					//write("Predator ate");
					this.eat(obj, state);
					return true;
				}// end of if
			}// end of for loop
		
		
		return false;
	}
	
	/*
	 * Purpose: Method that allows Predator to kill its Prey
	 * Input: object (prey), and state of world 
	 * Side Effects: Removes prey from world if same location as predator (non-Javadoc)
	 * @see sim.app.PVP_V2.src.pvp.Animal#eat(java.lang.Object, sim.engine.SimState)
	 */
	public void eat(Object p, SimState state)
	{
		assert (p != null);
		
		
		if(p.getClass().equals(Prey.class))
		{
		
			Prey prey = (Prey) p;
			assert(prey != null);
			if(prey.isDiseased()){
				this.setDisease(true);
				this.diseaseTimestep = state.schedule.getTime();
			}
			caught = true;
			lastMeal = 0;
			prey.stop.stop();
			emotions += eRate;
			numPrey--;
			preyCaught++;
			grid.remove(prey);
			

		}
			
	}
	
	
	/*
	 * Purpose: Method that "kills" the Predator by removing it from the grid based on factors
	 * Input: State of World
	 * Side Effect: Death of predator
	 */
	public boolean iDie(SimState state)
	{
		
		 //older = more likely to die
		 //if(age>oldAge)
		 	//actualDeathRate = actualDeathRate * agingDeathMod;
		 	
		 //Last meal, more likely to die
		 if(lastMeal > lastMealMed)
			actualDeathRate = actualDeathRate * (hungerDeathMod);
		 
		// Death Rate
		double d = state.random.nextInt(deathRandNum);
		double death = d/deathRandNum;
		
		assert(d >= 0 && death >=0);
		
		//write("d: " + d + " death: " + death);
		if(death < actualDeathRate){
			stop.stop();
			numPredator--;
			grid.remove(this);
			
				
			return true;
		}
		
		
		return false;
	}
	
	/*
	 * Purpose: Method that allows predator to have the chance to reproduce
	 * Input: State of the World
	 * Side Effects: Reproduced Predator if modifications allow
	 */
	public boolean iReproduce(SimState state)
	{
		// Reproduction Rate
		double r = state.random.nextInt(repRandNum);
		double repo = r/repRandNum;
				
		assert (r >= 0 && repo >= 0);
		if(repo <= actualRepRate && age >= repAge && numPredator<=maxPredator)
		{
			this.reproduce(state);
			this.lastRep = 0;
			return true;
		}
		
		return false;
	}
	

	/*
	 * Purpose: Method that allows Predator to duplicate
	 * Input: State of the World
	 * Side Effects: Actually creates new predator with learned probability(non-Javadoc)
	 * @see sim.app.PVP_V2.src.pvp.Animal#reproduce(sim.engine.SimState)
	 */
	public void reproduce(SimState state)
	{
		
		//write("Predator Reproduced");
		
		Predator p = new Predator(state, grid, numPredator + 1, learnedProb);
		numPredator++;
		grid.setObjectLocation(p, grid.getObjectLocation(this));
		Stoppable stop = state.schedule.scheduleRepeating(p);
		p.makeStoppable(stop);
	}
}
