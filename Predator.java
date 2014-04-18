package sim.app.PVP_V2.src.pvp;

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
	private static double defaultRepRate = .05;
	private Bag seen;
	protected double diseaseRecovery = .25;
	private boolean caught = false;



	/*
	 * Constructor
	 * Input: state of the world, world, and ID number
	 * Output: None
	 */
	Predator(SimState state, SparseGrid2D grid, int num){
		
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

		//vP = new VisualProcessor(state);
		//map = new ExpectationMap(grid.getWidth(), grid.getHeight(), expectMapDecayRate);
		//maxHunger = 30;
		//maxSocial = 100;
		//actualRepRate = defaultRepRate;
		//reproductionAge = repAge;
		//maxRep = 200;
		//actualDeathRate = defaultDeathRate;
		//ID = "F" + num;
		//eatingChance = 1;
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
		
		caught = false;
		/*repRandNum = 1000;
		//System.out.println();
		 //Chance of Disease recovery
		 if(this.isDiseased && ((state.schedule.getTime() - diseaseTimestep) > lastMealLow)){
			 	double d = state.random.nextInt(diseaseRandomNum);
				double disease = d/diseaseRandomNum; 
				
				if(disease < diseaseRecovery)
					this.isDiseased = false;
		 }
		 
		// Timesteps since last social interaction
		//System.out.println("Last Meal: " + lastMeal + " timesteps");
		*/
		//Death Calculations
		if(this.iDie(state)){
		 	//this.emotionsUpdate();
			this.printStats();
			return;
		}
		
		//Reproduction Calculations
		else if(this.iReproduce(state)){
		 	//this.emotionsUpdate();

			//this.printStats();
			return;
		}
		
		/****** LEARNING/ VISION ********************/
		Int2D cord = grid.getObjectLocation(this);
		Bag result = new Bag();
		IntBag xPos = new IntBag();
		IntBag yPos = new IntBag();
		
		
		grid.getMooreNeighbors(cord.x, cord.y, 1, Grid2D.TOROIDAL, result, xPos, yPos);
		
		System.out.println("Agent Position: " + cord.x + "," + cord.y + ": ");
		boolean first = true;
		for(int i = 0; i < result.numObjs; i++)
		{
			Object temp = result.get(i);
			//System.out.println("Result: " + temp);
			
			if(temp instanceof Prey && first == true){
				
				System.out.println("Found Food! : ");
				Int2D prey = grid.getObjectLocation(temp);
				System.out.println("Located: " + prey.x + " " + prey.y);
				int deltaX = prey.x - cord.x;
				int deltaY = prey.y - cord.y;
				
				int direction = deltaX + deltaY;
				System.out.println("DeltaX: " + deltaX + " DeltaY: " + deltaY);
				
				System.out.println("Direction: " + direction);
				
				this.setMovementPref(cord, prey, state);
				first = false;
			}
			
			
		}
		
		//Will I eat?
		if(this.willEat(grid, state)){
		 	//this.emotionsUpdate();
			//this.printStats();
			return;
		}
		
	
		
		/*
		//Visual Processor
		else{
			this.emotionsUpdate();
			this.vision(state, grid);
		
		}
	
		//End of Step, print out tests
		this.printStats();
		*/
		

	}

	//Method that allows Predator to kill its Prey
	public void eat(Object p, SimState state){
		assert (p != null);
		
		
		if(p.getClass().equals(Prey.class)){
		
			Prey prey = (Prey) p;
			assert(prey != null);
			if(prey.isDiseased()){
				this.setDisease(true);
				this.diseaseTimestep = state.schedule.getTime();
			}
			caught = true;
			lastMeal = 0;
			prey.stop.stop();
			numPrey--;
			grid.remove(prey);
			
			//System.out.println("Prey was eaten by Predator");

		}
			
	}
	
	
	//Method that "kills" the Predator by removing it from the grid
	public boolean iDie(SimState state){
		
		 //older = more likely to die
		 //if(age>oldAge)
		 	//actualDeathRate = actualDeathRate * agingDeathMod;
		 	
		 //Last meal, more likely to die
		 if(lastMeal > lastMealMed)
			actualDeathRate = actualDeathRate * (hungerDeathMod);
		 
		/*//System.out.println("deathRate: " + deathRate);
		 if(lastMeal > lastMealHigh){
			 stop.stop();
			 numPredator--;
			 grid.remove(this);
			 return true;
		 }*/
		 // Death Rate
		double d = state.random.nextInt(deathRandNum);
		double death = d/deathRandNum;
		
		assert(d >= 0 && death >=0);
		
		//System.out.println("d: " + d + " death: " + death);
		if(death < actualDeathRate){
			stop.stop();
			numPredator--;
			grid.remove(this);
			
				
			return true;
		}
		
		
		return false;
	}
	
	//Method that allows predator to have the chance to reproduce
	public boolean iReproduce(SimState state){
		// Reproduction Rate
		double r = state.random.nextInt(repRandNum);
		double repo = r/repRandNum;
				
		assert (r >= 0 && repo >= 0);
		if(repo <= actualRepRate && age >= repAge && numPredator<=maxPredator){
			this.reproduce(state);
			this.lastRep = 0;
			return true;
			}
		
		return false;
	}
	
	//Determines whether the predator will eat or not
	public boolean willEat(SparseGrid2D grid, SimState state){
				
		/*if(lastMeal < lastMealLow)
			actualRepRate = actualRepRate * 1.5;
		
		if(state.schedule.getTime()%eatingChance != 0)
			return false;
		*/
		//Eating Prey on the same location
		assert(grid.getObjectsAtLocationOfObject(this) !=null);
		
		
		//System.out.println(grid.getObjectsAtLocationOfObject(this).size());
		int gridNum = grid.getObjectsAtLocationOfObject(this).size();
			
		assert(gridNum != 0);
			
			for(int i = 0; i < gridNum; i++){
				Object obj = (grid.getObjectsAtLocationOfObject(this)).get(i);
				
				if(obj.getClass().equals(Prey.class)){
					//System.out.println("\nPredator Ate");
					System.out.println("Predator Would have eaten");
					this.eat(obj, state);
					return true;
				}// end of if
			}// end of for loop
		
		
		return false;
	}
	//Method that allows Predator to duplicate
	public void reproduce(SimState state){
		
		//System.out.println("Predator Reproduced");
		
		Predator p = new Predator(state, grid, numPredator + 1);
		numPredator++;
		grid.setObjectLocation(p, grid.getObjectLocation(this));
		Stoppable stop = state.schedule.scheduleRepeating(p);
		p.makeStoppable(stop);
	}
	/*public void vision(SimState state, SparseGrid2D grid){
		
		Int2D cord = grid.getObjectLocation(this);
		assert(cord != null);

		seen = vP.sight(cord.x, cord.y, state, direction);
		Bag locations = new Bag();
		if(state.schedule.getTime()%2 != 0)
			map.updateMapsPred(seen, grid);
		
		//map.printMaps();
		for(int s = 0; s < seen.size(); s++){
			
			Int2D obLoc = grid.getObjectLocation(seen.get(s));
	
			locations.add(obLoc);
			//System.out.println(" at location:" + obLoc);
			//if(j.equals(Prey.class))
				//System.out.println("****" + seen.get(s));
			
		}
			
		this.behaviorProb(locations, seen, state);
		
		//Move every timestep
		super.move(grid, state);
		
		//System.out.println("Predator Moved");
	}// end of vision
	
	/*public void behaviorProb(Bag locs, Bag seen, SimState state){
	
		behavior = new BehaviorProcessor(grid);
		double[] newProb = behavior.updateProbPred(locs, seen, defaultProb, this, state, maxHunger);
		
		actualProb = newProb;
	}*/
	
	//Accessor for reproduction rate
	public double getRepRate()
	{
		
		return actualRepRate;
	}
	//Setter for reproduction rate
	public void setRepRate(double repRate)
	{
		actualRepRate = repRate;
	}
	//Emotions model update
	/*
	public void emotionsUpdate()
	{
		anger = anger.updateAnger(this);
		sad = sad.updateSadness(this);
		dis = dis.updateDisgust(this);
		fear = fear.updateFear(this);
		happy = happy.updateHappiness(this);
		surprise = surprise.updateSurprise(this);
		mood = mood.updateMood(anger, sad, dis, fear, happy);
	}*/
	
	//Print Stats to screen
	public void printStats()
	{
		System.out.print(", " + ID);
		//map.printMaps();
		System.out.print(", lastMeal: " + lastMeal);
		System.out.print(", deathRate " + actualDeathRate);
		System.out.print(", lastSocial: " + lastSocial);
		System.out.print(", directionChange: " + directChangeTotal + "\n");
		
	}
}
