package Util;

import Model.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static List<String> base = loadBase();

    public static List<Data> loadFile(String baseType) {
        String fileName = "baseT.txt".replace("T", baseType);

        if (!(new File(fileName).exists()))
            createFile(fileName, baseType);

        List<String> data = reader(fileName);

        return DatabaseHandler.formatDatabase(data, baseType);
    }

    public static void createFile(String fileName, String baseClassification) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);

            for (String value : base) {
                if (isBaseType(value, baseClassification)) {
                    bw.write(value);
                    bw.newLine();
                }
            }

            bw.close();
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    public static List<String> reader(String fileName) {
        List<String> data = new ArrayList<>();

        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            String line;

            while((line = br.readLine()) != null) {
                data.add(line.toLowerCase());
            }

            br.close();
        } catch (IOException e) {
            System.err.println("Arquivo " + fileName + " não encontrado!");
        }

        return data;
    }

    private static boolean isBaseType(String value, String baseClassification) {
        String[] array = value.split(",");

        return array[array.length - 1].equals(baseClassification);
    }

    private static List<String> loadBase() {
        String file = "abalone.data";
        return reader(file);
    }

}