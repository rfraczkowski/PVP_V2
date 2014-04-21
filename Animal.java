package sim.app.PVP_V2.src.pvp;


import java.io.*;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public abstract class Animal implements Steppable {

	protected SparseGrid2D grid;
	protected boolean isDiseased = false;
	protected FileWriter writer;
	protected File outputFile;
	protected File dir = new File("~/Documents/SeniorProject/corun_1");
	protected String outputString = "";

	protected int age = 0;
	protected int oldAge; 
	protected int direction;
	protected int lastMeal = 0;
	protected double[] actualProb = {11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11};
	protected double[] learnedMov = actualProb;
	//Arrays for remembering policy
	protected double[] upLeft = {};
	protected double[] up = {};
	//protected double[] actualProb = new double[8];
	public final static int NORTH = 0;
	public final static int SOUTH = 1;
	public final static int EAST = 2;
	public final static int WEST = 3;
	public static int numPrey;
	public static int numPredator;
	
	//protected Anger anger = new Anger(this);
	//protected Sadness sad = new Sadness(this);
	//protected Disgust dis = new Disgust(this);
	//protected Fear fear = new Fear(this);
	//protected Happiness happy = new Happiness(this);
	//protected Surprise surprise = new Surprise(this);
	//protected Mood mood = new Mood(anger, sad, dis, fear, happy);
	
	protected int reproductionAge;
	protected static double expectMapDecayRate;
	protected int velocity = 1;
	//protected BehaviorProcessor behavior;
	//protected VisualProcessor vP;
	protected Stoppable stop;
	//protected ExpectationMap map;
	protected static int maxHunger;
	protected int lastNumPrey = 0;
	protected int lastNumPredator = 0;
	protected int lastSeenPredator;
	protected int deathCollectPrey;
	protected int deathCollectPredator;
	protected int reproductionCollectPrey;
	protected int reproductionCollectPredator;
	protected int lastSeenPrey;
	protected int maxSeenPredator;
	protected int lastRep;
	protected int maxRep;
	protected static int maxSocial;
	protected static int maxPrey = 300;
	protected static int maxPredator = 300;
	protected int lastSocial = 0;
	protected int directChangeTotal = 0;
	protected String ID;
	protected Bag allObjects = new Bag();
	protected double diseaseTimestep;
	protected int diseaseRandomNum = 100;
	protected Int2D prevLoc;
	
	/**
		*Purpose: 
		*Input:
		*Output:
	**/
	protected final static void initialize(int prey, int pred){
		numPrey = prey;
		numPredator = pred;
	}
	
	/**
		*Purpose: A Step in the agent system	
		*Input:The state of the world SimState
		*Output:One agent turn, linked to MASON visualization
	**/
	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		//PVPEmo pvp = (PVPEmo)state;
		PVP_2 pvp = (PVP_2)state;
		age++;
		grid = pvp.world;
		this.move(grid, pvp);
		if(lastMeal >= oldAge)
		{
			stop.stop();
			
			if(this.getClass().equals(Predator.class))
				numPredator--;
			else if (this.getClass().equals(Prey.class))
				numPrey--;
			
			grid.remove(this);
		}
		/*age++;
		lastMeal++;
		lastSocial++;
		lastRep++;
		lastSeenPrey++;
		lastSeenPredator++;
		maxSeenPredator = 30;
		//Start of every step uses default movement
		actualProb = defaultProb;
		velocity= 1;
		vP = new VisualProcessor(state);
		 
		if(lastNumPrey > numPrey)
			deathCollectPrey += (lastNumPrey - numPrey);
		else if(lastNumPrey < numPrey)
			reproductionCollectPrey += (numPrey - lastNumPrey);
		
		if(lastNumPredator > numPredator)
			deathCollectPredator += (lastNumPredator - numPredator);
		else if(lastNumPredator < numPredator)
			reproductionCollectPredator += (numPredator - lastNumPredator);
		
		write(state.schedule.getTime() + ", " + numPrey + ", " + numPredator);*/
		if(numPrey == 0 || numPredator == 0){
			printFinalStats(state);
			state.kill();
		}
		
		/*
		lastNumPrey = numPrey;
		lastNumPredator = numPredator;*/
	
	}

	/**
		*Purpose: Prints the final stats of the simulation
		*Input: SimState
		*Output:Statistics to screen
	**/
	protected void printFinalStats(SimState state){
		
		write("\nTimeStep:" + (int)state.schedule.getTime());
		
		double finalRepRatePrey = reproductionCollectPrey/state.schedule.getTime();
		double finalDeathRatePrey = deathCollectPrey/state.schedule.getTime();
		
		double finalRepRatePredator = reproductionCollectPredator/state.schedule.getTime();
		double finalDeathRatePredator = deathCollectPredator/state.schedule.getTime();
		
		write("\n Final Stats:");
		write("Prey:");
		write("Death Rate: " + finalDeathRatePrey);
		write("Reproduction Rate: " + finalRepRatePrey);
		write("Predator:");
		write("Death Rate: " + finalDeathRatePredator);
		write("Reproduction Rate: " + finalRepRatePredator);
		
		
	}
	/**
		*Purpose: Moves agent based on object position
		*Input:Grid of the world and the state of the world
		*Output: Change position of the animal
	**/
	protected void move(SparseGrid2D grid, SimState pvp){
		
		
		// Biased Random Movement
		Int2D cord = grid.getObjectLocation(this);
		//assert ---Cord != null);
		if(cord != null){
		int xCord = cord.getX();
		int yCord = cord.getY();
		
		
		int choice = pvp.random.nextInt(100);
		//write("Choice: " + choice);
		
		//Each direction has biased defaultProbabilities
		
		int facing = direction;
		
		//Save the previous state of the world in order to learn
		prevLoc = cord;
		
		//If moving up, left, change coordinates to up left
		if (choice < actualProb[0]){
			grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord - velocity));
			this.direction = NORTH;
			if(facing != direction)
				directChangeTotal++;
		}
		//Moving forward only
		else if (choice < (actualProb[0] + actualProb[1])){
			grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord - velocity));
			this.direction = NORTH;
			if(facing != direction)
				directChangeTotal++;
		}
		//Moving up and right
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2])){
			grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord - velocity));
			this.direction = NORTH;
			if(facing != direction)
				directChangeTotal++;
		}
		//Moving left
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3])){
			grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord));
			this.direction = WEST;
			if(facing != direction)
				directChangeTotal++;
		}
		//Staying put
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4])){
			grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord));
			this.direction = EAST;
			if(facing != direction)
				directChangeTotal++;
		}
		//Moving right
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4]+ actualProb[5])){
			grid.setObjectLocation(this, grid.tx(xCord+velocity), grid.ty(yCord));
			this.direction = SOUTH;
			if(facing != direction)
				directChangeTotal++;
		}
		//Moving down left
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4]+ actualProb[5] + actualProb[6])){
			grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord + velocity));
			this.direction = SOUTH;
			if(facing != direction)
				directChangeTotal++;
		}
		//Moving down only
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7])){
			grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord +velocity));
			this.direction = SOUTH;
			if(facing != direction)
				directChangeTotal++;
		}
		//Moving down right
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7] + actualProb[8])){
			grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord +velocity));
			this.direction = SOUTH;
			if(facing != direction)
				directChangeTotal++;
		}
		
		//write("Old Location: " + grid.getObjectLocation(this));
		//write("Direction: " + direction + "Facing: " + facing);
		//write("Velocity: " + velocity);
		//Facing upward
		/*switch(facing){
		
			case NORTH:
				if (choice < actualProb[0]){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4]+ actualProb[5])){
					grid.setObjectLocation(this, grid.tx(xCord-velocity), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4]+ actualProb[5] + actualProb[6])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord -velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
			
			
			//Facing to the left
			case WEST: 
				if (choice < actualProb[0]){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord - velocity));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord + velocity));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord- velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord - velocity));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord  + velocity));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
			
			//Facing Downwards
			case SOUTH:
				if (choice < actualProb[0]){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord  + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
			
			
			//Facing to the right
			case EAST:
				if (choice < actualProb[0]){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord + velocity));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord - velocity));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord + velocity));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord  - velocity));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				
		}*/
		}
		
		//write("New Location: " + grid.getObjectLocation(this));
		
		}
	
	/**
		*Purpose: 
		*Input:
		*Output:
	**/
	protected abstract void eat(Object p, SimState state);
	
	/**
		*Purpose: 
		*Input:
		*Output:
	**/
	protected abstract void reproduce(SimState state);
	
	/**
		*Purpose: 
		*Input:
		*Output:
	**/
	protected void setDisease(boolean diseased){
		isDiseased = diseased;
	}
	
	/**
		*Purpose: 
		*Input:
		*Output:
	**/
	protected enum Direction{
		NORTH(0), SOUTH(1), EAST(2), WEST(3);
		
		private int value;
		
		private Direction(int value){
			this.value = value;
		}
	}
	
	/**
	 * Purpose:Readjust the probability of movement / policy so there is a slight reward for
	 * 	moving in a certain direction
	 * Input: Animal's location, the previous state of the world, 
	 * Output:Movement with new policy
	 */
	protected void reward(Int2D animal, SimState pvp, boolean isLow)
	{
		
		int deltaX = prevLoc.x - animal.x;
		//write("deltaX: x - y = " + prevLoc.x + " - " + animal.x);
		int deltaY = prevLoc.y - animal.y;
		//write("DeltaY: x - y = " + prevLoc.y + " - " + animal.y);
		int g = -1;
		int o = -1;

		
		int slope = deltaX + deltaY;
		
		//write("DeltaX =" + deltaX);
		//write(" DeltaY  = " + deltaY);
		write("Slope, " + slope);
		
		if(Math.abs(slope) > 48)
			return;
		
		
		if(slope == 0)
		{
			//If new Loc is upper right
			if(deltaX == -1)
			{
				g = 2;
				
				o = 6;
				
			}
			//Goal is lower left
			else if(deltaY == 1)
			{
				g = 6;
				o = 2;
				
				//Lower reward
			}
			else
			{
				//You are on the goal
				g = 4;
				o = pvp.random.nextInt(8);
				
				//High reward
				
			}
		}
		else if(slope == 1)
		{
			//Goal is directly up
			if(deltaX == 0)
			{
				g = 1;
				o = 7;
			}
			//Goal is direct to left
			else
			{
				g = 3;
				o = 5;
			}
		}
		else if(slope == -1)
		{
			//Goal is directly south
			if(deltaX == 0)
			{
				g = 7;
				o = 1;
			}
			//Goal is directly to right
			else
			{
				
				g = 5;
				o = 3;
			}
		}
		//Goal is upper left
		else if(slope == 2)
		{
			g = 0;
			o = 8;
			
		}
		//Goal is lower right
		else
		{
			g = 8;
			o = 0;
		}
		
		double increase;
		double decrease;
		if(isLow)
		{
			
			increase = actualProb[g]*.1;
			increase = Math.round(increase);
			
			
			write("Goal: " + actualProb[g]);
			write("New Goal Prob: " + increase);
			write("Opposite: " + actualProb[o]);
			//write("New Opp Prob: " + decrease);
		}
		else
		{
			increase = actualProb[g]*.10;
			increase = Math.round(increase);
			
			write("Goal: " + actualProb[g]);
			write("New Goal Prob: " + increase);
			write("Opposite: " + actualProb[o]);
			//write("New Opp Prob: " + decrease);
		}
		
		double sum = 0;
		for(int j = 0; j < 8; j++)
		{
			write("aP" + j + ": " + actualProb[j]);
			
			if(j != g)
			{
				sum += actualProb[j];
			}
		}
		
		if(increase < sum && increase < actualProb[o])
		{
			actualProb[g] = actualProb[g]+ increase;
			actualProb[o] = actualProb[o] - increase;
			write(", Normal Scenario");
		}
		else if(increase < sum && increase > actualProb[o])
		{
			int newIndex = pvp.random.nextInt(8);
			actualProb[g] = actualProb[g]+ increase;
			actualProb[newIndex] = actualProb[newIndex] - increase;
			write(", Opposite Prob is all wiped out");
		}
		else if(increase >= sum)
		{
			write(", Increase is too much!");
		}
		
		
		for(int j = 0; j < 8; j++)
		
			write("endaP" + j + ": " + actualProb[j]);
		
		
		
	}
	/**
	 * Purpose: Sets up the movement preference for the animal
	 * @param animal, a 2d int corresponding to a position
	 * @param goal, a 2d int corresponding to the goal position
	 */
	protected void setMovementPref(Int2D animal, Int2D goal, SimState pvp)
	{
		
		int deltaX = animal.x - goal.x;
		//write("deltaX: x - y = " + animal.x + " - " + goal.x);
		int deltaY = animal.y - goal.y;
		//write("DeltaY: x - y = " + animal.y + " - " + goal.y);
	
		
		int slope = deltaX + deltaY;
		
		//write("DeltaX =" + deltaX);
		//write(" DeltaY  = " + deltaY);
		write("Slope, " + slope);
		
		if(Math.abs(slope) > 48)
			return;
		
		if(animal.x == goal.x && animal.y == goal.y)
			reward(animal, pvp, false);
		else
			reward(animal, pvp, true);
		
	}
	
	/**
	 * Purpose: Sets up the movement preference for the animal
	 * @param animal, a 2d int corresponding to a position
	 * @param avoid, a 2d int corresponding to the opposite position
	 */
	protected Int2D find_Opp(Int2D animal, Int2D avoid)
	{
		int deltaX = grid.tx(animal.x) - grid.tx(avoid.x);
		int deltaY = animal.y - avoid.y;
		int slope = deltaX + deltaY;
		
		//write("DeltaX =" + deltaX);
		//write(" DeltaY  = " + deltaY);
		write("Slope, " + slope);
		
		int x, y;
		
		if(slope == 0)
		{
			//If new Loc is upper right
			if(deltaX == -1)
			{
				x = grid.tx(animal.x - 1);
				y = grid.ty(animal.y + 1);
				
			}
			//Goal is lower left
			else if(deltaY == 1)
			{
				x = grid.tx(animal.x + 1);
				y = grid.ty(animal.y - 1);
				
				//Lower reward
			}
			else
			{
				//You are dead
				x = grid.tx(animal.x);
				y = grid.ty(animal.y);
				
			}
		}
		else if(slope == 1)
		{
			//Goal is directly up
			if(deltaX == 0)
			{
				x = grid.tx(animal.x);
				y = grid.ty(animal.y + 1);
			}
			//Goal is direct to left
			else
			{
				x = grid.tx(animal.x + 1);
				y = grid.ty(animal.y);
			}
		}
		else if(slope == -1)
		{
			//Goal is directly south
			if(deltaX == 0)
			{
				x = grid.tx(animal.x);
				y = grid.ty(animal.y - 1);
			}
			//Goal is directly to right
			else
			{
				
				x = grid.tx(animal.x);
				y = grid.ty(animal.y - 1);
			}
		}
		//Goal is upper left
		else if(slope == 2)
		{
			x = grid.tx(animal.x + 1);
			y = grid.ty(animal.y + 1);
		}
		//Goal is lower right
		else
		{
			x = grid.tx(animal.x - 1);
			y = grid.ty(animal.y - 1);
		}
		
		
		return new Int2D(x, y);
	}
	
	protected void write(String out)
	{
		try
		{
			writer.append(out);
			//writer.append('\n');
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	protected void append(String out)
	{
		outputString = outputString + "," + out;
	}

	}
