package test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import model.HyperParameters;
import model.SamplerState;
import model.SamplerStateTracker;
import model.Posterior;
import sampler.GibbsSampler;
import test.Test;
import test.TestSample;
import test.TestPrediction;


import util.Util;
import Likelihood.DirichletLikelihood;
import Likelihood.Likelihood;
import data.Data;


/**
 * Represents a prediction experiment with the model, paramaterized by the 
 * model hyperparameters, the test data generation parameters, and the number 
 * of experimental runs.
 * @author Justin Cranshaw
 *
 */
public class Experiment {

  private Test test;

  //the number of times to run the experiment
  private int numExperiments; 

  //the number of Gibbs sampling iterations per exp 
  private int numIterationsPerExperiment; 

  //the hyperparameters for the model
  private HyperParameters hyperParameters; 

  //for each experimental run, for each city, for each test sample, a prediction
  private ArrayList<ArrayList<ArrayList<TestPrediction>>> experimentResults = new ArrayList<ArrayList<ArrayList<TestPrediction>>>();


  public Experiment
  (Test t, int numE, int numI, HyperParameters h)
  {
    test = t;
    numExperiments = numE;
    numIterationsPerExperiment = numI;
    hyperParameters = h;
  }

  // needs:
  //    vocab_size
  //  
  public void runExperiment() {

    long init_time = System.currentTimeMillis();
  
    ArrayList<ArrayList<Double>> listObservations = Data.getObservations();  

    //SamplerStateTracker.samplerStates.get(0).prettyPrint(System.out);
    DirichletLikelihood l = new DirichletLikelihood(hyperParameters);
    
    // For each experiment
    for (int i=0; i<numExperiments; i++) {
      //Initialize the sampler state
      SamplerStateTracker.initializeSamplerState(listObservations);
      SamplerStateTracker.max_iter = numIterationsPerExperiment;

      //BEFORE DOING SAMPLING, LETS SEPARATE SOME TEST DATA
      test.generateTestSamples(); //it will populate appropriate data structures which will be accessed later in the sampling class
         
      //INFERENCE           
      System.out.println("Gibbs Sampler will run for "+SamplerStateTracker.max_iter+" iterations.");
      for (int j=0; j<SamplerStateTracker.max_iter; j++)
      {
        long init_time_iter = System.currentTimeMillis();
        GibbsSampler.doSampling(l, test);
        System.out.println("----------------------");
        System.out.println("Iteration "+j+" done");
        System.out.println("Took "+(System.currentTimeMillis() - init_time_iter)/(double)1000+" seconds");
        SamplerState curr = SamplerStateTracker.samplerStates.get(j);
        if (j > 0) {
          SamplerState prev = SamplerStateTracker.samplerStates.get(j-1);
          System.out.println("Table similarity from prev: "+curr.tableJiccardSimilarity(prev));
        }
        curr.prettyPrint(System.out);
        // curr.estimateThetas();

        double ll = l.computeFullLogLikelihood(curr.getCustomersAtTableList());
        System.out.println("Log Likelihood: " + ll);
      }
      
      //PREDICTION
      ArrayList<ArrayList<TestPrediction>> testPredictions = new ArrayList<ArrayList<TestPrediction>>();
      ArrayList<ArrayList<TestSample>> testSamples = test.getTestSamples();
      for (int j=0; j<testSamples.size(); j++) {
        ArrayList<TestPrediction> cityPredictions = new ArrayList<TestPrediction>();
        ArrayList<TestSample> citySamples = testSamples.get(j);
        for (int k=0; k<citySamples.size(); k++) {
          TestSample s = citySamples.get(k);
          TestPrediction p = makeRandomPrediction(s);
          cityPredictions.add(p);
        }
        testPredictions.add(cityPredictions);
      }
      experimentResults.add(testPredictions);

      Posterior.estimatePosterior(0);
      Posterior.prettyPrint(System.out);

      // outputResultsToCsv();
    }

  }

  public TestPrediction makeRandomPrediction(TestSample s) {
    // For now, just make a fake prediction
    Random randomGenerator = new Random();
    double fakePred = (double) randomGenerator.nextInt(hyperParameters.getVocabSize());
    TestPrediction p = new TestPrediction(s.getListIndex(), 
                                          s.getCityIndex(), 
                                          s.getVenueCategory(), 
                                          fakePred);

    return p;

    // int numCats = 400;  // TODO: NEED TO FIGURE OUT WHERE THIS SHOULD GO
    // for (int x=0; x<numCats; x++) {

    // }    
  }

  public void computePosteriorPredictiveProb() {

  }

  public void outputResultsToCsv() {

    System.out.println("experimentNumber,cityIndex,venueIndex,trueCategory,predictedCategory");

    for (int i=0; i<experimentResults.size(); i++) {
      ArrayList<ArrayList<TestPrediction>> singleExperiment = experimentResults.get(i);
      for (int j=0; j<singleExperiment.size(); j++) {
        ArrayList<TestPrediction> cityResults = singleExperiment.get(j);
        for (int k=0; k<cityResults.size(); k++) {
          TestPrediction p = cityResults.get(k);
          System.out.println( String.valueOf(i) + "," +
                              String.valueOf(p.getCityIndex()) + "," +
                              String.valueOf(p.getListIndex()) + "," +
                              String.valueOf(p.getVenueCategory()) + "," +
                              String.valueOf(p.getPredictedVenueCategory()) );
        }
      }
    }

  }

}
