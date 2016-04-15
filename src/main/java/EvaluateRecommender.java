import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import java.io.File;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;

/**
 * Created by wanghao on 3/30/16.
 */
public class EvaluateRecommender {
    public static void main(String[] args) throws Exception {
        try {

            DataModel model = new FileDataModel(new File("output.csv"));
//            RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();

            double userPrec1 = 0, userRec1 = 0, userPrec2 = 0, userRec2 = 0;
            double itemPrec1 = 0, itemRec1 = 0, itemPrec2 = 0, itemRec2 = 0;
            for(int i = 0; i < 10; i++) {
                IRStatistics stats1 = buildUserRecommend1(model);
                IRStatistics stats2 = buildUserRecommend2(model);
                userPrec1 += stats1.getPrecision();
                userRec1 += stats1.getRecall();
                userPrec2 += stats2.getPrecision();
                userRec2 += stats2.getRecall();

                IRStatistics stats3 = buildItemRecommend1(model);
                IRStatistics stats4 = buildItemRecommend1(model);
                itemPrec1 += stats3.getPrecision();
                itemRec1 += stats3.getRecall();
                itemPrec2 += stats4.getPrecision();
                itemRec2 += stats4.getRecall();


            }
            userPrec1 = (userPrec1/10) * 100 ; userRec1 = (userRec1/10) * 100;
            userPrec2 = (userPrec2/10) * 100; userRec2 = (userRec2/10) * 100;

            System.out.println("USER BASED CF RECOMMENDATIONS: ");
            System.out.println("--------------------------------------------------------------------------");
            // on average, about P % of recommendations are good
            System.out.println("PearsonCorrelationSimilarity  PRECISION: On Avarege, about " + userPrec1 + "% of recommendations are good" );

            // %R of good recommenations are amont those recommended
            System.out.println("PearsonCorrelationSimilarity  RECALL: " + userRec1 + "% of good recommenations are among those recommended");
            // on average, about P % of recommendations are good
            System.out.println("LogLikelihoodSimilarity PRECISION: On Avarege, about " + userPrec2 + "% of recommendations are good" );

            // %R of good recommenations are amont those recommended
            System.out.println("LogLikelihoodSimilarity RECALL: " + userRec2 + "% of good recommenations are among those recommended");
            System.out.println("--------------------------------------------------------------------------");




            itemPrec1 = (itemPrec1/10) * 100 ; itemRec1 = (itemRec1/10) * 100;
            itemPrec2 = (itemPrec2/10) * 100; itemRec2 = (itemRec2/10) * 100;

            System.out.println("ITEM BASED CF RECOMMENDATIONS: ");
            System.out.println("--------------------------------------------------------------------------");
            // on average, about P % of recommendations are good
            System.out.println("PearsonCorrelationSimilarity PRECISION: On Avarege, about " + itemPrec1 + "% of recommendations are good" );

            // %R of good recommenations are amont those recommended
            System.out.println("PearsonCorrelationSimilarity RECALL: " + itemRec1 + "% of good recommenations are among those recommended");
            // on average, about P % of recommendations are good
            System.out.println("LogLikelihoodSimilarity PRECISION: On Avarege, about " + itemPrec2 + "% of recommendations are good" );

            // %R of good recommenations are amont those recommended
            System.out.println("LogLikelihoodSimilarity RECALL: " + itemRec2 + "% of good recommenations are among those recommended");
            System.out.println("--------------------------------------------------------------------------");


        }  catch (Exception e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        }

    }

    public static IRStatistics buildUserRecommend1(DataModel model) throws Exception{
        RecommenderBuilder builder1 = new MyUserRecommenderBuilder1();
        RecommenderIRStatsEvaluator evaluator1 = new GenericRecommenderIRStatsEvaluator();
        IRStatistics stats1 = evaluator1.evaluate(builder1,
                null,
                model,
                null,
                1,
                GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
                0.8);
        return stats1;
    }

    public static IRStatistics buildUserRecommend2(DataModel model) throws Exception{
        RecommenderBuilder builder2 = new MyUserRecommenderBuilder2();
        RecommenderIRStatsEvaluator evaluator2 = new GenericRecommenderIRStatsEvaluator();
        IRStatistics stats2 = evaluator2.evaluate(builder2,
                null,
                model,
                null,
                1,
                GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
                0.8);
        return stats2;
    }

    public static class MyUserRecommenderBuilder1 implements RecommenderBuilder {
        public Recommender buildRecommender(DataModel dataModel) throws TasteException {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.005, similarity, dataModel);
            return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
        }
    }

    public static class MyUserRecommenderBuilder2 implements RecommenderBuilder {
        public Recommender buildRecommender(DataModel dataModel) throws TasteException {
            UserSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.005, similarity, dataModel);
            return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
        }
    }

    public static IRStatistics buildItemRecommend1(DataModel model) throws Exception{
        RecommenderBuilder builder1 = new MyItemRecommenderBuilder1();
        RecommenderIRStatsEvaluator evaluator1 = new GenericRecommenderIRStatsEvaluator();
        IRStatistics stats1 = evaluator1.evaluate(builder1,
                null,
                model,
                null,
                1,
                GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
                0.8);
        return stats1;
    }

    public static IRStatistics buildItemRecommend2(DataModel model) throws Exception{
        RecommenderBuilder builder2 = new MyItemRecommenderBuilder2();
        RecommenderIRStatsEvaluator evaluator2 = new GenericRecommenderIRStatsEvaluator();
        IRStatistics stats2 = evaluator2.evaluate(builder2,
                null,
                model,
                null,
                1,
                GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
                0.8);
        return stats2;
    }



    public static class MyItemRecommenderBuilder1 implements RecommenderBuilder {
        public Recommender buildRecommender(DataModel dataModel) throws TasteException {
            ItemSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);

            return new GenericItemBasedRecommender(dataModel, similarity);
        }
    }
    public static class MyItemRecommenderBuilder2 implements RecommenderBuilder {
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                ItemSimilarity similarity = new LogLikelihoodSimilarity(dataModel);

                return new GenericItemBasedRecommender(dataModel, similarity);
            }
    }

}

