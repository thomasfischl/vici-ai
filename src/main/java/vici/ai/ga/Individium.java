package vici.ai.ga;

import lombok.Data;

@Data
public class Individium<T> {

  private T value;

  private double fitness;

  private String resultDetails;

  private Object resultData;

  private boolean acceptableSolution = false;

  public Individium(T value) {
    this.value = value;
  }

}
