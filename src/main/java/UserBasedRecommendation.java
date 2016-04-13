/**
 * Created by wanghao on 3/29/16.
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

public class UserBasedRecommendation {

    public static void main(String[] args) {
        try{

            //Step 1:- Input CSV file (CSV file should be in userID, itemID, preference) format

            DataModel datamodel = new FileDataModel(new File("output.csv"));

            //Step 2:- Create UserSimilarity or ItemSimilarity Matrix
            UserSimilarity similarity = new PearsonCorrelationSimilarity(datamodel);

            //Step 3:- Create UserNeighbourHood object. (No Need to create ItemNeighbourHood object while creating
            //Item based Recommendation)
            UserNeighborhood nbhood = new ThresholdUserNeighborhood(0.2,similarity,datamodel);
            //Step 4:- Create object of UserBasedRecommender or ItemBasedRecommender
            UserBasedRecommender recommender = new GenericUserBasedRecommender(datamodel,nbhood,similarity);
            //Step 5:- Call the Generated Recommender in previous step to getting
            //recommendation for partiorg.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilaritycular user or Item

            List<RecommendedItem> recommendations  = recommender.recommend(254,3);
            for (RecommendedItem recommend: recommendations) {
                System.out.println(recommend);
            }

        }
        catch (Exception e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        }

    }
}