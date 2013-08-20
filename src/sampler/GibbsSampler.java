/**
 * 
 */
package sampler;

import java.util.ArrayList;
import java.util.HashMap;

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

import data.Data;

/**
 * @author rajarshd
 *
 */
public class GibbsSampler {

	public static void doSampling()
	{
		//create the copy of the latest sampler state and assign it to the new one
		SamplerState s = SamplerStateTracker.samplerStates.get(SamplerStateTracker.current_iter).copy();
		SamplerStateTracker.samplerStates.add(s);
		//increase the number of iteration of sampling by 1
		SamplerStateTracker.current_iter = SamplerStateTracker.current_iter + 1;		
		
		//get the observations
		ArrayList<ArrayList<Double>> all_observations = Data.getObservations();
		
		//Sampling for each list (city/document)
//		for(int i=0;i<all_observations.size();i++)
//		{
//			ArrayList<Double> list = all_observations.get(i); //each city in our case
//			//For each observation in the list sample customer assignments (for each venue in a city)
//			for(int j=0;j<list.size();j++) //Concern: No of observation in a list should not cross the size of integers
//			{				
//				//sample customer link for this observation
//				sampleLink(list,j,i); //sending the list (city) and the index so that the observation can be accessed
//			}		
//		}	
		sampleLink(all_observations.get(10), 0, 0);
	}
	
	private static void sampleLink(ArrayList<Double>list, int index, int list_index)
	{
		//check to see if the table has circle or not
		Double obs_to_sample = list.get(index); //observation whose link has to be sampled
		
		//get the table id where this observation is sitting
		SamplerState s = SamplerStateTracker.samplerStates.get(SamplerStateTracker.current_iter);
		Long table_id = s.get_t(index, list_index); //the table id where the observation is sitting
		
		//get all the customers who are sitting in the table.
		String customers_in_table = s.getCustomers_in_table(table_id.intValue(), list_index);
		String customer_indexes[] = customers_in_table.split(",");
		
		//Create a graph with customers_in_table as the vertices
		DirectedGraph<Long, DefaultEdge> g =
	            new DefaultDirectedGraph<Long, DefaultEdge>(DefaultEdge.class);
		HashMap<Long,Long> map_of_references = new HashMap<Long,Long>(); //map to store the reference of the vertices so that they can be used later to form edges.
		
		for(int i=0;i<customer_indexes.length;i++)		
		{
			if(map_of_references.get(Long.parseLong(customer_indexes[i]))==null) //didnot encounter this customer b4 
			{
				Long customer_index = Long.parseLong(customer_indexes[i]); // creating the reference for the new customer
				map_of_references.put(customer_index, customer_index); //putting into the map for future reference while creating the edges
				g.addVertex(customer_index);								
			}		
			//now create an edge with its customer assignment
			Long customer_assignment = s.getC(Integer.parseInt(customer_indexes[i]), list_index);
			
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
			g.addEdge(map_of_references.get(Long.parseLong(customer_indexes[i])), customer_assignment);//1st arg, the current customer, 2nd arg, its assignment
		} //the graph should be ready
		
		//If the 'obs_to_sample' is a part of a cycle then removing its customer assignment cannot split the table. 
		//Check if there is a cycle containing obs_to_sample
		CycleDetector<Long,DefaultEdge> cycleDetector = new CycleDetector<Long,DefaultEdge>(g);
		//System.out.println(cycleDetector.detectCyclesContainingVertex(map_of_references.get(new Long(index))));
		boolean isCyclePresent = cycleDetector.detectCyclesContainingVertex(map_of_references.get(new Long(index)));
		if(!isCyclePresent) //on removing the link, the table will be split 
		{
			//Now collecting the table members of the observation we are sampling for
			//I will first create a undirected graph, without the outgoing edge from the observation, Then I will do a 
			//depth first traversal starting from the observation to be sampled and get all the other nodes of the components reachable from it
			UndirectedGraph<Long,DefaultEdge> u_g =
					new AsUndirectedGraph<Long, DefaultEdge>(g); //creating the undirected graph from the directed graph
			//now removing the edge (obs_to_sample -> its customer assignment)
			u_g.removeEdge(map_of_references.get(new Long(index)), map_of_references.get(s.getC(index, list_index)));
			
			//Lets do a depth first traversal now and get all the table members
			DepthFirstIterator<Long,DefaultEdge> iter = new DepthFirstIterator<Long,DefaultEdge>(u_g,map_of_references.get(new Long(index)));
			ArrayList<Long> new_table_members = new ArrayList<Long>();
			 while(iter.hasNext())			 
				 new_table_members.add(iter.next());		
			 
			 //Let's get the customers which remained in the original table after the split, do a depth first traversal starting from the original customer assignment of the observation to be sampled
			 iter = new DepthFirstIterator<Long,DefaultEdge>(u_g,map_of_references.get(s.getC(index, list_index)));
			 ArrayList<Long> old_table_members = new ArrayList<Long>();
			 while(iter.hasNext())
				 old_table_members.add(iter.next());
			 
			 //Ok, now since the table has split, update the sampler state accordingly
			 s.setT(s.getT()+1); //incrementing the number of tables
			 s.setC(null, index, list_index); //since the observation has 'no' customer assignment as of now
			 for(Long l:new_table_members) //setting the table assignment to the new table number			 
				 s.set_t(s.getT()-1, l.intValue(), list_index);// It's s.getT()-1 since, the table assignment starts from 0.
			Long old_table_id = table_id;
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<old_table_members.size()-1;i++)			
				sb.append(old_table_members.get(i)).append(",");
			sb.append(old_table_members.get(old_table_members.size()-1));
			s.setCustomers_in_table(sb, old_table_id.intValue(), list_index);
			
			Long new_table_id = s.getT()-1;
			table_id = new_table_id; //since this is the new table_id of the current customer we are trying to sample for.
			sb = new StringBuffer();
			for(int i=0;i<new_table_members.size()-1;i++)			
				sb.append(new_table_members.get(i)).append(",");
			sb.append(new_table_members.get(new_table_members.size()-1));
			s.setCustomers_in_table(sb, new_table_id.intValue(), list_index);
		}
		//Now, will sample a new link for the customer
		//get the distance matrix for Prior computation
		ArrayList<CRSMatrix> distanceMatrices = Data.getDistanceMatrices();
		CRSMatrix distance_matrix = distanceMatrices.get(list_index); // getting the correct distance matrix 
		Vector priors = distance_matrix.getRow(index);		
		priors = priors.normalize();		
		//Now for each possible 'new' customer assignment, ie for those whose priors != 0
		//we calculate the posterior probability of forming the link with that customer.
		//For that we calculate the change in likelihood if there are joins of tables
		
		ArrayList<Double> logPosterior = new ArrayList<Double>(); //this will hold the posterior probabilities for all possible customer assignment and we will sample according to these probabilities
		ArrayList<Integer> indexes = new ArrayList<Integer>(); // for storing the indexes of the customers who could be possible assignments
		for(int i=0;i<priors.length();i++)
		{
			if(priors.get(i)!=0)
			{
				indexes.add(i); //adding the index of this possible customer assignment.
				//get the table id of this table				
				Long table_proposed = s.get_t(i, list_index); //table_proposed is the proposed table to be joined
				if(table_proposed.equals(table_id)) //since the proposed table is the same, hence there will be no change in the likelihood if this is the customer assignment
				{
					logPosterior.add(Math.log10(priors.get(i))); //since the posterior will be determined only by the prior probability
				}
				else //will have to compute the change in likelihood
				{					
					
				}
			}
		}
		
		
	}

}
