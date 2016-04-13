import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import java.io.File;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.SparkConf;
import scala.Tuple2;


/**
 * Created by jhan on 4/6/16.
 */
public class MatrixFactorBasedRecommendation {

    public static void main(String[] args) {



        try {
            //set SparkContext masterURL locally
            SparkConf conf = new SparkConf().setAppName("Project A MatrixFactorizationModel").setMaster("local");
            JavaSparkContext jsc = new JavaSparkContext(conf);

            // Load and parse the data
            String path = "output.csv";
            JavaRDD<String> data = jsc.textFile(path);
            JavaRDD<Rating> ratings = data.map(
                    new Function<String, Rating>() {
                        public Rating call(String s) {
                            String[] sarray = s.split(",");
                            return new Rating(Integer.parseInt(sarray[0]), Integer.parseInt(sarray[1]),
                                    Double.parseDouble(sarray[2]));
                        }
                    }
            );

            /*
            double[] splitRate = {0.9,0.1};
            JavaRDD<Rating>[] splitRDD = ratings.randomSplit(splitRate);
            JavaRDD<Rating> trainings = splitRDD[0];
            JavaRDD<Rating> testings = splitRDD[1];
            */
            // Build the recommendation model using ALS
            int rank = 10;
            int numIterations = 10;
            double lambda = 0.01;
            MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(ratings), rank, numIterations, lambda); // ratings

            //recommendations for a given user
            for(int i = 1; i <= 27; i++) {
                System.out.println(String.valueOf(i) + ":   " + model.predict(254,i));
            }
            for(Rating rateItem: model.recommendProducts(254,4)) {
                System.out.println("Top recommendations and rates: " + rateItem.toString());
            }


            // Evaluate the model on rating data
            JavaRDD<Tuple2<Object, Object>> userProducts = ratings.map(
                    new Function<Rating, Tuple2<Object, Object>>() {
                        public Tuple2<Object, Object> call(Rating r) {
                            return new Tuple2<Object, Object>(r.user(), r.product());
                        }
                    }
            );
            JavaPairRDD<Tuple2<Integer, Integer>, Double> predictions = JavaPairRDD.fromJavaRDD(
                    model.predict(JavaRDD.toRDD(userProducts)).toJavaRDD().map(
                            new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
                                public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r){
                                    return new Tuple2<Tuple2<Integer, Integer>, Double>(
                                            new Tuple2<Integer, Integer>(r.user(), r.product()), r.rating());
                                }
                            }
                    ));

            JavaRDD<Tuple2<Double, Double>> ratesAndPreds =
                    JavaPairRDD.fromJavaRDD(ratings.map(
                            new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
                                public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r){
                                    return new Tuple2<Tuple2<Integer, Integer>, Double>(
                                            new Tuple2<Integer, Integer>(r.user(), r.product()), r.rating());
                                }
                            }
                    )).join(predictions).values();
            double MSE = JavaDoubleRDD.fromRDD(ratesAndPreds.map(
                    new Function<Tuple2<Double, Double>, Object>() {
                        public Object call(Tuple2<Double, Double> pair) {
                            Double err = pair._1() - pair._2();
                            return err * err;
                        }
                    }
            ).rdd()).mean();
            System.out.println("Root Mean Squared Error = " + Math.sqrt(MSE));

            //stop SparkContext
            jsc.stop();

        } catch (Exception e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        }
    }
}