package vici.ai.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class StateRangeService {

  private final List<Integer> actorStats = Lists.newArrayList(1);
  private final List<Integer> hourStats = Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
  private final List<Integer> minuteStats = Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
      25, 26, 27, 28,
      29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
      50, 51, 52, 53, 54, 55, 56, 57, 58, 59);

  private final List<Integer> dayOfWeekStats = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7);

  private final Random rand = new Random();

  public void removeMinuteState(int val) {
    minuteStats.remove(Integer.valueOf(val));
  }

  public void removeHourState(int val) {
    hourStats.remove(Integer.valueOf(val));
  }

  public void removeDayOfWeekState(int val) {
    dayOfWeekStats.remove(Integer.valueOf(val));
  }

  public int getRandomState(String actor) {
    if (actor.startsWith("A")) {
      return actorStats.get(rand.nextInt(actorStats.size()));
    } else if (actor.equals("Hour")) {
      return hourStats.get(rand.nextInt(hourStats.size()));
    } else if (actor.equals("Minute")) {
      return minuteStats.get(rand.nextInt(minuteStats.size()));
    } else if (actor.equals("DayOfWeek")) {
      return dayOfWeekStats.get(rand.nextInt(dayOfWeekStats.size()));
    }
    throw new IllegalStateException("invalid actor " + actor);
  }

  public int getStateCount(String actor) {
    if (actor.startsWith("A")) {
      return actorStats.size();
    } else if (actor.equals("Hour")) {
      return hourStats.size();
    } else if (actor.equals("Minute")) {
      return minuteStats.size();
    } else if (actor.equals("DayOfWeek")) {
      return dayOfWeekStats.size();
    }
    throw new IllegalStateException("invalid actor " + actor);
  }

  public int getHighestValue(String actor) {
    if (actor.startsWith("A")) {
      return 1;
    } else if (actor.equals("Hour")) {
      return 23;
    } else if (actor.equals("Minute")) {
      return 59;
    } else if (actor.equals("DayOfWeek")) {
      return 7;
    }
    throw new IllegalStateException("invalid actor " + actor);
  }

  public int getLowestValue(String actor) {
    if (actor.startsWith("A")) {
      return 0;
    } else if (actor.equals("Hour")) {
      return 0;
    } else if (actor.equals("Minute")) {
      return 0;
    } else if (actor.equals("DayOfWeek")) {
      return 1;
    }
    throw new IllegalStateException("invalid actor " + actor);
  }

  public int getNumberOfPossibleGens() {
    return hourStats.size() + minuteStats.size() + dayOfWeekStats.size() + (actorStats.size() * getTargetActors().size());
  }

  public ArrayList<String> getTargetActors() {
    return Lists.newArrayList("A0_0", "A1_0", "A2_0", "A3_0", "A4_0", "A5_0", "A6_0", "A7_0", "A8_0", "A9_0", "A10_0", "A11_0", "A12_0", "A13_0", "A14_0",
        "A15_0", "A16_0", "A17_0", "A18_0", "A19_0");
  }

  public ArrayList<String> getSourceActorForOffsetCondition() {
    return Lists.newArrayList("A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "A12", "A13", "A14",
        "A15", "A16", "A17", "A18", "A19");

  }

  public String getRandomSourceActorForOffsetCondition() {
    ArrayList<String> actors = getSourceActorForOffsetCondition();
    return actors.get(rand.nextInt(actors.size() - 1));
  }

}
