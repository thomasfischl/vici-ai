package vici.ai.ga;

import java.util.List;

import vici.ai.engine.DataContext;

public interface ProblemDef<T> {

  Individium<T> createRandomIndividium();

  void calculateFitness(Individium<T> individium, List<DataContext> data);

  Individium<T> crossover(Individium<T> i1, Individium<T> i2);

  Individium<T> mutate(Individium<T> i1);

  void showPopulationStatistics(Individium<T>[] population);

  void removeUnwantedIndividiums(Individium<T>[] newPopulation);

  boolean isExceptableSolution(Individium<T> bestIndividium);

}
