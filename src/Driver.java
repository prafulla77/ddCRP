import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import model.SamplerStateTracker;

import data.Data;


public class Driver {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<Double>> list_observations = Data.getObservations();
		long num_data = 0;
		for(int i=0;i<list_observations.size();i++)
			num_data = num_data + list_observations.get(i).size();
		
		SamplerStateTracker.initializeSamplerState(list_observations);
		SamplerStateTracker.samplerStates.get(0).prettyPrint(System.out);
	}

}
