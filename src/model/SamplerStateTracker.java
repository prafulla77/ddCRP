package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


/**
 * This class will store all the sampler state objects of each iteration
 * @author rajarshd
 *
 */
public class SamplerStateTracker {
	
	/**
	 * The current iteration number
	 */
	public static int current_iter;
	
	/**
	 * The maximum possible sampling iteration 
	 */
	public static int max_iter;
	
	/**
	 * List to hold the sampler states for each iteration
	 */
	public static ArrayList<SamplerState> samplerStates = new ArrayList<SamplerState>();
	
	
	public static void initializeSamplerState(Long num_data)
	{
		if(current_iter == 0)
		{
			SamplerState state0 = new SamplerState(); //initial state		
			//setting the state
			SamplerState.setNum_data(num_data);
			ArrayList<Long> customer_assignments = new ArrayList<Long>();
			ArrayList<Long> table_assignments = new ArrayList<Long>();
			ArrayList<Long> topic_assignments_table = new ArrayList<Long>();
			ArrayList<Long> topic_assignments_customer = new ArrayList<Long>();
			HashMap<Long,Long> count_each_topic = new HashMap<Long,Long>();
			int num_topics = 100; //setting the initial number of topics to 100
			Random gen = new Random();
			//initializing the customer assignments for each point to itself and hence each customer in its own table 
			for(long i=0;i<num_data;i++)		
			{
				customer_assignments.add(i);
				table_assignments.add(i);			
				int topic = gen.nextInt(num_topics);
				topic_assignments_table.add(new Long(topic));
				topic_assignments_customer.add(new Long(topic));
				Long count = count_each_topic.get(topic);
				if(count == null) //new entry			
					count_each_topic.put(new Long(topic), 1L);
				else
					count_each_topic.put(new Long(topic), count+1);
				
			}
			state0.setC(customer_assignments);
			state0.set_t(table_assignments);
			state0.setK_c(topic_assignments_customer);
			state0.setK_t(topic_assignments_table);
			state0.setT(num_data); //number of tables equal to num_data
			state0.setK(new Long(num_topics));
			state0.setM(count_each_topic);
			
			//Now putting into the arraylist of sampler states
			current_iter = 1;
			samplerStates.add(state0);
		}
	}
	
	

}
