import java.util.Properties
import collection.JavaConverters._
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations

object SentimentAnalyzer {
  def getSentiment(text: String): Sentiment.Value = {
    val sentiments = getSentiments(text)
    val (_, sentiment) = sentiments.maxBy((sentence, _) => sentence.length)
    sentiment
  }

  private def getSentiments(text: String): List[(String, Sentiment.Value)] = {
    val props = new Properties()
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
    val annotation = new StanfordCoreNLP(props).process(text)

    annotation
      .get(classOf[CoreAnnotations.SentencesAnnotation])
      .asScala
      .toList
      .map(sentence =>
        (
          sentence,
          sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])
        )
      )
      .map { (sentence, tree) =>
        (
          sentence.toString,
          Sentiment.toSentiment(RNNCoreAnnotations.getPredictedClass(tree))
        )
      }
  }
}
