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
	 * The current iteration number. The first iteration is zeroth iteration.
	 */
	public static int current_iter;
	
	/**
	 * The maximum possible sampling iteration 
	 */
	public static int max_iter;
	
	/**
	 * List to hold the sampler states for each iteration. 
	 */
	public static ArrayList<SamplerState> samplerStates = new ArrayList<SamplerState>();
	
	/**
	 * Returns the current sampler state.
	 * @return
	 */
	public static SamplerState returnCurrentSamplerState()
	{
		if(current_iter >= 0 && current_iter==samplerStates.size()-1)
			return samplerStates.get(current_iter);
		else
			return null;
	}
	
	/**
	 * This initializes the sampler state. All observations point to themselves ie they have the customer assignments as themselves.
	 * As a result, each table consists of only one customer because of self links.
	 * @param list_observations
	 */
	public static void initializeSamplerState(ArrayList<ArrayList<Double>> list_observations)
	{
		if(current_iter == 0)
		{
			long num_data = 0;
			for(int i=0;i<list_observations.size();i++)
				num_data = num_data + list_observations.get(i).size();
			
			SamplerState state0 = new SamplerState(); //initial state		
			//setting the state
			SamplerState.setNum_data(num_data);
			ArrayList<ArrayList<Integer>> customer_assignments = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> table_assignments = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Long>> topic_assignments_table = new ArrayList<ArrayList<Long>>();
			ArrayList<ArrayList<Long>> topic_assignments_customer = new ArrayList<ArrayList<Long>>();
			ArrayList<HashMap<Integer,StringBuffer>> list_customers_in_table = new  ArrayList<HashMap<Integer,StringBuffer>>();
			HashMap<Long,Long> count_each_topic = new HashMap<Long,Long>();
			int num_topics = 100; //setting the initial number of topics to 100
			Random gen = new Random();			
			for(int i=0;i<list_observations.size();i++)		//keeping i as int, hoping that the number of cities/documents will be no greater than the size of integers
			{
				ArrayList<Integer> customer_assignment_per_list = new ArrayList<Integer>();
				ArrayList<Integer> table_assignment_per_list = new ArrayList<Integer>();
				ArrayList<Long> topic_assignments_table_per_list = new ArrayList<Long>();
				ArrayList<Long> topic_assignments_customer_per_list = new ArrayList<Long>();
				HashMap<Integer,StringBuffer> customers_in_table_per_list = new HashMap<Integer,StringBuffer>(); 
				for(int j=0;j<list_observations.get(i).size();j++) //note: the customers are indexed from 0
				{//initializing the customer assignments for each point to itself and hence each customer in its own table
					customer_assignment_per_list.add(j); 
					table_assignment_per_list.add(j);
					customers_in_table_per_list.put(j, new StringBuffer(Long.toString(j)));
					int topic = gen.nextInt(num_topics);
					topic_assignments_table_per_list.add(new Long(topic));
					topic_assignments_customer_per_list.add(new Long(topic));
					Long count = count_each_topic.get(topic);
					if(count == null) //new entry			
						count_each_topic.put(new Long(topic), 1L);
					else
						count_each_topic.put(new Long(topic), count+1);
				}
				
				customer_assignments.add(customer_assignment_per_list);
				table_assignments.add(table_assignment_per_list);
				list_customers_in_table.add(customers_in_table_per_list);
				topic_assignments_table.add(topic_assignments_table_per_list);
				topic_assignments_customer.add(topic_assignments_customer_per_list);
			}
			state0.setC(customer_assignments);
			state0.set_t(table_assignments);
			state0.setCustomers_in_table(list_customers_in_table);
			state0.setK_c(topic_assignments_customer);
			state0.setK_t(topic_assignments_table);
			state0.setT(num_data); //number of tables equal to num_data
			state0.setK(new Long(num_topics));
			state0.setM(count_each_topic);
			
			//Now putting into the arraylist of sampler states
			current_iter = 0;
			samplerStates.add(state0);
		}
	}
	
	

}
