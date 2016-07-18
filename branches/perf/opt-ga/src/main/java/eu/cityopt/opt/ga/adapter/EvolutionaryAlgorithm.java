package eu.cityopt.opt.ga.adapter;

import org.opt4j.core.optimizer.OptimizerModule;
import org.opt4j.optimizers.ea.EvolutionaryAlgorithmModule;
import org.opt4j.optimizers.ea.EvolutionaryAlgorithmModule.CrossoverRateType;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.opt.AlgorithmParameters;

/**
 * Provides the Opt4J EvolutionaryAlgorithm for CityOPT.
 *
 * @author Hannu Rummukainen
 */
public class EvolutionaryAlgorithm extends AbstractOpt4JAlgorithm {
    protected OptimizerModule configureOptimizer(
            AlgorithmParameters parameters) throws ConfigurationException {
        EvolutionaryAlgorithmModule ea = new EvolutionaryAlgorithmModule();
        ea.setGenerations(parameters.getInt("number of generations", 10));
        ea.setAlpha(parameters.getInt("population size", 100));
        ea.setMu(parameters.getInt("number of parents per generation", 25));
        ea.setLambda(parameters.getInt("number of offspring per generation", 25));
        ea.setCrossoverRate(parameters.getDouble("crossover rate", 0.95));
        ea.setCrossoverRateType(CrossoverRateType.CONSTANT);
        return ea;
    }

    @Override
    public String getName() {
        return "genetic algorithm";
    }
}
