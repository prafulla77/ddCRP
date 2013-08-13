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
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
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
		sampleLink(all_observations.get(10), 10, 10);
	}
	
	private static void sampleLink(ArrayList<Double>list, int index, int list_index)
	{
		//get the distance matrix for Prior computation
		//ArrayList<CRSMatrix> distanceMatrices = Data.getDistanceMatrices();
		//CRSMatrix distance_matrix = distanceMatrices.get(list_index); // getting the correct distance matrix 		
		
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
		CycleDetector<Long,DefaultEdge> cycleDetector = new CycleDetector<>(g);
		System.out.println(cycleDetector.detectCyclesContainingVertex(map_of_references.get(new Long(index)))); 
	}

}
