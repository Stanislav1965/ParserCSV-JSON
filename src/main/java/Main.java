import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listEmployee = parseCSV(columnMapping, fileName);

        String jsonEmployee = listToJson(listEmployee);
        writeString(jsonEmployee);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> listEmployee = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            listEmployee = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return listEmployee;
    }

    public static String listToJson(List<Employee> listEmployee) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(listEmployee, listType);
    }


    public static void writeString(String jsonEmployee) {

        try (FileWriter file = new FileWriter("data.json")) {
            file.write(jsonEmployee);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
