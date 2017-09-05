package vici.ai.engine;

import lombok.Value;

@Value
public class RuleEngineMatchSummary {

  private int countMatchP;
  private int countMatchN;
  private int countFN;
  private int countFP;

  private int numberOfConditions;

  @Override
  public String toString() {
    return "(" + countMatchP + "/" + countMatchN + "/" + countFN + "/" + countFP + "/" + ((double) countFP / countMatchP) + ") [" + getFitness() + "]";
  }

  /**
   * Real magic is happen in this method!
   * 
   * @return
   */
  public double getFitness() {

    if (countFP == 0 && countMatchP == 0) {
      return -1000;
    }

    if (countMatchP == 0) {
      return -1000 * countFP;
    }
    // return ((double) (countFP * 1.5) / countMatchP) * -10 + Math.min(countMatchP, 10) - numberOfConditions;

    int max = countFN + countFP + countMatchN + countMatchP;

    double positivRatio = (double) countMatchP / max;
    double positivFRatio = (double) countMatchN / max;
    double negativRatio = (double) countFP / max;

    double boost = 0;

    if (countMatchP > 10) {
      boost += 0.4;
    }

    if (countFP > 10) {
      boost -= 0.4;
    }

    return positivRatio - negativRatio + Math.min(countMatchP * 0.001, 0.01) - Math.min(countFP * 0.001, 0.02) + boost;

  }

}
