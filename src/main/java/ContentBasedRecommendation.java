/**
 * Created by jhan on 4/4/16.
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

public class ContentBasedRecommendation {
    public static void main(String[] args) {
        try{

            //Step 1:- Input CSV file (CSV file should be in userID(or movieID), genreID, preference) format
            DataModel dm = new FileDataModel(new File("inputData.csv"));
            //Step 2:- Create UserSimilarity or ItemSimilarity Matrix
            //UserSimilarity similarity = new UncenteredCosineSimilarity(dm);
            UserSimilarity similarity = new TanimotoCoefficientSimilarity(dm);
            //Step 3:- Create UserNeighbourHood object. (No Need to create ItemNeighbourHood object while creating
            //Item based Recommendation)
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dm);
            //Step 4:- Create object of UserBasedRecommender or ItemBasedRecommender
            UserBasedRecommender recommender = new GenericUserBasedRecommender(dm, neighborhood, similarity);
            //Step 5:- Call the Generated Recommender in previous step to getting
            //recommendation for particular user or Item

            int count = 0;
            //print top 3 recommendations for a given user
            for(long similarId: recommender.mostSimilarUserIDs(254,100)) {
                //10001 - 10028 are movie ids, other are user ids
                if(similarId >= 10001 && similarId <= 10038) {
                    System.out.println("Recommended movie is: " + similarId);
                    count++;
                }
                if(count == 3) {
                    break;
                }
            }
        }
        catch (Exception e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        }


    }
}
