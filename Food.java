package sim.app.PVP_V2.src.pvp;
/*
 * This class is for the food in the system
 */
import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class Food implements Steppable {

	protected double amount = 1000;
//	private double diseasePr = .005;
//	private int diseaseRandNum = 10000;
	private double repPr = .0005;
	private int repRandNum = 10000;
//	private boolean diseased = false;
	private double regrowthRate = .25;
	public SparseGrid2D grid;
	protected Stoppable stop;

	//Food is an agent, and therefore has a step
	@Override
	public void step(SimState state) {
		PVP_2 pvp = (PVP_2)state;
		
		grid = pvp.world;
		
		//Slowly grows back
		amount += regrowthRate;
		
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

	/*
	 * Used for reproduction of food
	 */
	public void spread(SparseGrid2D grid, SimState state)
	{
		
	
		Int2D cord = grid.getObjectLocation(this);
		
		if(cord != null){
		
			Food p = new Food();
		
			int direction = state.random.nextInt(7);
	
			
			if (direction == 0){
				grid.setObjectLocation(p, cord.x, cord.y + 1);
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 1){
				grid.setObjectLocation(p, cord.x, cord.y - 1);	
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 2){
				grid.setObjectLocation(p, cord.x + 1, cord.y);	
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 3){
				grid.setObjectLocation(p, cord.x + 1, cord.y + 1);
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 4){
				grid.setObjectLocation(p, cord.x + 1, cord.y - 1);	
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 5){
				grid.setObjectLocation(p, cord.x - 1, cord.y + 1);
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 6){
				grid.setObjectLocation(p, cord.x - 1, cord.y - 1);
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
		}// end of if
	}// end of spread
	
	/*
	 * Needed to end simulation. Stops an agent when it is removed
	 */
	public void makeStoppable(Stoppable stopper)
	{
		stop = stopper;
	}
	
	/*
	 * Used for when a prey will eat the food, allows the amount to be gradient.
	 */
	public void eat()
	{
		amount = amount - .7;
		if(amount <0){
			//amount = 0.0;
			//may be a point where it is being removed, but not stopped.
			this.stop.stop();
			grid.remove(this);
			
		}
	}
}// end of class
