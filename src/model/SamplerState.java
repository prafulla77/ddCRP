package model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is for storing the state of the sampler for an iteration.
 * An object of this class represent the state of the sampler at one iteration.
 * @author rajarshd
 */
public class SamplerState {

	/**
	 * The number of data instances.
	 */
	private static Long num_data;
	
	/**
	 * This stores the customer link for each data point. Each list represents a city/document etc.
	 */
	private ArrayList<ArrayList<Long>> c;
	
	/**
	 * This stores the table assignment for each data point.Each list represents a city/document etc.
	 */
	private ArrayList<ArrayList<Long>> t;
	
	/**
	 * The number of occupied tables at this iteration
	 */
	private Long T;
	
	/**
	 * The total number of topics
	 */
	private Long K;
	/**
	 * This stores the topic assignments for each data point (which is basically the topic assignment at the given table they are sitting at)
	 */
	private ArrayList<ArrayList<Long>> k_c;
	
	/**
	 * This stores the topic assignments for each table;
	 */
	private ArrayList<ArrayList<Long>> k_t;
	
	/**
	 * This stores the number of tables(clusters) assigned to each topic.
	 */
	private HashMap<Long,Long> m;
	
	/**
	 * Map of table and the customer_ids.
	 * To Discuss: Need to find a better way of storing customer ids, presently concatenating strings of ids, not very efficient 
	 */
	private ArrayList<HashMap<Long,StringBuffer>> customers_in_table;

	/**
	 * 
	 * Getters and Setters
	 * 
	 */
	
	public static Long getNum_data() {
		return num_data;
	}

	public static void setNum_data(Long num_data) {
		SamplerState.num_data = num_data;
	}


	public Long getT() {
		return T;
	}

	public void setT(Long t) {
		T = t;
	}

	public Long getK() {
		return K;
	}

	public void setK(Long k) {
		K = k;
	}

	public HashMap<Long,Long> getM() {
		return m;
	}
	
	public void setM(HashMap<Long,Long> m) {
		this.m = m;
	}

	public ArrayList<ArrayList<Long>> getC() {
		return c;
	}
	
	/**
	 * Returns the customer link of a particular customer given the list and the customer indexes
	 * @param customer_index
	 * @param city_index
	 * @return
	 */
	public Long getC(int customer_index,int city_index)
	{
		return c.get(city_index).get(customer_index);
	}

	public void setC(ArrayList<ArrayList<Long>> c) {
		this.c = c;
	}
	
	/**
	 * Sets the new customer assignment for a customer
	 * @param cust_assignment
	 * @param customer_index
	 * @param city_index
	 */
	public void setC(Long cust_assignment, int customer_index,int city_index)
	{
		c.get(city_index).set(customer_index, cust_assignment);
	}

	public ArrayList<ArrayList<Long>> getK_c() {
		return k_c;
	}

	public void setK_c(ArrayList<ArrayList<Long>> k_c) {
		this.k_c = k_c;
	}

	public ArrayList<ArrayList<Long>> getK_t() {
		return k_t;
	}

	public void setK_t(ArrayList<ArrayList<Long>> k_t) {
		this.k_t = k_t;
	}

	public ArrayList<ArrayList<Long>> get_t()
	{
		return t;
	}
	
	/**
	 * Returns the table assignment for a specific customer given the list and the customer index
	 * @param customer_index
	 * @param city_index
	 * @return
	 */
	public Long get_t(int customer_index,int city_index)
	{
		return t.get(city_index).get(customer_index);
	}
	public void set_t(ArrayList<ArrayList<Long>> t) {
		this.t = t;
	}
	/**
	 * Sets the new table assignment for a customer
	 * @param table_assignment
	 * @param customer_index
	 * @param city_index
	 */
	public void set_t(Long table_assignment, int customer_index,int city_index)
	{
		t.get(city_index).set(customer_index, table_assignment);
	}
	public ArrayList<HashMap<Long, StringBuffer>> getCustomers_in_table() {
		return customers_in_table;
	}
	/**
	 * returns the string of customers sitting at a table in a given list
	 * @param table_id
	 * @param list_index
	 * @return
	 */
	public String getCustomers_in_table(int table_id,int list_index)
	{		
		return customers_in_table.get(list_index).get(new Long(table_id)).toString();
	}
	/**
	 * Sets the customers sitting at a table, given the indexes and the table number
	 * @param s
	 * @param table_id
	 * @param list_index
	 */
	public void setCustomers_in_table(StringBuffer s,int table_id,int list_index)
	{
		customers_in_table.get(list_index).put(new Long(table_id), s);
	}

	public void setCustomers_in_table(
			ArrayList<HashMap<Long, StringBuffer>> customers_in_table) {
		this.customers_in_table = customers_in_table;
	}
	

	/**
	 * Returns a new sampler state which is identical to the given sampler state.
	 * @return
	 */
	public SamplerState copy()
	{
		SamplerState s = new SamplerState();
		ArrayList<ArrayList<Long>> new_c = new ArrayList<ArrayList<Long>>(); //customer assignments
		ArrayList<ArrayList<Long>> new_t = new ArrayList<ArrayList<Long>>(); //table assignments per customer
		ArrayList<HashMap<Long,StringBuffer>> new_customers_in_table = new ArrayList<HashMap<Long,StringBuffer>>();  
		//ArrayList<ArrayList<Long>> new_k_c = new ArrayList<ArrayList<Long>>(); //topic assignments per customer
		for(int i=0;i<c.size();i++)
		{
			ArrayList<Long> customer_assignments_copy = new ArrayList<Long>(c.get(i)); //this will create a new list pointing to the same long objects, but its ok since Long is immutable.
			new_c.add(customer_assignments_copy);
			ArrayList<Long> table_assignments_copy = new ArrayList<Long>(t.get(i));
			new_t.add(table_assignments_copy);
			HashMap<Long,StringBuffer> customers_in_table_copy = new HashMap<Long,StringBuffer>(customers_in_table.get(i));
			new_customers_in_table.add(customers_in_table_copy);
			//ArrayList<Long> topic_assignments_copy = new ArrayList<Long>(k_c.get(i));
			//new_k_c.add(topic_assignments_copy);
		}
		s.c = new_c;
		s.t = new_t;
		s.T = new Long(T);
		s.customers_in_table = new_customers_in_table;
		//s.K = new Long(K);
		return s;
	}

	/**
	 * Prints the object state
	 */
	public void prettyPrint(PrintStream out)
	{
		out.println("Total number of observations are "+SamplerState.num_data);
		out.println("Total number of tables are "+T);
		out.println("Total number of topics "+K);
	}
	
	
}