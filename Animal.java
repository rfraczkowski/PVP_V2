package sim.app.PVP_V2;
//Class for Animal, Parent Class for Predator and Prey

import java.io.*;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public abstract class Animal implements Steppable {

	protected SparseGrid2D grid;
	protected static FileWriter writer;
	protected String outputString = "";

	protected static int preyStay = 0;
	protected static int predStay = 0;
	protected int age = 0;
//	protected int oldAge; 
	protected int direction;
	protected int lastMeal = 0;
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
	protected final static short LPWIDTH = 9; //width of the learnedProb array
	protected final static short LPHEIGHT = 9; //height of the learnedProb array
	//protected double[] learnedMov = actualProb;
	//Arrays for remembering policy - commented out as unused
//	protected double[] upLeft = {};
//	protected double[] up = {};
	//protected double[] actualProb = new double[8];
	public final static int NORTH = 0;
	public final static int SOUTH = 1;
	public final static int EAST = 2;
	public final static int WEST = 3;
	public static int numPrey;
	public static int numPredator;
//	protected int reproductionAge;

	protected int velocity = 1;
	//protected BehaviorProcessor behavior;
	//protected VisualProcessor vP;
	protected Stoppable stop;

	protected static int maxHunger;
//	protected int lastSeenPredator;
//	protected int lastSeenPrey;
//	protected int maxSeenPredator;
	protected int lastRep;
	protected int maxRep;
//	protected static int maxSocial;
//	protected int lastSocial = 0;
//	protected static int maxPrey = 300;
//	protected static int maxPredator = 300;
	protected int directChangeTotal = 0;
	protected String ID;
	protected Bag allObjects = new Bag();
	protected Int2D prevLoc;
	
	/* Disease related data members */
//	protected double diseaseTimestep;
//	protected int diseaseRandomNum = 100;
//	protected boolean isDiseased = false;

	/* emotion related data members */
	//protected ExpectationMap map;
//	protected static double expectMapDecayRate;
//	protected double mood = 0.0;
//	protected double emotions = 0.0;
//	protected double eRate = .1;
	
	/**
	 * Copies over one learnedProb array into the current one
	 * @param array the array whose values will replace thos in learnedProb. Must be same size
	 */
	protected void copyLearnedProb(double[][] array)
	{
		assert array.length == LPHEIGHT;
		
		for(int i=0; i<LPHEIGHT; i++)
		{
			assert array[i].length == LPWIDTH;
			for(int j=0; j<LPWIDTH; j++)
			{
				learnedProb[i][j] = array[i][j];
			}
		}
	}
	/**
		*Purpose: Initializes the Animal Class
		*Input:Number of predator, number or prey, and the directory to keep output information
		*Output:None
	**/
	protected final static void initialize(int prey, int pred, File directory, String filename){
		numPrey = prey;
		numPredator = pred;
		//dir = directory;
		try
		{
			directory.mkdir();
			File outputFile = new File(directory, filename + ".csv");//"run_" + System.currentTimeMillis()%600 + ".csv");
			writer = new FileWriter(outputFile);
			//write("AgentPosX, AgentPosY, FoodX, FoodY, DeltaX, DeltaY, Direction, Slope, Slope, Before Position: 0, 1, 2, 3, 4, 5, 6, 7, 8, Sum, After Pos: 0, 1, 2, 3, 4, 5, 6, 7, Sum, EmotionRate");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
		*Purpose: A Step in the agent system, called by each predator/prey at start of their own step	
		*Input:The state of the world SimState
		*Output:One agent turn, linked to MASON visualization
	**/
	@Override
	public void step(SimState state) {
	//	System.out.println(ID + " " + age + " " + ((PVP_2)state).world.getObjectLocation(this).x + " " + ((PVP_2)state).world.getObjectLocation(this).y);
		//PVPEmo pvp = (PVPEmo)state;
		PVP_2 pvp = (PVP_2)state;
		age++;
		grid = pvp.world;
		this.move(grid, pvp);
		
		//printLP(state);
//		age++;
		lastMeal++;
		lastRep++;

		//below data members are not currently in use
//		lastSeenPrey++;
//		lastSocial++;
//		lastSeenPredator++;
//		maxSeenPredator = 30;
		//Start of every step uses default movement

		velocity= 1;
	
		//All statistics that should happen per time step are now in the StatisticsAgent step method
	
	}

	/**
	 * Purpose: Write the learned probability to the output
	 * Input: State of the World
	 * Output: Writes to file/output the amounts of learned probability
	 **/
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
	 * Purpose: Write the learned probability to standard output
	 * @param State of the World
	 * Output: Writes to file/output the amounts of learned probability and ID
	 **/
	public void printLPSO(SimState state)
	{
		System.out.print("\n" + ID);
		//System.out.println(learnedProb.length);
		//System.out.println(learnedProb[0].length);
		for(int i = 0; i < (learnedProb.length); i++)
		{
			System.out.print("\n|");
			for(int j = 0; j < (learnedProb[0].length); j++)
					System.out.print(learnedProb[i][j] + "|");
		}
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
		assert cord !=null; //there is no valid reason this could be null //if(cord != null){
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
		}
		//Moving forward only
		else if (choice < (actualProb[0] + actualProb[1])){
			grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord - velocity));
			this.direction = NORTH;
		}
		//Moving up and right
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2])){
			grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord - velocity));
			this.direction = NORTH;
		}
		//Moving left
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3])){
			grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord));
			this.direction = WEST;
		}
		//Staying put
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4])){
			//unnecessary as there is nothing changing: grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord));
			this.direction = EAST;
		}
		//Moving right
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4]+ actualProb[5])){
			grid.setObjectLocation(this, grid.tx(xCord+velocity), grid.ty(yCord));
			this.direction = SOUTH;
		}
		//Moving down left
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4]+ actualProb[5] + actualProb[6])){
			grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord + velocity));
			this.direction = SOUTH;
		}
		//Moving down only
		else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7])){
			grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord +velocity));
			this.direction = SOUTH;
		}
		//Moving down right
		else //unnecessary: if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7] + actualProb[8]))
		{
			grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord +velocity));
			this.direction = SOUTH;
		}
		if(facing != direction)
			directChangeTotal++;
		
		/*******************This section is with old direction *********************************/
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
		/**********************************End of Old Direction **********************************/
		
		if(prevLoc.x == grid.getObjectLocation(this).x && prevLoc.y == grid.getObjectLocation(this).y)
		{
			if(this instanceof Prey)
				preyStay++;
			else
				predStay++;
		}
		
		//write("New Location: " + grid.getObjectLocation(this));
		
}
	
	/**
		*Purpose: Abstract method for eat
		*Input:Object to eat and state of world
		*Output: None
	**/
	protected abstract void eat(Object p, SimState state);
	
	/**
		*Purpose: Abstract method for reproducing
		*Input:State of world
		*Output:None
	**/
	protected abstract void reproduce(SimState state);
	
	/**
		*Purpose: Sets diseased
		*Input:Boolean for diseased
		*Output:None
	**/
//	protected void setDisease(boolean diseased){
//		isDiseased = diseased;
//	}
	
	/*
	 * Purpose: Returns whether or not the animal is diseased
	 * Input: None
	 * Output: Boolean of whether it is diseased
	 */
//	public boolean isDiseased(){
//		return isDiseased;
//	}
	/**
		*Purpose: Enum for direction
		*Input:None
		*Output:None
	**/
//	protected enum Direction{
//		NORTH(0), SOUTH(1), EAST(2), WEST(3);
//		
//		private int value;
//		
//		private Direction(int value){
//			this.value = value;
//		}
//	};
	
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
		
//		assert Math.abs(slope) <=2 : "Slope was > 2: " + animal.x + " " + goal.x;
		//write("DeltaX =" + deltaX);
		//write(" DeltaY  = " + deltaY);
		//write(slope + ",");
		
	//	if(Math.abs(slope) > 48) //currently unnecessary
	//		return;
		
		//Index Positions
		/*
		 *  0 - 1 - 2
		 *  3 - 4 - 5
		 *  6 - 7 - 8
		 */
		
		//Finding the goal and its opposite based on the slope
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
		double increase = calculateGoalIncrease(tempProb[g], isLow);
				
		/****************ADJUST OTHERS *********************/
		
		adjustRewardProbabilities(pvp, increase, tempProb, g, o);
		
		//OLSEN -- is all of this just for output? Or is it doing something? What is the point of the if statement?
//		double sum = 0;
//		for(int j = 0; j < 9; j++)
//		{
//			if(this.ID.equals("F0"))
//				write(tempProb[j] + ",");
//			sum += tempProb[j];
//		}
//		if(this.ID.equals("F0"))
//			write(sum + ",");
		//The above code ruins the output file. If it is output, it should be to a completely separate file.
		
		actualProb = tempProb;
	}
	
	/**
	 * Purpose: Makes the changes to movement probabilities
	 * @param tempProb the movement probabilities for all neighbors
	 * @param isLow whether or not the effect should be low?
	 * @return double the amount to increase the goal probability
	 */
	private double calculateGoalIncrease(double goal_prob, boolean isLow)
	{
		double increase;
		if(isLow)
		{
			
			increase = goal_prob*.10;
			increase = Math.round(increase);
			
			
			//write("Goal, " + actualProb[g]);
			//write("New Goal Prob: " + increase);
			//write("Opposite: " + actualProb[o]);
			//write("New Opp Prob: " + decrease);
		}
		else
		{
			increase = goal_prob*.25;
			increase = Math.round(increase);
			
			//write("Goal: " + actualProb[g]);
			//write("New Goal Prob: " + increase);
			//write("Opposite: " + actualProb[o]);
			//write("New Opp Prob: " + decrease);
		}
		return increase;
	}
	/**
	 * Purpose: Makes the changes to movement probabilities
	 * @param pvp the current state of the simulation
	 * @param increase the amount by which the goal increases and the opposite decreases
	 * @param tempProb the movement probabilities for all neighbors
	 * @param goal the location of the goal
	 * @param opp the location opposite of the goal
	 */
	private void adjustRewardProbabilities(SimState pvp, double increase, double[] tempProb, int goal, int opp)
	{
		double sum = 0; //OLSEN -- what is the point of this variable? It appears to be used to add up all probabilities for movement
		for(int j = 0; j < 9; j++)
		{
			//Write before positions
			//if(this.ID.equals("F0")) //this line and one below commented out by OLSEN to preserve output file
			//write(tempProb[j] + ",");
			//System.out.print("j:" + tempProb[j] + ",");
			//if(j != g)
			//{
				sum += tempProb[j];
			//}
		}
		
		//Write before position sum //commented out by OLSEN
	//	if(this.ID.equals("F0"))
		//	write(sum + ",");
		sum -= tempProb[goal];
		
		//If the increase is not more than the opposite square, adjust increase
		if(increase < sum && increase <= tempProb[opp])
		{
			tempProb[goal] += increase;
			tempProb[opp] -= increase;
			//write(", Normal Scenario");
		}
		else if(increase < sum && increase > tempProb[opp])
		{
			int newIndex = pvp.random.nextInt(8);
			
			//System.out.println("Increase: " + increase);
			
			if(tempProb[newIndex] >= increase)
			{
				tempProb[goal] += increase;
				tempProb[newIndex] -= increase;
			}
			else if(sum >= increase)
			{
				//sum -= increase;
				double newP = sum/8;
				for(int i = 0; i < 9; i++)
				{
					if(i != goal)
						tempProb[i] = newP;
				}
			}
			//System.out.println();
			//write(", Opposite Prob is all wiped out");
		}
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
	
	/**
	 * Purpose: Writes the string to the output 
	 * Input: String to be written
	 * Output: None
	 * @param out the string to be output
	 */
	protected void write(String out)
	{
		try
		{
		//	System.out.print("writing..." + out);
			writer.append(out);
			//writer.append('\n');
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Purpose: Appends the string to the output string
	 * Input: String to be appended
	 * Output: None
	 * @param out the value to be appended to the data member outputString
	 */
	protected void append(String out)
	{
		outputString = outputString + "," + out;
	}

}
