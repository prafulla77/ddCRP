package util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.SamplerState;
import model.SamplerStateTracker;

public class Util {

	/**
	 * Samples from a discrete distribution. The input is a list of probabilites (non negative and non zero)
	 * They need not sum to 1, the list will be normalized. 
	 * @param probs
	 * @return
	 */
	public static int sample(List<Double> probs)
	{
		ArrayList<Double> cumulative_probs = new ArrayList<Double>();
		double sum_probs = 0;
		for(double prob:probs)
		{
			sum_probs = sum_probs + prob;
			cumulative_probs.add(sum_probs);
		}
		if(sum_probs!=1)		//normalizing
			for(int i=0;i<probs.size();i++)
			{
				probs.set(i, probs.get(i)/sum_probs);
				cumulative_probs.set(i, cumulative_probs.get(i)/sum_probs);
			}
		Random r  = new Random();
		double nextRandom = r.nextDouble();
		for(int i=0;i<cumulative_probs.size();i++)		
			if(cumulative_probs.get(i)>nextRandom)			
				return i;
		
		return -1;		
	}
	
	/**
	 * Prints the table configuration for the current (last) state of the sampler for a given list index
	 * @param list_index
	 */
	public static void printTableConfiguration(int list_index, PrintStream out)
	{
		SamplerState s = SamplerStateTracker.returnCurrentSamplerState();
		int count  = 0;
		for(int table_id=0;table_id<s.getC().get(list_index).size();table_id++)
		{
			String customers = s.getCustomers_in_table(table_id, list_index);
			if(customers != null)
			{
				count++;
				String[] eachCustomers = customers.split(",");
				out.println("Table "+table_id+" Count "+eachCustomers.length+" :\t"+customers);
				
			}
		}
		out.println("There are "+count+" occupied tables");
	}

}
