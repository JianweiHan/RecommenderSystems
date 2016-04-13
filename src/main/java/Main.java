
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

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        try{


            //Step 1:- Input CSV file (CSV file should be in userID, itemID, preference) format

            DataModel dm = new FileDataModel(new File("output.csv"));

            //Step 2:- Create UserSimilarity or ItemSimilarity Matrix
            UserSimilarity similarity = new PearsonCorrelationSimilarity(dm);
            //Step 3:- Create UserNeighbourHood object. (No Need to create ItemNeighbourHood object while creating
            //Item based Recommendation)
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dm);
            //Step 4:- Create object of UserBasedRecommender or ItemBasedRecommender
            UserBasedRecommender recommender = new GenericUserBasedRecommender(dm, neighborhood, similarity);
            //Step 5:- Call the Generated Recommender in previous step to getting
            //recommendation for particular user or Item
            List<RecommendedItem> recommendations = recommender.recommend(254, 3);
            for (RecommendedItem recommendation : recommendations) {
                System.out.println(recommendation);
            }
            for(long similarId: recommender.mostSimilarUserIDs(3,1)) {
                System.out.println(similarId);
            }
        }
        catch (Exception e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        }



    }
}
