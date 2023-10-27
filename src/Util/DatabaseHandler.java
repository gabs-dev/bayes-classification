package Util;

import Model.Data;

import java.util.*;

public class DatabaseHandler {

    private static List<String> acceptedEntries = Arrays.asList("8", "9", "10");
    public static List<Integer> typesOfClasses = Arrays.asList(1, 2, 3);

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

        splitBase(base, trainingBase, testBase);
    }

    /**
     * Obtém uma lista de objetos Data filtrados por um valor de classe específico.
     *
     * @param base A lista de objetos Data a ser filtrada.
     * @param classType O valor da classe usado como critério de filtro.
     * @return Uma lista de objetos Data que pertencem à classe especificada.
     */
    public static List<Data> getBaseByClass(List<Data> base, int classType) {
        return base.stream()
                .filter(x -> x.getOutput() == classType)
                .toList();
    }

    /**
     * Retorna um vetor contendo um conjunto específico de amostras.
     *
     * @param base Lista de objetos Data a partir do qual o valores serão extraídos.
     * @param sampleNumber O índice da amostra desejada a ser extraída de cada objeto Data.
     * @return Um array de valores double que contém as amostras extraídas.
     */
    public static double[] getSamples(List<Data> base, int sampleNumber) {
        double[] samples = new double[base.size()];

        for (int i = 0; i < samples.length; i++) {
            samples[i] = base.get(i).getInput()[sampleNumber];
        }

        return samples;
    }

    private static void splitBase(List<Data> base, List<Data> trainingBase, List<Data> testBase) {
        int size = (int) (base.size() * 0.7);

        Collections.shuffle(base);

        for (int i = 0; i < base.size(); i++) {
            if (i < size)
                trainingBase.add(base.get(i));
            else
                testBase.add(base.get(i));
        }

        sortBase(trainingBase);
        sortBase(testBase);
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

    private static void sortBase(List<Data> base) {
        Collections.sort(base, new Comparator<Data>() {
            @Override
            public int compare(Data o1, Data o2) {
                return Integer.compare(o1.getOutput(), o2.getOutput());
            }
        });
    }
}