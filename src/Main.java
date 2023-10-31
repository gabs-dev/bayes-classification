import Model.Data;
import Util.DatabaseHandler;

import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Data> trainingBase = new LinkedList<>();
        List<Data> testBase = new LinkedList<>();

        DatabaseHandler.getTrainingTestBases(trainingBase, testBase);

        int numberOfClasses = DatabaseHandler.typesOfClasses.size();
        int numberOfInputs = trainingBase.get(0).getInput().length;

        Classifier classifier = new Classifier(numberOfClasses, numberOfInputs);

        classifier.fillMatrices(trainingBase);

        classifier.setTrainingBase(trainingBase);
        int trainingBaseClassificationError = 0;
        for (Data sample : trainingBase) {
            int classification = classifier.classify(sample);
            if (classification != sample.getOutput())
                trainingBaseClassificationError += 1;
        }

        classifier.setTrainingBase(testBase);
        int testBaseClassificationError = 0;
        for (Data sample : testBase) {
            int classification = classifier.classify(sample);
            if (classification != sample.getOutput())
                testBaseClassificationError += 1;
        }

        System.out.println("Erro de classificação base de treino: " + trainingBaseClassificationError);
        System.out.println("Erro de classificação base de teste: " + testBaseClassificationError);
    }
}