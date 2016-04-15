/**
 * Created by jhan on 4/7/16.
 */

import java.io.*;
import java.util.*;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.*;
import org.apache.mahout.cf.taste.impl.neighborhood.*;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.*;
import org.apache.mahout.cf.taste.neighborhood.*;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.similarity.*;

public class HybridRecommendation {
    public static void main(String[] args) {
        try{
            //1.Establish similarity and neighborhood from content-based user profile, to find out similar users
            DataModel dm = new FileDataModel(new File("ContentBasedDataUserProfile.csv"));
            UserSimilarity similarity = new TanimotoCoefficientSimilarity(dm);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dm);

            //2.make the prediction with User-Based Recommendation, but neighborhood from content-based
            DataModel rateModel = new FileDataModel(new File("output.csv"));
            UserBasedRecommender recommender = new GenericUserBasedRecommender(rateModel, neighborhood, similarity);
            //Call the Generated Recommender in previous step to getting
            //recommendation for particular user or Item
            List<RecommendedItem> recommendations = recommender.recommend(254, 3);
            for (RecommendedItem recommendation : recommendations) {
                System.out.println(recommendation);
            }
        }
        catch (Exception e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        }

    }

}
