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
	private static double defaultDeathRate;
	private double actualDeathRate;
	private static int deathRandNum = 1000;
	private static double agingDeathMod;
	private static double hungerDeathMod;
	private static int lastMealLow = 15;
	private static int lastMealMed;
	private static int lastMealHigh;
	protected static int repRandNum = 1000;
	protected int eatingChance;
	private static int repAge;
	private static double defaultRepRate = .20;
	private static double actualRepRate;
	private Bag seen;
	protected double diseaseRecovery = .25;

	/*
	 * Purpose: Constructor
	 * Input: State of the world, world, and its assigned ID
	 */
	Prey(SimState state, SparseGrid2D grid, int num){
	
		int directionNum= state.random.nextInt(3);
		if(directionNum == 0)
			direction = 0;
		else if(directionNum == 1)
			direction = 1;
		else if (directionNum == 2)
			direction = 2;
		else
			direction = 3;
		
		oldAge = 10;
		/*
		vP = new VisualProcessor(state);
		map = new ExpectationMap(grid.getWidth(), grid.getHeight(), expectMapDecayRate);
		maxHunger = 30;
		maxSocial = 30;
		maxRep = 200;
		actualRepRate = defaultRepRate;
		actualDeathRate = defaultDeathRate;
		reproductionAge = repAge;
		*/
		ID = "R" + num;
		dir.mkdirs();
		
		try
		{
			outputFile = new File(dir, ID + ".csv");
			
			writer = new FileWriter(outputFile);
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
		agingDeathMod = agDM;
		hungerDeathMod = hDM;
		lastMealLow = lmL;
		lastMealMed = lmM;
		lastMealHigh = lmH;
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
	public void step(SimState state) {
	 super.step(state);
	 
	 //write();
	 //Chance of Disease recovery
	/*if(this.isDiseased && ((state.schedule.getTime() - diseaseTimestep) > lastMealLow)){
		 	double d = state.random.nextInt(diseaseRandomNum);
			double disease = d/diseaseRandomNum; 
			
			if(disease < diseaseRecovery)
				this.isDiseased = false;
	 }
		
	 */
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
	 
	 /**Learning **/

	/*	Int2D cord = grid.getObjectLocation(this);
		Bag result = new Bag();
		IntBag xPos = new IntBag();
		IntBag yPos = new IntBag();
		
		
		grid.getMooreNeighbors(cord.x, cord.y, 1, Grid2D.TOROIDAL, result, xPos, yPos);
		
		write("Prey Position: " + cord.x + "," + cord.y + ": ");
		boolean first = true;
		for(int i = 0; i < result.numObjs; i++)
		{
			Object temp = result.get(i);
			//write("Result: " + temp);
			
			if(temp instanceof Predator && first == true){
				
				Int2D pred = grid.getObjectLocation(temp);
				write("Located: " + pred.x + " " + pred.y);
				int deltaX = pred.x - cord.x;
				int deltaY = pred.y - cord.y;
				
				Int2D opp = this.find_Opp(cord, pred);
				this.setMovementPref(cord, opp, state);
				first = false;
			}
			
			
		}
		
		for(int i = 0; i < result.numObjs; i++)
		{
			Object temp = result.get(i);
			
			if(temp instanceof Food && first == true)
			{
				Int2D food = grid.getObjectLocation(temp);
				this.setMovementPref(cord, food, state);
				first = false;
			}
		}
	 */
	 
	 //Chance of Eating
	 if(this.willEat(grid, state)){
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
	
	//Method which determines whether or not the Prey will eat on location.
	public boolean willEat(SparseGrid2D grid, SimState state){
		
		/*if(lastMeal < lastMealLow)
			return false;
		else if(lastMeal < lastMealMed){
			if(state.schedule.getTime()%eatingChance != 0)
				return false;
		}
		*/
		//Eating Food on the same location
		assert(grid.getObjectsAtLocationOfObject(this) !=null);
			//write("Num of objects at location: " + grid.getObjectsAtLocationOfObject(this));
		
			int gridNum = grid.getObjectsAtLocationOfObject(this).size();
			
			
			for(int i = 0; i < gridNum; i++){
				Object obj = (grid.getObjectsAtLocationOfObject(this)).get(i);
				if(obj.getClass().equals(Food.class) && obj != null){
					//write("Prey would have eate");
					this.eat(obj, state);
					return true;
				}// end of if
			}// end of for loop
		
		
		return false;
	}

	//Method for actually eating/ removing food from the grid.
	public void eat(Object p, SimState state){
		
		//write(p);
			Food food = (Food) p;
			assert(food != null);
			/*if(food.isDiseased()){
				this.setDisease(true);
				this.diseaseTimestep = state.schedule.getTime();
			}*/
			//write(this + " ate " + p);
			/*food.amount = food.amount - .9;
			if(food.amount <0){
				//amount = 0.0;
				//may be a point where it is being removed, but not stopped.
				food.stop.stop();
				grid.remove(food);
				
			}*/
			lastMeal = 0;
			
			//write("Food is removed");
		
	}
	
	//This was used when we were using disease to determine emotion
	public void setDiseased(boolean dis)
	{
		isDiseased = dis;
	}
	
	/*
	 * Reproduces new prey
	 */
	public void reproduce(SimState state)
	{
		
		Prey p = new Prey(state, grid, numPrey + 1);
		numPrey++;
		grid.setObjectLocation(p, grid.getObjectLocation(this));
		Stoppable stop = state.schedule.scheduleRepeating(p);
		p.makeStoppable(stop);
	}
	
	/*
	 * Returns whether or not the animal is diseased
	 */
	public boolean isDiseased(){
		return isDiseased;
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
		/*//write("deathRate: " + deathRate);
	 	
	 	if(lastMeal > lastMealHigh){
			 stop.stop();
			 numPrey--;
			 grid.remove(this);
			 return true;
		 }*/
	 	
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
	/*public void vision(SimState state, SparseGrid2D grid){
		
		Int2D cord = grid.getObjectLocation(this);
		assert(cord != null);

		seen = vP.sight(cord.x, cord.y, state, direction);
		Bag locations = new Bag();
		if(state.schedule.getTime()%2 != 0)
			map.updateMapsPrey(seen, grid);
		
		//map.printMaps();
		for(int s = 0; s < seen.size(); s++){
			
			Int2D obLoc = grid.getObjectLocation(seen.get(s));
	
			locations.add(obLoc);
			//write(" at location:" + obLoc);
			//if(j.equals(Prey.class))
				//write("****" + seen.get(s));
			
		}
			
		this.behaviorProb(locations, seen, state);
		
		//Move every timestep
		super.move(grid, state);
		//write("Predator Moved");
	}// end of vision
	
	public void behaviorProb(Bag locs, Bag seen, SimState state){
	
		behavior = new BehaviorProcessor(grid);
		double[] newProb = behavior.updateProbPrey(locs, seen, defaultProb, this, state);
		
		actualProb = newProb;
	}
	public double getRepRate(){
		
		return actualRepRate;
	}
	
	public void setRepRate(double repRate){
		actualRepRate = repRate;
	}
	
	public void updateEmotions()
	{
		anger = anger.updateAnger(this);
		sad = sad.updateSadness(this);
		dis = dis.updateDisgust(this);
		fear = fear.updateFear(this);
		happy = happy.updateHappiness(this);
		surprise = surprise.updateSurprise(this);
		mood = mood.updateMood(anger, sad, dis, fear, happy);
	}
	
	public void printStats()
	{
		write(", " + ID);
		map.printMaps();
		write(", lastMeal: " + lastMeal);
		write(", deathRate " + actualDeathRate);
		write(", lastSocial: " + lastSocial);
		write(", directionChange: " + directChangeTotal + "\n");
	}*/
}
