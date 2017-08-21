package vici.ai.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import vici.ai.engine.DataContext;

public class GA<T> {

  private GAConfig config;

  private Individium<T>[] population;

  private ProblemDef<T> problemDef;

  private ExecutorService executorService;

  public GA(GAConfig config, ProblemDef<T> problemDef) {
    this.config = config;
    this.problemDef = problemDef;

    population = new Individium[config.getPopulationSize()];

    executorService = Executors.newFixedThreadPool(config.getThreadCount());
  }

  public Individium<T> train(List<DataContext> data) {
    try {
      // create population
      for (int i = 0; i < population.length; i++) {
        population[i] = problemDef.createRandomIndividium();
      }

      // evaluate population fitness
      evaluatePopulationFitness(data);

      int currPopulationSize;

      for (int iteration = 0; iteration < config.getMaxNumberOfGenerations(); iteration++) {
        Individium<T>[] newPopulation = new Individium[config.getPopulationSize()];
        currPopulationSize = 0;

        // elitism
        currPopulationSize = fillPopulitionWithBestXIndividiums(newPopulation, currPopulationSize);

        // create new individiums with the crossover function
        currPopulationSize = fillPopulationWithCrossoverIndividiums(newPopulation, currPopulationSize);

        // create new individiums with the mutation function
        currPopulationSize = fillPopulationWithMutatedIndividiums(newPopulation, currPopulationSize);

        // fill the rest of the population wiht new random individiums for more diversitiy
        fillPopulationWithRandomIndividiums(newPopulation, currPopulationSize);

        problemDef.removeUnwantedIndividiums(newPopulation);

        population = newPopulation;
        // evaluate population fitness
        evaluatePopulationFitness(data);

        // Print best individium
        sortPopulation(population);

        if (iteration % 10 == 0) {
          showStatistics(iteration);
          // System.out.println("Hero[-1]: " + population[1]);
          // System.out.println("Hero[-2]: " + population[2]);
          // System.out.println("Hero[-3]: " + population[3]);
          // System.out.println("Hero[-4]: " + population[4]);
          // System.out.println("Hero[-5]: " + population[5]);
          // System.out.println("Losser: " + population[population.length - 1]);
        }

        if (problemDef.isExceptableSolution(getBestIndividium())) {
          showStatistics(iteration);

          Individium<T> bestIndividium = getBestIndividium();
          bestIndividium.setAcceptableSolution(true);
          return bestIndividium;
        }

      }

      return getBestIndividium();
    } finally {
      executorService.shutdownNow();
    }
  }

  private void showStatistics(int iteration) {
    System.out.println("Generation " + iteration);
    problemDef.showPopulationStatistics(population);
    System.out.println("Hero[ 0]: " + getBestIndividium());
  }

  private int fillPopulitionWithBestXIndividiums(Individium<T>[] newPopulation, int currPopulationSize) {
    for (int i = 0; i < config.getElitism(); i++) {
      newPopulation[currPopulationSize] = population[i];
      currPopulationSize++;
    }
    return currPopulationSize;
  }

  private int fillPopulationWithMutatedIndividiums(Individium<T>[] newPopulation, int currPopulationSize) {

    newPopulation[currPopulationSize] = problemDef.mutate(getBestIndividium());
    currPopulationSize++;
    newPopulation[currPopulationSize] = problemDef.mutate(getBestIndividium());
    currPopulationSize++;

    for (int i = 0; i < config.getNumberOfMutationOperations(); i++) {
      Individium<T> i1 = selectIndividiumWithTournamentSelector(population);
      newPopulation[currPopulationSize] = problemDef.mutate(i1);
      currPopulationSize++;
    }

    return currPopulationSize;
  }

  private int fillPopulationWithCrossoverIndividiums(Individium<T>[] newPopulation, int currPopulationSize) {

    for (int i = 0; i < config.getNumberOfCrossoverOperations(); i++) {

      Individium<T> i1 = selectIndividiumWithTournamentSelector(population);
      Individium<T> i2 = selectIndividiumWithTournamentSelector(population);

      newPopulation[currPopulationSize] = problemDef.crossover(i1, i2);
      currPopulationSize++;
    }

    return currPopulationSize;
  }

  private Individium<T> selectIndividiumWithTournamentSelector(Individium<T>[] population) {
    int pos1 = config.getRand().nextInt(population.length - 1);
    int pos2 = config.getRand().nextInt(population.length - 1);
    Individium<T> i1 = population[pos1];
    Individium<T> i2 = population[pos2];

    if (i1.getFitness() > i2.getFitness()) {
      return i1;
    } else {
      return i2;
    }
  }

  private void fillPopulationWithRandomIndividiums(Individium<T>[] population, int currPopulationSize) {
    for (int i = currPopulationSize; i < population.length; i++) {
      population[i] = problemDef.createRandomIndividium();
    }
  }

  private Individium<T> getBestIndividium() {
    return population[0];
  }

  private void sortPopulation(Individium<T>[] population) {
    Arrays.sort(population, (o1, o2) -> Double.compare(o2.getFitness(), o1.getFitness()));
  }

  private void evaluatePopulationFitness(List<DataContext> data) {
    List<Future<?>> fList = new ArrayList<>();
    for (Individium<T> individium : population) {
      Future<?> f = executorService.submit(() -> problemDef.calculateFitness(individium, data));
      fList.add(f);
    }

    for (Future<?> future : fList) {
      try {
        future.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

  }

}
