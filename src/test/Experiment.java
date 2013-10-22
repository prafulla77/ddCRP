package test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import model.HyperParameters;
import model.SamplerStateTracker;
import sampler.GibbsSampler;
import test.Test;
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
  private int numExperiments; //the number of times to run the experiment
  private int numIterationsPerExperiment; //the number of Gibbs sampling iterations per exp 
  private HyperParameters hyperParameters; 

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
    SamplerStateTracker.initializeSamplerState(listObservations);
    SamplerStateTracker.max_iter = numIterationsPerExperiment;

    //SamplerStateTracker.samplerStates.get(0).prettyPrint(System.out);
    DirichletLikelihood l = new DirichletLikelihood(hyperParameters);
    
    // For each experiment
    for (int i=0; i<numExperiments; i++) {
      //BEFORE DOING SAMPLING, LETS SEPARATE SOME TEST DATA
      test.generateTestSamples(); //it will populate appropriate data structures which will be accessed later in the sampling class
            
      System.out.println("Gibbs Sampler will run for "+SamplerStateTracker.max_iter+" iterations.");
      for(int j=0; j<SamplerStateTracker.max_iter; j++)
      {
        long init_time_iter = System.currentTimeMillis();
        GibbsSampler.doSampling(l, test);
        System.out.println("Iteration "+j+" done");
        System.out.println("Took "+(System.currentTimeMillis() - init_time_iter)/(double)1000+" seconds");
      }
      
    }

  }

}
