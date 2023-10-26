package Util;

import Model.Data;

import java.util.*;

public class DatabaseFormatter {

    private static List<String> acceptedEntries = Arrays.asList("8", "9", "10");

    public static List<Data> formatDatabase(List<String> base, String baseType) {
        List<Data> formattedBase = new LinkedList<>();

        for (String item : base) {
            String[] split = item.split(",");
            double[] input = new double[split.length - 1];
            int output;

            if (split[split.length - 1].equals(baseType)) {

                for (int i = 0; i < input.length; i++)
                    input[i] = (i == 0) ? classifySex(split[i]) : Double.parseDouble(split[i]);

                output = handleOutput(split[split.length - 1]);

                if (!Arrays.stream(input).allMatch(x -> x == 0.0))
                    formattedBase.add(new Data(input, output));
            }
        }

        return formattedBase;
    }

    public static void getTrainingTestBases(List<Data> trainingBase, List<Data> testBase) {
        List<Data> base = new LinkedList<>();

        for (String entry : acceptedEntries)
            base.addAll(FileHandler.loadFile(entry));

        normalize(base);

        splitBase(base, trainingBase, testBase);
    }

    private static void splitBase(List<Data> base, List<Data> trainingBase, List<Data> testBase) {
        int size = (int) (base.size() * 0.7);

        for (int i = 0; i < base.size(); i++) {
            if (i < size)
                trainingBase.add(base.get(i));
            else
                testBase.add(base.get(i));
        }
    }

    private static double classifySex(String sex) {
        if (sex.equalsIgnoreCase("M"))
            return -1;
        else if (sex.equalsIgnoreCase("F"))
            return 0;
        else if (sex.equalsIgnoreCase("I"))
            return 1;

        return 0;
    }

    private static int handleOutput(String value) {
        if (value.equals("8"))
            return 1;
        else if (value.equals("9"))
            return 2;
        else if (value.equals("10"))
            return 3;

        return 0;
    }

    private static void normalize(List<Data> base) {
        List<Data> normalizedData = new LinkedList<>();
        double[] minInput = new double[base.get(0).getInput().length];
        double[] maxInput = new double[base.get(0).getInput().length];

        for (int i = 0; i < minInput.length; i++) {
            minInput[i] = base.get(0).getInput()[i];
            maxInput[i] = base.get(0).getInput()[i];
        }

        for(int i = 1; i < base.size(); i++){
            for (int j = 0; j < maxInput.length; j++) {
                if(base.get(i).getInput()[j] > maxInput[j]) maxInput[j] = base.get(i).getInput()[j];

                if(base.get(i).getInput()[j] < minInput[j]) minInput[j] = base.get(i).getInput()[j];
            }
        }

        for(int i = 0; i < base.size(); i++){
            double[] newInput = new double[minInput.length];
            for (int j = 0; j < maxInput.length; j++) {
                newInput[j] = (base.get(i).getInput()[j] - minInput[j]) / (maxInput[j] - minInput[j]);
            }
            Data newData = new Data(newInput, base.get(i).getOutput());
            normalizedData.add(newData);
        }

        base.clear();
        base.addAll(normalizedData);
    }

}