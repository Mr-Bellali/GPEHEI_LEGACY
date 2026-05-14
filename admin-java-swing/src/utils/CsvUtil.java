package utils;

import javax.swing.table.TableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {

    public static void exportToCsv(TableModel model, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.print(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) writer.print(",");
            }
            writer.println();

            // Write data
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object val = model.getValueAt(i, j);
                    writer.print(val != null ? val.toString().replace(",", ";") : "");
                    if (j < model.getColumnCount() - 1) writer.print(",");
                }
                writer.println();
            }
        }
    }

    public static List<String[]> importFromCsv(File file) throws IOException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { // Skip headers
                    firstLine = false;
                    continue;
                }
                data.add(line.split(","));
            }
        }
        return data;
    }
}
