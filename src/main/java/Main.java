import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // преобразование CSV-JSON
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> listEmployee = parseCSV(columnMapping, "data.csv");
        String jsonEmployee = listToJson(listEmployee);
        writeString(jsonEmployee, "data.json");

        // преобразование XML-JSON
        List<Employee> listEmployeeXML = parseXML("data.xml");
        String xmlEmployee = listToJson(listEmployeeXML);
        writeString(xmlEmployee, "data2.json");
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));

            NodeList staff = document.getElementsByTagName("employee");
            for (int i = 0; i < staff.getLength(); i++) {
                Node node = staff.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    Employee employee = new Employee(id, firstName, lastName, country, age);

                    employees.add(employee);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
        return employees;
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


    public static void writeString(String jsonEmployee, String fileName) {

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonEmployee);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
