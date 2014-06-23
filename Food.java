package sim.app.PVP_V2;
/*
 * This class is for the food in the system
 */
import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class Food implements Steppable {

	protected static int numFood = 0; //number of Food agents
	protected double amount = 1; //amount of food a single agent represents
//	private double diseasePr = .005;
//	private int diseaseRandNum = 10000;
	private double repPr = .0005;
	private final static int repRandNum = 10000;
//	private boolean diseased = false;
	private final static double regrowthRate = .25;
	private SparseGrid2D grid;
	protected Stoppable stop;

	/**
	 * Setting the initial amount of food
	 * @param i the number of food agents in the entire system
	 */
	protected static void setNumFood(int i)
	{
		numFood = i;
	}
	//Food is an agent, and therefore has a step
	@Override
	public void step(SimState state) {
		PVP_2 pvp = (PVP_2)state;
		
		grid = pvp.world;
		
		//Slowly grows back
		amount += regrowthRate;
		if(amount > 1)
			amount = 1;
		//Chance of disease
		//this.diseaseChance(pvp);
		
		//Chance of spread
		//this.reproductionChance(pvp);
	}

	/*public void diseaseChance(PVP_2 pvp){
		
		double d = pvp.random.nextInt(diseaseRandNum);
		double disease = d/diseaseRandNum;
		
		assert (disease >= 0);
		
		if(disease < diseasePr)
			diseased = true;
		
	}
	*/
	public void reproductionChance(PVP_2 pvp){
		
		// reproduction rate
		double rep = pvp.random.nextInt(repRandNum);
		//System.out.println("rep: " + rep);
		double repro = rep/repRandNum;
		//System.out.println("Repro: " + repro);
		assert (repro >= 0);
		
		if(repro < repPr)
			this.spread(grid, pvp);
	}
	
	
	/*
	 * Used for disease
	 */
//	public boolean isDiseased()
//	{
//		return diseased;
//	}

	/**
	 * Used for reproduction of food
	 * @param grid the grid of world objects, used to find locations and add agents to locations
	 * @param state the SimState object, used to schedule agents
	 */
	public void spread(SparseGrid2D grid, SimState state)
	{
		
		Int2D cord = grid.getObjectLocation(this);

		if(cord != null){
		
			Food p = new Food();
		
			int direction = state.random.nextInt(8);
			int nX;
			int nY;
			
			//find location to assign new food agent
			if (direction == 0){
				nX = cord.x;
				nY = cord.y+1;
			}
			else if (direction == 1){
				nX =cord.x;
				nY = cord.y - 1;	
			}
			else if (direction == 2){
				nX =cord.x + 1;
				nY =cord.y;	
			}
			else if (direction == 3){
				nX = cord.x + 1;
				nY = cord.y + 1;
			}
			else if (direction == 4){
				nX = cord.x + 1;
				nY = cord.y - 1;	
			}
			else if (direction == 5){
				nX = cord.x - 1;
				nY = cord.y + 1;
			}
			else if (direction == 6){
				nX = cord.x - 1;
				nY = cord.y - 1;
			}
			else //why was this one missing previously?
			{
				nX = cord.x - 1;
				nY = cord.y;
			}
			//guarantee location is within bounds
			nX = nX % PVP_2.getWidth();
			nY = nY % PVP_2.getHeight();
			//add new agent to the grid and schedule it
			boolean success = grid.setObjectLocation(p, nX, nY);
			assert success : "Was not able to place new food object in given location " + nX + " " + nY;
			Stoppable stop = state.schedule.scheduleRepeating(p);
			p.makeStoppable(stop);
			numFood++;
		}// end of if
	}// end of spread
	
	/*
	 * Needed to end simulation. Stops an agent when it is removed
	 */
	public void makeStoppable(Stoppable stopper)
	{
		stop = stopper;
	}
	
	/**
	 * Used for when a prey will eat the food, allows the amount to be gradient.
	 * @param decrease the amount by which the food's amount decreases during an eat event
	 */
	public void eat(double decrease)
	{
		amount = amount - decrease; //was .7, and method wasn't used then
		if(amount <=0){
			//amount = 0.0;
			//may be a point where it is being removed, but not stopped.
			this.stop.stop();
			grid.remove(this);
			numFood--;
		}
	}
}// end of class
