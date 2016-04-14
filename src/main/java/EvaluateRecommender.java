import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import java.io.*;
import java.util.*;
/**
 * Created by jhan on 4/4/16.
 */
public class EvaluateRecommender {
    public static void main(String[] args) {
        double sum = 0;
        int count = 0;
        for(int i = 0; i < 10; i++) {
            try {
                DataModel model = new FileDataModel(new File("output.csv"));
                RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
                RecommenderBuilder builder = new MyRecommenderBuilder();
                double result = evaluator.evaluate(builder, null, model, 0.9, 1.0);
                System.out.println(result);
                sum += result;
                count++;

            }
            catch (Exception e) {
                System.out.println("There was an error.");e.printStackTrace();
            }
        }

        System.out.println("average of 10 times evaluation is " + sum / count);

    }


    static class MyRecommenderBuilder implements RecommenderBuilder {
        public Recommender buildRecommender(DataModel dataModel) throws TasteException {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel);
            return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
        }
    }
}
