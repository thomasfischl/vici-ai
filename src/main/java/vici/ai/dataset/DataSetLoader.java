package vici.ai.dataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import vici.ai.engine.DataContext;

public class DataSetLoader {

  private static final int NUMBER_OF_ACTORS = 20;
  private static final int NeutralEvent = 0;

  public List<DataContext> load(File dataFile) {
    List<DataContext> data = new ArrayList<>();
    try {
      List<String> lines = Files.readLines(dataFile, Charsets.UTF_8);

      for (String line : lines) {
        String[] cells = line.split(";");

        DataContext ctx = new DataContext();
        data.add(ctx);

        for (int idx = 0; idx < cells.length; idx++) {
          ctx.add("A" + idx, 0, Integer.parseInt(cells[idx]));
        }

      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return data;
  }

  public List<DataContext> loadThomasDataSet(File dataFile) {
    List<DataContext> data = new ArrayList<>();
    try {
      List<String> lines = Files.readLines(dataFile, Charsets.UTF_8);

      for (String line : lines) {
        String[] cells = line.split(";");

        DataContext ctx = new DataContext();
        data.add(ctx);

        ctx.add("A1", 0, Integer.parseInt(cells[0]));
        ctx.add("A1", 1, Integer.parseInt(cells[1]));
        ctx.add("A1", 2, Integer.parseInt(cells[2]));
        ctx.add("A1", 3, Integer.parseInt(cells[3]));
        ctx.add("A1", 4, Integer.parseInt(cells[4]));
        ctx.add("A1", 5, Integer.parseInt(cells[5]));

        ctx.add("A0", 0, Integer.parseInt(cells[6]));
        ctx.add("A0", 1, Integer.parseInt(cells[7]));
        ctx.add("A0", 2, Integer.parseInt(cells[8]));
        ctx.add("A0", 3, Integer.parseInt(cells[9]));
        ctx.add("A0", 4, Integer.parseInt(cells[10]));
        ctx.add("A0", 5, Integer.parseInt(cells[11]));
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return data;
  }

  public List<DataContext> loadThomasDataSetWithTime(File dataFile) {
    List<DataContext> data = new ArrayList<>();
    try {
      List<String> lines = Files.readLines(dataFile, Charsets.UTF_8);

      for (String line : lines) {
        String[] cells = line.split(";");

        DataContext ctx = new DataContext();
        data.add(ctx);

        ctx.add("Hour", 0, Integer.parseInt(cells[0]));
        ctx.add("Minute", 0, Integer.parseInt(cells[1]));
        ctx.add("Time", 0, (Integer.parseInt(cells[0]) * 100) + Integer.parseInt(cells[1]));

        ctx.add("A1", 0, Integer.parseInt(cells[2]));
        ctx.add("A1", 1, Integer.parseInt(cells[3]));
        ctx.add("A1", 2, Integer.parseInt(cells[4]));
        ctx.add("A1", 3, Integer.parseInt(cells[5]));
        ctx.add("A1", 4, Integer.parseInt(cells[6]));
        ctx.add("A1", 5, Integer.parseInt(cells[7]));

        ctx.add("A0", 0, Integer.parseInt(cells[8]));
        ctx.add("A0", 1, Integer.parseInt(cells[9]));
        ctx.add("A0", 2, Integer.parseInt(cells[10]));
        ctx.add("A0", 3, Integer.parseInt(cells[11]));
        ctx.add("A0", 4, Integer.parseInt(cells[12]));
        ctx.add("A0", 5, Integer.parseInt(cells[13]));
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return data;
  }

  public DataSet loadThomasDataSetWithTimeFinal(File dataFile) {
    DataSet set = new DataSet();
    try {
      List<String> lines = Files.readLines(dataFile, Charsets.UTF_8);
      String headers = lines.remove(0);
      String[] headerParts = headers.split(";");

      for (int i = 0; i < NUMBER_OF_ACTORS; i++) {
        System.out.println("A" + i + ": " + headerParts[5 + (i * 6)].trim());
        set.getDeviceNameMapping().put("A" + i + "_0", headerParts[5 + (i * 6)].trim());
      }

      for (String line : lines) {
        String[] cells = line.split(";");

        DataContext ctx = new DataContext();
        set.getData().add(ctx);

        ctx.add("Hour", Integer.parseInt(cells[2].trim()));
        ctx.add("Minute", Integer.parseInt(cells[3].trim()));
        ctx.add("DayOfWeek", Integer.parseInt(cells[4].trim()));

        for (int i = 0; i < NUMBER_OF_ACTORS; i++) {
          ctx.add("A" + i, 0, Integer.parseInt(cells[5 + (i * 6)].trim()));
          ctx.add("A" + i, 1, Integer.parseInt(cells[6 + (i * 6)].trim()));
          ctx.add("A" + i, 2, Integer.parseInt(cells[7 + (i * 6)].trim()));
          ctx.add("A" + i, 3, Integer.parseInt(cells[8 + (i * 6)].trim()));
          ctx.add("A" + i, 4, Integer.parseInt(cells[9 + (i * 6)].trim()));
          ctx.add("A" + i, 5, Integer.parseInt(cells[10 + (i * 6)].trim()));
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return set;
  }

  public List<DataContext> loadWithOffset(File dataFile, int offsetSize) {
    List<DataContext> data = load(dataFile);

    for (int i = 0; i < data.size(); i++) {
      DataContext ctx = data.get(i);
      Set<String> actors = ctx.getActors();

      for (String actor : actors) {
        for (int offset = 1; offset < offsetSize; offset++) {

          if (i - offset < 0) {
            // no offset data exists
            ctx.add(actor, offset, NeutralEvent);
          } else {
            ctx.add(actor, offset, data.get(i - offset).get(actor, 0));
          }
        }
      }
    }

    return data;
  }

}
