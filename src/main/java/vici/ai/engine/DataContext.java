package vici.ai.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataContext {

  private Map<String, Integer> data = new HashMap<>();

  private Set<String> actors = new HashSet<>();

  public void add(String actor, int state) {
    data.put(actor, state);
    actors.add(actor);
  }

  public void add(String actor, int offset, int state) {
    data.put(actor + "_" + offset, state);
    actors.add(actor);
  }

  public int get(String actor, int offset) {
    return data.get(actor + "_" + offset);
  }

  public int get(String fullActorName) {
    return data.get(fullActorName);
  }

  public Set<String> getActors() {
    return actors;
  }

  public List<String> getNames() {
    return new ArrayList<String>(data.keySet());
  }

  public static void print(List<DataContext> lines) {

    List<String> header = new ArrayList<>(lines.get(0).data.keySet());
    Collections.sort(header);

    String formatedText = "";
    for (String head : header) {
      formatedText += head + "   ";
    }

    System.out.println(formatedText);

    for (DataContext line : lines) {
      formatedText = "";
      for (String head : header) {
        formatedText += "  " + line.get(head) + "    ";
      }
      System.out.println(formatedText);
    }
  }

  public static void print(DataContext ctx) {

    List<String> header = new ArrayList<>(ctx.data.keySet());
    Collections.sort(header);

    String formatedText = "";
    for (String head : header) {
      formatedText += head + "   ";
    }

    System.out.println(formatedText);

    formatedText = "";
    for (String head : header) {
      formatedText += "  " + ctx.get(head) + "    ";
    }
    System.out.println(formatedText);
  }

}
