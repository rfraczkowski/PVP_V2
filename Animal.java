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
	protected static FileWriter writer;
	protected static File outputFile;
	protected static File dir;
	protected String outputString = "";

	protected double mood = 0.0;
	protected double emotions = 0.0;
	protected double eRate = .1;
	protected static int preyStay = 0;
	protected static int predStay = 0;
	protected int age = 0;
	protected int oldAge; 
	protected int direction;
	protected int lastMeal = 0;
	static double interval = 500;
	protected double[] actualProb = {11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11};
	protected double[][] learnedProb = {
			
			{11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11}, 
			{11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11}, 
			{11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11},
			{11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11},
			{11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11},
			{11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11},
			{11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11},
			{11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11},
			{11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11, 11.11}
	};
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
	protected static int lastNumPrey = 0;
	protected int lastNumPredator = 0;
	protected int lastSeenPredator;
	protected static int deathCollectPrey;
	protected static int deathCollectPredator;
	protected static int reproductionCollectPrey;
	protected static int reproductionCollectPredator;
	protected int lastSeenPrey;
	protected int maxSeenPredator;
	protected static int preyCaught = 0;
	protected static int predOutran = 0;
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
	protected final static void initialize(int prey, int pred, File directory){
		numPrey = prey;
		numPredator = pred;
		dir = directory;
		try
		{
			dir.mkdir();
			outputFile = new File(dir, "run_" + System.currentTimeMillis()%600 + ".csv");
			writer = new FileWriter(outputFile);
			//write("AgentPosX, AgentPosY, FoodX, FoodY, DeltaX, DeltaY, Direction, Slope, Slope, Before Position: 0, 1, 2, 3, 4, 5, 6, 7, 8, Sum, After Pos: 0, 1, 2, 3, 4, 5, 6, 7, Sum, EmotionRate");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
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
		/*if(lastMeal >= oldAge)
		{
			stop.stop();
			
			if(this.getClass().equals(Predator.class))
				numPredator--;
			else if (this.getClass().equals(Prey.class))
				numPrey--;
			
			grid.remove(this);
		}*/
		
		//printLP(state);
		age++;
		lastMeal++;
		lastSocial++;
		lastRep++;
		lastSeenPrey++;
		lastSeenPredator++;
		maxSeenPredator = 30;
		//Start of every step uses default movement

		velocity= 1;
	
		 
		if(lastNumPrey > numPrey)
			deathCollectPrey += (lastNumPrey - numPrey);
		else if(lastNumPrey < numPrey)
			reproductionCollectPrey += (numPrey - lastNumPrey);
		
		if(lastNumPredator > numPredator)
			deathCollectPredator += (lastNumPredator - numPredator);
		else if(lastNumPredator < numPredator)
			reproductionCollectPredator += (numPredator - lastNumPredator);
		
		if(state.schedule.getTime() % interval == 0)
			printFinalStats(state);
		
		//write(state.schedule.getTime() + ", " + numPrey + ", " + numPredator);*/
		if(numPrey == 0 || numPredator == 0){
			write("End of sim,");
			printFinalStats(state);
			state.kill();
		}
		
		
		lastNumPrey = numPrey;
		lastNumPredator = numPredator;
	
	}

	
	public void printLP(SimState state)
	{
		write("\nTimeStep:" + (int)state.schedule.getTime());
		write(" Learned Probability Array {\n");
		//System.out.println(learnedProb.length);
		//System.out.println(learnedProb[0].length);
		for(int i = 0; i < (learnedProb.length); i++)
			for(int j = 0; j < (learnedProb[0].length); j++)
					write("|" + learnedProb[i][j] + "|");
	}
	/**
		*Purpose: Prints the final stats of the simulation
		*Input: SimState
		*Output:Statistics to screen
	**/
	protected void printFinalStats(SimState state){
		
		write("\nTimeStep:" + (int)state.schedule.getTime() + ",");
		
		double finalRepRatePrey = reproductionCollectPrey/interval;
		double finalDeathRatePrey = deathCollectPrey/interval;
		
		double finalRepRatePredator = reproductionCollectPredator/interval;
		double finalDeathRatePredator = deathCollectPredator/interval;
		double predOutranRate = predOutran/interval;
		double preyCaughtRate = preyCaught/interval;
		
		write("Prey,");
		write("Prey Total: " + numPrey + ",");
		write("Death Rate: " + finalDeathRatePrey + ",");
		write("Reproduction Rate: " + finalRepRatePrey + ",");
		write("Learning Outrun Pred: " + predOutranRate + ",");
		write("Prey Stay: " + preyStay + ",");
		write("Predator,");
		write("Predator Total: " + numPredator + ",");
		write("Death Rate: " + finalDeathRatePredator + ",");
		write("Reproduction Rate: " + finalRepRatePredator + ",");
		write("Learning Catch Prey: " + preyCaughtRate + ",");
		write("Predator Stay: " + predStay + ",");
		write("Food,");
		write("Food Total" + ".1" + ",");
		write("Food Clustered" + "Yes" + ",");
		
		reproductionCollectPrey = 0;
		deathCollectPrey = 0;
		reproductionCollectPredator = 0;
		deathCollectPredator = 0;
		predOutran = 0;
		preyCaught = 0;
		preyStay = 0;
		predStay = 0;
		
		
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
		
			if(prevLoc.x == grid.getObjectLocation(this).x && prevLoc.y == grid.getObjectLocation(this).y)
			{
				if(this instanceof Prey)
					preyStay++;
				else
					predStay++;
			}
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
	protected void reward(Int2D animal, Int2D goal, SimState pvp, boolean isLow)
	{
		int deltaX = animal.x - goal.x;
		//write("deltaX: x - y = " + prevLoc.x + " - " + animal.x);
		int deltaY = animal.y - goal.y;
		//write("DeltaY: x - y = " + prevLoc.y + " - " + animal.y);
		int g = -1;
		int o = -1;
		
		double[] tempProb; 
		
		int slope = deltaX + deltaY;
		
		//write("DeltaX =" + deltaX);
		//write(" DeltaY  = " + deltaY);
		//write(slope + ",");
		
		if(Math.abs(slope) > 48)
			return;
		
		//Index Positions
		/*
		 *  0 - 1 - 2
		 *  3 - 4 - 5
		 *  6 - 7 - 8
		 */
		
		if(slope == 0)
		{
			//If new Loc is upper right POSITION INDEX 2
			if(deltaX == -1)
			{
				g = 2;
				
				o = 6;
				
				
			}
			//Goal is lower left POSITION INDEX 5
			else if(deltaX == 1)
			{
				g = 6;
				o = 2;
				
				//Lower reward
			}
			else
			{
				//You are on the goal POSITION INDEX 4
				g = 4;
				o = pvp.random.nextInt(8);
				
				//High reward
				
			}
		}
		else if(slope == 1)
		{
			//Goal is directly up POSITION INDEX 1
			if(deltaX == 0)
			{
				g = 1;
				o = 7;
	
			}
			//Goal is direct to left POSITION INDEX 3
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
		
		
		/******************INCREASE GOAL *************************/
		tempProb = learnedProb[g];
		double increase;
		double decrease;
		if(isLow)
		{
			
			increase = tempProb[g]*.10;
			increase = Math.round(increase);
			
			
			//write("Goal, " + actualProb[g]);
			//write("New Goal Prob: " + increase);
			//write("Opposite: " + actualProb[o]);
			//write("New Opp Prob: " + decrease);
		}
		else
		{
			increase = tempProb[g]*.25;
			increase = Math.round(increase);
			
			//write("Goal: " + actualProb[g]);
			//write("New Goal Prob: " + increase);
			//write("Opposite: " + actualProb[o]);
			//write("New Opp Prob: " + decrease);
		}
		
		/****************ADJUST OTHERS *********************/
		
		double sum = 0;
		for(int j = 0; j < 9; j++)
		{
			//Write before positions
			if(this.ID.equals("F0"))
			write(tempProb[j] + ",");
			//System.out.print("j:" + tempProb[j] + ",");
			//if(j != g)
			//{
				sum += tempProb[j];
			//}
		}
		
		//Write before position sum
		if(this.ID.equals("F0"))
		write(sum + ",");
		sum -= tempProb[g];
		//If the increase is not more than the opposite square, adjust increase
		if(increase < sum && increase <= tempProb[o])
		{
			tempProb[g] += increase;
			tempProb[o] -= increase;
			//write(", Normal Scenario");
		}
		else if(increase < sum && increase > tempProb[o])
		{
			int newIndex = pvp.random.nextInt(8);
			
			//System.out.println("Increase: " + increase);
			
			if(tempProb[newIndex] >= increase)
			{
				tempProb[g] += increase;
				tempProb[newIndex] -= increase;
			}
			else if(sum >= increase)
			{
				//sum -= increase;
				double newP = sum/8;
				for(int i = 0; i < 9; i++)
				{
					if(i != g)
						tempProb[i] = newP;
				}
			}
			
		
			//System.out.println();
			//write(", Opposite Prob is all wiped out");
		}
		
		
		sum = 0;
		for(int j = 0; j < 9; j++)
		{
			if(this.ID.equals("F0"))
			write(tempProb[j] + ",");
			sum += tempProb[j];
		}
		if(this.ID.equals("F0"))
		write(sum + ",");
		
		
		actualProb = tempProb;
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
		//write(slope + ",");
		
		if(Math.abs(slope) > 48)
			return;
		
		if(animal.x == goal.x && animal.y == goal.y)
			reward(animal, goal, pvp, false);
		else
			reward(animal, goal, pvp, true);
		
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
		//write("Slope, " + slope);
		
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
