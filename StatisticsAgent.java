package sim.app.PVP_V2.src.pvp;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * This agent exists to output and calculate statistics after every certain interval of time
 * It should be scheduled at the correct interval within MASON
 * It should be given the highest ordering of all scheduled agents so that it is run after everything for that timestep is complete
 * @author mmolsen
 *
 */
@SuppressWarnings("serial")
public class StatisticsAgent extends Animal implements Steppable {

	private double interval;
//	private static int lastNumPrey = 0;
//	private int lastNumPredator = 0;
	public StatisticsAgent(double i)
	{
		interval = i;
<<<<<<< HEAD
		write("Time, numPrey, numPred, preyDeathByInterval, preyReproductionByInterval, predOutran, predDeathByInterval, predReproductionByInterval, preyCaughtByInterval, preyStay, predStay\n");
		
=======
>>>>>>> FETCH_HEAD
	}
	@Override
	public void step(SimState state) {
		//Updates stats every step
		//if(lastNumPrey > numPrey) //moved so that count is done when death occurs
		//	deathCollectPrey += (lastNumPrey - numPrey);
		//else if(lastNumPrey < numPrey) //moved so that count is done when birth occurs
		//	reproductionCollectPrey += (numPrey - lastNumPrey);
		
		//below code moved to predator class to count each event as it occurs
//		if(lastNumPredator > numPredator)
//			deathCollectPredator += (lastNumPredator - numPredator);
//		else if(lastNumPredator < numPredator)
//			reproductionCollectPredator += (numPredator - lastNumPredator);
		
		//if(state.schedule.getTime()) % interval == 0) no longer necessary as agent only scheduled every interval timestep
		printStats(state);
		
		//write(state.schedule.getTime() + ", " + numPrey + ", " + numPredator);
		
		//If either all the prey are dead, or all the predator, then stop the simulation
		// and print the final stats.
		if(numPrey == 0 || numPredator == 0){
		//	write("End of sim,");
		//	printStats(state); will now get called above, so not needed
			state.kill();
		}
		
		
	//	lastNumPrey = numPrey;
	//	lastNumPredator = numPredator;
	}
	
	/**
	*Purpose: Prints the stats of the simulation, called after every "interval" of time
	*Input: SimState
	*Output:Statistics to screen
	**/
	protected void printStats(SimState state){
	
<<<<<<< HEAD
		write((int)state.schedule.getTime() + ","); //added minus 1, as if it is scheduled as first agent then the stats are through prior timestep
=======
		write("\nTimeStep:" + (int)state.schedule.getTime() + ","); //added minus 1, as if it is scheduled as first agent then the stats are through prior timestep
>>>>>>> FETCH_HEAD
		
		double finalRepRatePrey = Prey.reproductionCollectPrey/interval;
		double finalDeathRatePrey = Prey.deathCollectPrey/interval;
		
		double finalRepRatePredator = Predator.reproductionCollectPredator/interval;
		double finalDeathRatePredator = Predator.deathCollectPredator/interval;
		double predOutranRate = Prey.predOutran/interval;
		double preyCaughtRate = Predator.preyCaught/interval;
		
		/*
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
		*/
		write(numPrey + ",");
<<<<<<< HEAD
		write(numPredator + ",");
		write(finalDeathRatePrey + ",");
		write(finalRepRatePrey + ",");
		write(predOutranRate + ",");
		write(finalDeathRatePredator + ",");
		write(finalRepRatePredator + ",");
		write(preyCaughtRate + ",");
		write(preyStay + ",");
		write(predStay + "\n");
		//write(".1" + ",");
		//write(((PVP_2) state).getClusters()+"\n");
=======
		write(finalDeathRatePrey + ",");
		write(finalRepRatePrey + ",");
		write(predOutranRate + ",");
		write(preyStay + ",");
		write(numPredator + ",");
		write(finalDeathRatePredator + ",");
		write(finalRepRatePredator + ",");
		write(preyCaughtRate + ",");
		write(predStay + ",");
		write(".1");
>>>>>>> FETCH_HEAD
		
		Prey.reproductionCollectPrey = 0;
		Prey.deathCollectPrey = 0;
		Predator.reproductionCollectPredator = 0;
		Predator.deathCollectPredator = 0;
		Prey.predOutran = 0;
		Predator.preyCaught = 0;
		preyStay = 0;
		predStay = 0;
<<<<<<< HEAD
			
=======

		
>>>>>>> FETCH_HEAD
	}
	@Override
	protected void eat(Object p, SimState state) {
		//nothing happens
		
	}

	@Override
	protected void reproduce(SimState state) {
		// nothing happens
		
	}

}
