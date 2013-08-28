/**
 * 
 */
package sampler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import model.HyperParameters;
import model.SamplerState;
import model.SamplerStateTracker;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.vector.Vector;

import util.Util;

import Likelihood.Likelihood;

import data.Data;

/**
 * @author rajarshd
 *
 */
public class GibbsSampler {
	
	/**
	 * A list of queues, each queue for maintaining a list of empty tables, which can be assigned when split of tables happen.
	 */
	private static List<Queue<Integer>> emptyTables = new ArrayList<Queue<Integer>>();
	
	private final static Logger LOGGER = Logger.getLogger(GibbsSampler.class
		      .getName());
	
	static{
		try {		
			LOGGER.setLevel(Level.INFO);
			FileHandler logFileHandler;
			logFileHandler = new FileHandler("log.txt");
			logFileHandler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(logFileHandler);
		
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	public static void doSampling(Likelihood l) 
	{
		//create the copy of the latest sampler state and assign it to the new one
		SamplerState s = SamplerStateTracker.samplerStates.get(SamplerStateTracker.current_iter).copy();
		SamplerStateTracker.samplerStates.add(s);
		//increase the number of iteration of sampling by 1
		SamplerStateTracker.current_iter = SamplerStateTracker.current_iter + 1;		
		
		//if queue already not initialized, then initialize a list of empty queues
		ArrayList<ArrayList<Double>> all_observations = Data.getObservations();
		if(emptyTables.size()!=all_observations.size())		
			for(int i=0;i<all_observations.size();i++)
				emptyTables.add(new LinkedList<Integer>());
		
		//Sampling for each list (city/document)
		
			for(int i=0;i<all_observations.size();i++)
			{
				LOGGER.log(Level.FINE, "Starting to sample for list "+i);			
				ArrayList<Double> list = all_observations.get(i); //each city in our case
				//For each observation in the list sample customer assignments (for each venue in a city)
				for(int j=0;j<list.size();j++) //Concern: No of observation in a list should not cross the size of integers
				{				
					//sample customer link for this observation
					sampleLink(j,i,l); //sending the list (city) and the index so that the observation can be accessed
				}
				LOGGER.log(Level.FINE, "Done for list "+i);
				System.out.println("Done for list "+i);
		}
	}
	
	private static void sampleLink(int index, int list_index,Likelihood ll)
	{
		LOGGER.log(Level.FINE, "Sampling link for index "+index+" list_index "+list_index);
		
		//check to see if the table has circle or not
		//get the table id where this observation is sitting
		SamplerState s = SamplerStateTracker.samplerStates.get(SamplerStateTracker.current_iter);
		int table_id = s.get_t(index, list_index); //the table id where the observation is sitting
		
		//get all the customers who are sitting in the table.
		String customers_in_table = s.getCustomers_in_table(table_id, list_index);
		String customer_indexes[] = customers_in_table.split(",");
		
		ArrayList<Integer> orig_table_members = new ArrayList<Integer>(); //this will hold the table members of the customer, if there is a split, we will update it to point to the members of the new table
		//Create a graph with customers_in_table as the vertices
		DirectedGraph<Integer, DefaultEdge> g =
	            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
		HashMap<Integer,Integer> map_of_references = new HashMap<Integer,Integer>(); //map to store the reference of the vertices so that they can be used later to form edges.
		
		for(int i=0;i<customer_indexes.length;i++)		
		{
			orig_table_members.add(Integer.parseInt(customer_indexes[i]));
			if(map_of_references.get(Integer.parseInt(customer_indexes[i]))==null) //didnot encounter this customer b4 
			{
				Integer customer_index = Integer.parseInt(customer_indexes[i]); // creating the reference for the new customer
				map_of_references.put(customer_index, customer_index); //putting into the map for future reference while creating the edges
				g.addVertex(customer_index);								
			}		
			//now create an edge with its customer assignment
			int customer_assignment = s.getC(Integer.parseInt(customer_indexes[i]), list_index);
			
			//lets check we have a vertex already created for this customer or not?
			if(map_of_references.get(customer_assignment)==null)
			{ //nope will have to create a new object
				//instead of creating the object, lets use the customer_assignment reference itself
				map_of_references.put(customer_assignment, customer_assignment);
				g.addVertex(customer_assignment);
			}
			else
			{
				customer_assignment = map_of_references.get(customer_assignment); //since we have made a reference earlier, we will use that (Is it getting clumsy?)				
			}
			//adding the edge
			g.addEdge(map_of_references.get(Integer.parseInt(customer_indexes[i])), customer_assignment);//1st arg, the current customer, 2nd arg, its assignment
		} //the graph should be ready
		
		//If the 'obs_to_sample' is a part of a cycle then removing its customer assignment cannot split the table. 
		//Check if there is a cycle containing obs_to_sample
		CycleDetector<Integer,DefaultEdge> cycleDetector = new CycleDetector<Integer,DefaultEdge>(g);
		boolean isCyclePresent = cycleDetector.detectCyclesContainingVertex(map_of_references.get(index));
		if(!isCyclePresent) //on removing the link, the table will be split 
		{
			LOGGER.log(Level.FINE, index+" doesnot have a cycle and hence the table "+table_id+" will split");
			
			//Now collecting the table members of the observation we are sampling for
			//I will first create a undirected graph, without the outgoing edge from the observation, Then I will do a 
			//depth first traversal starting from the observation to be sampled and get all the other nodes of the components reachable from it
			UndirectedGraph<Integer,DefaultEdge> u_g =
					new AsUndirectedGraph<Integer, DefaultEdge>(g); //creating the undirected graph from the directed graph
			//now removing the edge (obs_to_sample -> its customer assignment)
			u_g.removeEdge(map_of_references.get(index), map_of_references.get(s.getC(index, list_index)));
			
			//Lets do a depth first traversal now and get all the table members
			DepthFirstIterator<Integer,DefaultEdge> iter = new DepthFirstIterator<Integer,DefaultEdge>(u_g,map_of_references.get(index));
			ArrayList<Integer> new_table_members = new ArrayList<Integer>();
			 while(iter.hasNext())			 
				 new_table_members.add(iter.next());		
			 orig_table_members = new_table_members; //updating orig_table_members to point to new_table_members
			 //Let's get the customers which remained in the original table after the split, do a depth first traversal starting from the original customer assignment of the customer to be sampled
			 iter = new DepthFirstIterator<Integer,DefaultEdge>(u_g,map_of_references.get(s.getC(index, list_index)));
			 ArrayList<Integer> old_table_members = new ArrayList<Integer>();
			 while(iter.hasNext())
				 old_table_members.add(iter.next());
			 
			 //Ok, now since the table has split, update the sampler state accordingly
			 s.setT(s.getT()+1); //incrementing the number of tables
			 s.setC(null, index, list_index); //since this customer has 'no' customer assignment as of now
			 int new_table_id = emptyTables.get(list_index).remove(); //getting an empty table
			 LOGGER.log(Level.FINE, "The new table id after splitting is "+new_table_id);
			 
			 for(int l:new_table_members) //setting the table assignment to the new table number			 
				 s.set_t(new_table_id, l, list_index);
			int old_table_id = table_id;
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<old_table_members.size()-1;i++)			
				sb.append(old_table_members.get(i)).append(",");
			sb.append(old_table_members.get(old_table_members.size()-1));
			s.setCustomers_in_table(sb, old_table_id, list_index);
			
			table_id = new_table_id; //updating, since this is the new table_id of the current customer we are trying to sample for.
			sb = new StringBuffer();
			for(int i=0;i<new_table_members.size()-1;i++)			
				sb.append(new_table_members.get(i)).append(",");
			sb.append(new_table_members.get(new_table_members.size()-1));
			s.setCustomers_in_table(sb, new_table_id, list_index);
		}
		//Now, will sample a new link for the customer
		//get the distance matrix for Prior computation
		ArrayList<CRSMatrix> distanceMatrices = Data.getDistanceMatrices();
		CRSMatrix distance_matrix = distanceMatrices.get(list_index); // getting the correct distance matrix 
		Vector priors = distance_matrix.getRow(index);		
		priors.set(index, HyperParameters.ddcrp_prior); //since according to the ddcrp prior, the prob of a customer forming a link to itself is given by \alpha
		double sum = 0;
		for(int i=0;i<priors.length();i++)
			sum = sum + priors.get(i);
		priors = priors.divide(sum);	
		//Now for each possible 'new' customer assignment, ie for those whose priors != 0
		//we calculate the posterior probability of forming the link with that customer.
		//For that we calculate the change in likelihood if there are joins of tables
		
		ArrayList<Double> posterior = new ArrayList<Double>(); //this will hold the posterior probabilities for all possible customer assignment and we will sample according to these probabilities
		ArrayList<Integer> indexes = new ArrayList<Integer>(); // for storing the indexes of the customers who could be possible assignments
		for(int i=0;i<priors.length();i++)
		{
			if(priors.get(i)!=0)
			{
				indexes.add(i); //adding the index of this possible customer assignment.
				//get the table id of this table				
				int table_proposed = s.get_t(i, list_index); //table_proposed is the proposed table to be joined
				if(table_proposed == table_id) //since the proposed table is the same, hence there will be no change in the likelihood if this is the customer assignment				
					posterior.add(priors.get(i)); //since the posterior will be determined only by the prior probability
				
				else //will have to compute the change in likelihood
				{					
					//get the proposed table members
					String s_table_proposed_members = s.getCustomers_in_table(table_proposed, list_index);					
					String[] table_proposed_members = s_table_proposed_members.split(",");
					//create an arraylist of the members
					ArrayList<Integer> proposed_table_members = new ArrayList<Integer>();
					for(int j=0;j<table_proposed_members.length;j++)
						proposed_table_members.add(Integer.parseInt(table_proposed_members[j]));					
					//Now compute the change in likelihood
					double change_in_log_likelihood = compute_change_in_likelihood(ll,orig_table_members,proposed_table_members,list_index);
					//System.out.println("Change in LL "+change_in_log_likelihood);
					posterior.add(Math.exp(Math.log(priors.get(i)) + change_in_log_likelihood)); //adding the prior and likelihood
				}
			}
		}
		//the posterior probabilities are computed for each possible customer assignment, Now lets sample from it.
		int sample = Util.sample(posterior);		
		int customer_assignment_index = indexes.get(sample); //this is the customer assignment in this iteration, phew!		
		LOGGER.log(Level.FINE, "The sampled link for customer indexed "+index +" of list "+list_index+" is "+customer_assignment_index);
		
		int assigned_table = s.get_t(customer_assignment_index, list_index);
		s.setC(customer_assignment_index, index, list_index); //setting the customer assignment
		if(assigned_table!=table_id) //this is a join of two tables 
		{
			LOGGER.log(Level.FINE, "Table "+table_id+" joins with "+assigned_table);
			s.setT(s.getT()-1); //since there is a join, there is a decrease by 1.			
			for(Integer members:orig_table_members)			
				s.set_t(assigned_table, members, list_index); //setting the table assignment of the old table members to the new assigned table			
			//update the map now
			//First, update the new assigned table to include the table members of the customer's table
			StringBuffer sb_orig_members_in_new_table =new StringBuffer(s.getCustomers_in_table(assigned_table, list_index));
			for(int i=0;i<orig_table_members.size();i++)
				sb_orig_members_in_new_table.append(",").append(orig_table_members.get(i).toString());
			s.setCustomers_in_table(sb_orig_members_in_new_table, assigned_table, list_index);
			//Then, update the orig_table to null
			s.setCustomers_in_table(null,table_id, list_index);
			//Atlast, enqueue this table_id, since this table is empty
			emptyTables.get(list_index).add(table_id);
		}		
		LOGGER.log(Level.FINE, " DONE Sampling link for index "+index+" list_index "+list_index);
	}

	/**
	 * Method to compute the change in log-likelihood due to join of two tables
	 * @param l This will compute the log-likelihood
	 * @param orig_table_members
	 * @param proposed_table_members
	 * @return
	 */
	private static double compute_change_in_likelihood(Likelihood l,ArrayList<Integer> orig_table_members,ArrayList<Integer> proposed_table_members,int list_index )
	{
		double orig_table_loglikelihood = l.computeTableLogLikelihood(orig_table_members, list_index);
		double proposed_table_loglikelihood = l.computeTableLogLikelihood(proposed_table_members, list_index);
		
		//take union of the two lists
		ArrayList<Integer> union_list = new ArrayList<Integer>();
		for(Integer member:orig_table_members)		
			union_list.add(member);
		for(Integer member:proposed_table_members)
			union_list.add(member);
		double table_union_loglikelihood = l.computeTableLogLikelihood(union_list, list_index); 
		
		double change_in_log_likelihood = table_union_loglikelihood - (orig_table_loglikelihood + proposed_table_loglikelihood);
		
		return change_in_log_likelihood;
	}
}
