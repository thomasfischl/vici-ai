package vici.ai.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;

import lombok.Value;
import vici.ai.rule.Condition;

@Value
public class ConditionSet {

  private final List<Condition> timeConditions = new ArrayList<>();

  private final List<Condition> timeIntervalConditions = new ArrayList<>();

  private final List<Condition> actorConditions = new ArrayList<>();

  private final Map<String, List<Condition>> conditionMap = new HashMap<>();

  private Random rand = new Random();

  public void addCondition(Condition condition) {
    List<Condition> list = conditionMap.get(condition.getSrcActorName());
    if (list == null) {
      list = new ArrayList<>();
      conditionMap.put(condition.getSrcActorName(), list);
    }
    list.add(condition);
  }

  public Condition randomCondition(List<String> usedActors) {
    // if (rand.nextDouble() > 0.95) {
    // return exceptableConditions.getActorConditions().get(rand.nextInt(exceptableConditions.getActorConditions().size() - 1));
    // }
    //
    // if (rand.nextDouble() > 0.4) {
    // return exceptableConditions.getTimeIntervalConditions().get(rand.nextInt(exceptableConditions.getActorConditions().size() - 1));
    // }
    //
    // return exceptableConditions.getTimeConditions().get(rand.nextInt(exceptableConditions.getTimeConditions().size() - 1));

    List<String> actors;

    if (rand.nextDouble() > 0.8) {
      actors = new ArrayList<>(conditionMap.keySet());
    } else {
      actors = Lists.newArrayList("Hour", "Minute", "DayOfWeek");
    }
    for (String actor : usedActors) {
      actors.remove(actor);
    }

    // Collection<List<Condition>> values = conditionMap.values();
    // Iterator<List<Condition>> it = values.iterator();
    // for (int i = 0, max = rand.nextInt(values.size() - 1); i < max; i++) {
    // it.next();
    // }
    // List<Condition> possibleConditions = it.next();
    List<Condition> possibleConditions;

    if (actors.size() == 0) {
      return null;
    }
    if (actors.size() == 1) {
      possibleConditions = conditionMap.get(actors.get(0));
    } else {
      possibleConditions = conditionMap.get(actors.get(rand.nextInt(actors.size() - 1)));
    }

    if (possibleConditions.size() == 1) {
      return possibleConditions.get(0);
    }

    return possibleConditions.get(rand.nextInt(possibleConditions.size() - 1));
  }

}
