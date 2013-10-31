import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import model.HyperParameters;
import model.SamplerStateTracker;
import sampler.GibbsSampler;
import test.Test;
import test.TestUniform;
import test.Experiment;
import util.Util;
import Likelihood.DirichletLikelihood;
import Likelihood.Likelihood;
import data.Data;


public class Driver {
  
  public static final int VOCAB_SIZE = 419; //TO-DO, will take this as a command line param

  /**
   * @param args
   * @throws FileNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException {
    // TODO Auto-generated method stub
    try {

      // Run Gibbs samplling for 2 iterations if no command line args
      int numIter = 2;
      if(args.length!=0) {
        numIter = Integer.parseInt(args[1]);
      }

      //do sampling   

      Test t = new TestUniform(100);
      t.generateTestSamples();


      ArrayList<Double> dirichletParams = new ArrayList<Double>(VOCAB_SIZE);
      for(int i=0;i<VOCAB_SIZE;i++)
        dirichletParams.add(0.1);
      HyperParameters h = new HyperParameters(VOCAB_SIZE, dirichletParams, 0.75);
      Experiment e = new Experiment(t, 1, numIter, h);
      e.runExperiment();

      // long diff = System.currentTimeMillis() - init_time; 
      // System.out.println("Time taken for Sampling "+(double)diff/1000+" seconds");    
      // for(int j=0; j<listObservations.size(); j++)
      //   Util.printTableConfiguration(j, new PrintStream("../../tables/table_configuration"+j+".txt"));

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    
  }

}
