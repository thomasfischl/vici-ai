package vici.ai.ga;

import java.util.Random;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class GAConfig {

  private final int populationSize;

  private final int numberOfCrossoverOperations;

  private final int numberOfMutationOperations;

  private final int maxNumberOfGenerations;

  private final int elitism;

  private final int threadCount;
  
  private final Random rand = new Random();

}
