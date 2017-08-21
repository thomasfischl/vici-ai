package vici.ai.engine;

import lombok.Value;

@Value
public class RuleEngineMatchSummary implements Comparable<RuleEngineMatchSummary> {

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
    // if (countMatchP == 0) {
    // return -countFP - countFN;
    // }
    //
    return ((double) (countFP * 1.5) / countMatchP) * -10 + Math.min(countMatchP, 10) - numberOfConditions;

    // orginal
    // return (countMatchP * 100) - (countFP * 75) - countFN + (numberOfConditions * -1); // works really good
    // return (countMatchP * 100) + countMatchN - (countFP * 75) - countFN + (numberOfConditions * -1);
  }

  @Override
  public int compareTo(RuleEngineMatchSummary o) {
    // if(o.countMatchP > )

    return 0;
  }
}
