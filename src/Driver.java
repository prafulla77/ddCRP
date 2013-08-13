import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import sampler.GibbsSampler;

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
		SamplerStateTracker.initializeSamplerState(list_observations);
		SamplerStateTracker.samplerStates.get(0).prettyPrint(System.out);
		//do sampling
		GibbsSampler.doSampling();
		
	
//		 DirectedGraph<Long, DefaultEdge> g =
//		            new DefaultDirectedGraph<Long, DefaultEdge>(DefaultEdge.class);
//		 Long a = new Long(1);
//		 Long b = new Long(2);
//		 Long c = new Long(3);
//		 Long d = new Long(4);
//		 Long e = new Long(3);
//		 
//		 g.addVertex(a);
//		 g.addVertex(b);
//		 g.addVertex(c);
//		 g.addVertex(d);
//		 g.addVertex(e);
//		 
//		 g.addEdge(c, d);
//		 g.addEdge(e,a);
		 
//		 
//		 g.addEdge(a, a);
//		 g.addEdge(b, c);
//		 g.addEdge(c, d);
//		 //g.addEdge(d, a);
//		 
//		 CycleDetector<Long,DefaultEdge> c_d = new CycleDetector<>(g);
//		 System.out.println(c_d.detectCyclesContainingVertex(a));
//		 

		
		//get all the customers who are sitting in the table.
//				String customers_in_table = "1,2,3";
//				long c[]={1l,3l,1l};
//				String customer_indexes[] = customers_in_table.split(",");
//				
//				//Create a graph with customers_in_table as the vertices
//				DirectedGraph<Long, DefaultEdge> g =
//			            new DefaultDirectedGraph<Long, DefaultEdge>(DefaultEdge.class);
//				HashMap<Long,Long> map_of_references = new HashMap<Long,Long>(); //map to store the reference of the vertices so that they can be used later to form edges.
//				
//				for(int i=0;i<customer_indexes.length;i++)		
//				{
//					if(map_of_references.get(Long.parseLong(customer_indexes[i]))==null) //didnot encounter this customer b4 
//					{
//						Long customer_index = Long.parseLong(customer_indexes[i]); // creating the reference for the new customer
//						map_of_references.put(customer_index, customer_index); //putting into the map for future reference while creating the edges
//						g.addVertex(customer_index);								
//					}		
//					//now create an edge with its customer assignment
//					Long customer_assignment = c[Integer.parseInt(customer_indexes[i]) - 1];
//					
//					//lets check we have a vertex already created for this customer or not?
//					if(map_of_references.get(customer_assignment)==null)
//					{ //nope will have to create a new object
//						//instead of creating the object, lets use the customer_assignment reference itself
//						map_of_references.put(customer_assignment, customer_assignment);
//						g.addVertex(customer_assignment);
//					}
//					else
//					{
//						customer_assignment = map_of_references.get(customer_assignment); //since we have made a reference earlier, we will use that (Is it getting clumsy?)				
//					}
//					//adding the edge
//					g.addEdge(map_of_references.get(Long.parseLong(customer_indexes[i])), customer_assignment);//1st arg, the current customer, 2nd arg, its assignment
//					System.out.println(map_of_references.get(Long.parseLong(customer_indexes[i]))+" ---> "+customer_assignment);					
//				}
//				
//				 DepthFirstIterator<Long,DefaultEdge> iter = new DepthFirstIterator<Long,DefaultEdge>(g);
//				 while(iter.hasNext())
//				 {
//					 System.out.println(iter.next());			 
//				 }
//				
				
		
		
	}

}
