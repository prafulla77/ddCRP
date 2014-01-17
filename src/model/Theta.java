package model;

import data.Data;
import test.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

import org.la4j.matrix.sparse.CRSMatrix;


/**
 * Given a customer assignment (sampler state), this computes the inferred 
 * multinomial distribution parameters at each table
 * @author jcransh
 */
public class Theta {

  /** 
   * The multinomial parameter seen for each city, for each table in the city.  each theta is a CRSMatrix
   */
  private ArrayList<HashMap<Integer, CRSMatrix>> thetas = new ArrayList<HashMap<Integer, CRSMatrix>>();

  /**
   * The SamplerState for which we'd like to compute thetas for
   */ 
  private static SamplerState samplerState;

  /**
   * The model hyperparameters
   */ 
  private static HyperParameters hyperParameters;

  /**
   * Getter for the samplerState
   */
  public SamplerState getSamplerState() {
    return samplerState;
  }

  /**
   * Setter for the samplerState
   */
  public void setSamplerState(SamplerState s) {
    samplerState = s;
  }

  /**
   * Getter for the samplerState
   */
  public HyperParameters getHyperParameters() {
    return hyperParameters;
  }

  /**
   * Setter for the samplerState
   */
  public void setHyperParameters(HyperParameters h) {
    hyperParameters = h;
  }  

 /**
   * Getter for the computed thetas
   */
  public ArrayList<HashMap<Integer, CRSMatrix>> getThetas() {
    return thetas;
  }

  /*
   * Computes the value of the theta vectors for this stampler state, for each city, for each table in the city
   * Each table has a CRSMatrix theta, where
   * theta_j = (N_j + a_j) / (n + sum_i(a_i))
   * where a_j is the Dirichlet prior parameter
   */
  public void estimateThetas(Test t) {
    ArrayList<ArrayList<Double>> observations = Data.getObservations();  
    ArrayList<ArrayList<CRSMatrix>> myThetas = new ArrayList<ArrayList<CRSMatrix>>();

    ArrayList<HashMap<Integer, HashSet<Integer>>> customersAtTableList = samplerState.getCustomersAtTableList();

    // for each city
    //     for each table in the city
    //         init a CRSMatrix of length of the vocab size (all zeros)
    //             for each observation at the table, compute the counts per category

    // for each city
    for (int i=0; i<customersAtTableList.size(); i++) {
      HashMap<Integer, HashSet<Integer>> cityTablesMap = customersAtTableList.get(i);
      HashSet<HashSet<Integer>> citySeatingsSet = new HashSet<HashSet<Integer>>();
      
      ArrayList<Double> cityObservations = observations.get(i); // get observations from ith city
      HashMap<Integer,Integer> venueIds = t.getTestVenueIdsForCity(i); // Ids in teh test sample

      HashMap<Integer, CRSMatrix> cityThetas = new HashMap<Integer, CRSMatrix>();

      // for each table in the city
      for (Map.Entry<Integer, HashSet<Integer>> entry : cityTablesMap.entrySet()) {
        Integer tableId = entry.getKey();
        HashSet<Integer> tableMembers = entry.getValue();

        if (tableMembers!=null && tableMembers.size() > 0) {
          // Initialize the table's theta vector
          CRSMatrix myTheta = new CRSMatrix(1,hyperParameters.getVocabSize()); 
    
          // add the dirichlet parameters
          ArrayList<Double> dirichletParam =  hyperParameters.getDirichletParam();
          for (int j=0; j<dirichletParam.size(); j++) {
            myTheta.set(0, j, dirichletParam.get(j));
          }

          // for each customer at the table
          for (Integer j : tableMembers) {
            // make sure j is not in the test set (TODO: can do this more efficiently with a set diff)
            if(venueIds.get(j)==null) {
              Integer observation = cityObservations.get(j).intValue();
              double currentObservationCount = myTheta.get(0, observation);
              myTheta.set(0, observation, currentObservationCount + 1);
            }
          }

          // get the normalizing constant
          double norm = 0.0;
          for (int j=0; j<hyperParameters.getVocabSize(); j++) {
            norm += myTheta.get(0,j);
          }

          // divide by the normalizing constant
          for (int j=0; j<hyperParameters.getVocabSize(); j++) {
            double thetaJ = myTheta.get(0,j);
            myTheta.set(0, j, thetaJ/norm);
          }          

          // add this table's theta to the hash for this city
          cityThetas.put(tableId, myTheta);
        }
      } // end for each table
      thetas.add(cityThetas);
    } // end for each city
  }

  public void prettyPrint() {
    // for each city
    for (int i=0; i<thetas.size(); i++) {
      HashMap<Integer, CRSMatrix> cityThetas = thetas.get(i);
      System.out.println("City " + i);
      // for each table
      for (Map.Entry<Integer, CRSMatrix> entry : cityThetas.entrySet()) {
        Integer tableId = entry.getKey();
        System.out.println("   Table " + tableId);
        CRSMatrix theta = entry.getValue();
        String out = "       ";
        for (int k=0; k<hyperParameters.getVocabSize(); k++) {
          double count = theta.get(0, k);
          if (count > 0) {
            out += k + ":" + count + " ";
          } 
        }
        if (out != "       ")
          System.out.println(out);
      }
    }
  }

}