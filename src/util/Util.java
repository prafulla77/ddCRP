package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.la4j.matrix.sparse.CRSMatrix;

public class Util {

	//path to the distances file
	private static final String distanceFile = "Data/distances.txt";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		createDistanceMatrix();		
	}
	
	public static void createDistanceMatrix()
	{
		try
		{
			//read the distance file
			BufferedReader reader = new BufferedReader(new FileReader(distanceFile));
			String line;			
			ArrayList<CRSMatrix> distanceMatrices = new ArrayList<CRSMatrix>();
			while((line = reader.readLine())!=null) //each line represents a city or a document etc....
			{			
				String[] eachDistances = line.split(" ");
				//get the number of total observations in each line
				String[] last_obs_pair =  eachDistances[eachDistances.length-1].split(":");
				int numObservations = Integer.parseInt(last_obs_pair[0]);
				//create a sparse matrix for this line
				//TO Discuss: Should i create an (n+1 * n+1) matrix to keep the notation a[i][j] constant
				CRSMatrix dist_mat = new CRSMatrix(numObservations,numObservations); //create a square matrix. This is a Compressed Row Storage sparse matrix.
				//Fill up the distance matrix
				for(String obs:eachDistances)
				{
					String[] obs_pairs = obs.split(":");
					int first_id = Integer.parseInt(obs_pairs[0]);
					int second_id = Integer.parseInt(obs_pairs[1]);
					Double distance = Double.parseDouble(obs_pairs[2]);
					dist_mat.set(first_id-1,second_id-1, distance);					
				}
				distanceMatrices.add(dist_mat);
			}				
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		
		
	}

}
