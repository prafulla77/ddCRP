import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import model.HyperParameters;
import model.SamplerStateTracker;
import sampler.GibbsSampler;
import util.Util;
import Likelihood.DirichletLikelihood;
import Likelihood.Likelihood;
import data.Data;


public class Driver {
	
	public static int vocab_size = 419; //TO-DO, will take this as a command line param

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		try {
			//setup logging
//			Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
//			logger.setLevel(Level.FINE);
//			FileHandler logFileHandler = new FileHandler("log.txt");
//			logFileHandler.setFormatter(new SimpleFormatter());
//			logger.addHandler(logFileHandler);
		
			long init_time = System.currentTimeMillis();
			//set the hyper-parameters
			ArrayList<Double> dirichlet_params = new ArrayList<Double>(vocab_size);
			for(int i=0;i<vocab_size;i++)
				dirichlet_params.add(0.3);
			HyperParameters.dirichletParam = dirichlet_params;
			HyperParameters.ddcrp_prior = 0.1;
//			
			ArrayList<ArrayList<Double>> list_observations = Data.getObservations();	
			SamplerStateTracker.initializeSamplerState(list_observations);
			//SamplerStateTracker.samplerStates.get(0).prettyPrint(System.out);
			Likelihood l = new DirichletLikelihood();
			
			//do sampling		
			SamplerStateTracker.max_iter = Integer.parseInt(args[1]);
			System.out.println("Gibbs Sampler will run for "+SamplerStateTracker.max_iter+" iterations.");
			for(int i=0;i<SamplerStateTracker.max_iter;i++)
			{
				long init_time_iter = System.currentTimeMillis();
				GibbsSampler.doSampling(l);
				System.out.println("Iteration "+i+" done");
				System.out.println("Took "+(System.currentTimeMillis() - init_time_iter)/(double)1000+" seconds");
			}
			
			long diff = System.currentTimeMillis() - init_time; 
			System.out.println("Time taken for Sampling "+(double)diff/1000+" seconds");		
			for(int i=0;i<list_observations.size();i++)
				Util.printTableConfiguration(i, new PrintStream("tables/table_configuration"+i+".txt"));
			
//			
//			DirichletLikelihood l = new DirichletLikelihood();
//			ArrayList<Integer> t = new ArrayList<Integer>();
//			t.add(1);
//			t.add(2);
//			t.add(3);
//			
//			System.out.println(l.computeTableLogLikelihood(t, 0));
//		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
