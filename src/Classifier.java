import Model.Data;
import Util.DatabaseHandler;

import java.util.List;

public class Classifier {

    private double[][] means; // Matriz de médias
    private double[][] standardDeviation; // Matriz de desvio padrão
    private List<Data> trainingBase; // Base que está sendo treinada no momento.

    public Classifier(int numberOfClasses, int numberOfInputs) {
        this.means = new double[numberOfClasses][numberOfInputs];
        this.standardDeviation = new double[numberOfClasses][numberOfInputs];
    }

    /**
     * Classifica uma amostra da base de dados utilizando o Método de Bayes
     *
     * @param sample O objeto Data representando a amostra a ser classificada.
     * @return O resultado da classificação, geralmente um valor numérico ou uma classe.
     */
    public int classify(Data sample) {
        double class1 = calculateProbability(sample.getInput(), DatabaseHandler.typesOfClasses.get(0));
        double class2 = calculateProbability(sample.getInput(), DatabaseHandler.typesOfClasses.get(1));
        double class3 = calculateProbability(sample.getInput(), DatabaseHandler.typesOfClasses.get(2));

        if (class1 > class2 && class1 > class3)
            return 1;
        else if (class2 > class3)
            return 2;
        else
            return 3;
    }

    /**
     * Preenche matrizes de médias e desvios padrão para diferentes tipos de classes em uma base de dados.
     *
     * @param base A lista de objetos Data que contém os dados a serem processados.
     */
    public void fillMatrices(List<Data> base) {
        List<Integer> types = DatabaseHandler.typesOfClasses;

        for (Integer type : types) {
            List<Data> filteredBase = DatabaseHandler.getBaseByClass(base, type);
            int inputLength = filteredBase.get(0).getInput().length;
            for (int i = 0; i < inputLength; i++) {
                double[] samples = DatabaseHandler.getSamples(filteredBase, i);
                means(samples, type, i);
                standardDeviation(samples, type, i);
            }
        }
    }

    /**
     * Calcula a média de um conjunto de valores representados por um array.
     *
     * @param x O array contendo os valores para os quais a média será calculada.
     * @param classType O tipo de classe associado a essas médias.
     * @param sampleNumber O número da amostra em consideração.
     */
    public void means(double[] x, int classType, int sampleNumber) {
        classType -= 1;
        double sum = 0;

        for (int i = 0; i < x.length; i++)
            sum += x[i];

        double mean = sum / x.length;
        this.means[classType][sampleNumber] = mean;
    }

    /**
     * Calcula o desvio padrão de um conjunto de valores representados por um array.
     *
     * @param x O array contendo os valores para os quais o desvio padrão será calculado.
     * @param classType O tipo de classe associado a esse desvio padrão.
     * @param sampleNumber O número da amostra em consideração.
     */
    public void standardDeviation(double[] x, int classType, int sampleNumber) {
        classType -= 1;
        double mean = this.means[classType][sampleNumber];

        double sum = 0;
        for (int i = 0; i < x.length; i++)
            sum += Math.pow((x[i] - mean), 2);

        double r = (sum / x.length);
        this.standardDeviation[classType][sampleNumber] = Math.sqrt(r);
    }

    /**
     * Calcula a densidade de probabilidade de uma variável aleatória seguindo uma distribuição gaussiana (normal).
     *
     * @param x O valor da variável aleatória para o qual a densidade de probabilidade será calculada.
     * @param mean A média da distribuição gaussiana.
     * @param sd O desvio padrão da distribuição gaussiana.
     * @return A densidade de probabilidade para o valor da variável aleatória especificado.
     */
    private double gaussProbabilityDensity(double x, double mean, double sd) {
        double r = 1 / Math.sqrt(2 * Math.PI * sd);
        r = r * Math.exp(-(Math.pow(x - mean, 2) / (2 * Math.pow(sd, 2))));
        return r;
    }

    /**
     * Calcula a distribuição de densidade de probabilidade gaussiana para um conjunto de amostras de um tipo de classe específico.
     *
     * @param sample O array contendo as amostras para as quais a distribuição de densidade de probabilidade será calculada.
     * @param classType O tipo de classe para o qual a distribuição será calculada.
     * @return A distribuição de densidade de probabilidade gaussiana para o conjunto de amostras e tipo de classe especificados.
     */
    private double gaussProbabilityDensityDistribution(double[] sample, int classType) {
        double r = 1;
        classType -= 1;

        for (int i = 0; i < sample.length; i++) {
            double mean = this.means[classType][i];
            double sd = this.standardDeviation[classType][i];
            r *= gaussProbabilityDensity(sample[i], mean, sd);
        }

        return r;
    }

    /**
     * Calcula a probabilidade de uma classe específica em relação a uma base de dados.
     *
     * @param classType O tipo de classe para o qual a probabilidade será calculada.
     * @return A probabilidade da classe específica em relação à base de dados.
     */
    private double getP(int classType) {
        double baseSize = this.trainingBase.size();
        double filteredBaseSize = DatabaseHandler.getBaseByClass(this.trainingBase, classType).size();

        return filteredBaseSize / baseSize;
    }

    private double calculateProbability(double[] sample, int classType) {
        double r = gaussProbabilityDensityDistribution(sample, classType) * getP(classType);

        String formattedNumber = String.format("%.8f", r).replace(",", ".");

        return Double.parseDouble(formattedNumber);
    }

    public double[][] getMeans() {
        return means;
    }

    public double[][] getStandardDeviation() {
        return standardDeviation;
    }

    public void setTrainingBase(List<Data> trainingBase) {
        this.trainingBase = trainingBase;
    }
}
