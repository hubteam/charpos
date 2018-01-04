package com.wxw.wordpos.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;

import com.wxw.wordpos.crossvalidation.WordPosCrossValidation;
import com.wxw.wordpos.error.WordPosErrorPrinter;
import com.wxw.wordpos.evaluate.WordPosEvaluator;
import com.wxw.wordpos.evaluate.WordPosMeasure;
import com.wxw.wordpos.feature.WordPosContextGenerator;
import com.wxw.wordpos.feature.WordPosContextGeneratorConf;
import com.wxw.wordpos.model.WordPosME;
import com.wxw.wordpos.model.WordPosModel;
import com.wxw.wordpos.stream.FileInputStreamFactory;
import com.wxw.wordpos.stream.WordPosSample;
import com.wxw.wordpos.stream.WordPosSampleStream;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * 运行类
 * @author 王馨苇
 *
 */
public class WordPosRun {

	private static String flag = "train";
	//静态内部类
	public static class Corpus{
			//文件名和编码
		public String name;
		public String encoding;
		public String trainFile;
		public String testFile;
		public String modelbinaryFile;
		public String modeltxtFile;
		public String errorFile;
	}
	
	private static String[] corpusName = {"pos"};
	
	public static void main(String[] args) throws IOException {
		String cmd = args[0];
		if(cmd.equals("-train")){
			flag = "train";
			runFeature();
		}else if(cmd.equals("-model")){
			flag = "model";
			runFeature();
		}else if(cmd.equals("-evaluate")){
			flag = "evaluate";
			runFeature();
		}else if(cmd.equals("-cross")){
			String corpus = args[1];
			crossValidation(corpus);
		}
	}

	/**
	 * 交叉验证
	 * @param corpus 语料的名称
	 * @throws IOException 
	 */
	private static void crossValidation(String corpusName) throws IOException {
		Properties config = new Properties();
		InputStream configStream = WordPosRun.class.getClassLoader().getResourceAsStream("com/wxw/wordsegpos/run/corpus.properties");
		config.load(configStream);
		Corpus[] corpora = getCorporaFromConf(config);
        //定位到某一语料
        Corpus corpus = getCorpus(corpora, corpusName);
        WordPosContextGenerator contextGen = getContextGenerator(config);
        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(new File(corpus.trainFile)), corpus.encoding);
        
        ObjectStream<WordPosSample> sampleStream = new WordPosSampleStream(lineStream);

        //默认参数
        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));

        //把刚才属性信息封装
        WordPosCrossValidation crossValidator = new WordPosCrossValidation("zh", params);

        System.out.println(contextGen);
        crossValidator.evaluate(sampleStream, 10, contextGen);
	}
	
	/**
	 * 根据语料名称获取某个语料
	 * @param corpora 语料内部类数组，包含了所有语料的信息
	 * @param corpusName 语料的名称
	 * @return
	 */
	private static Corpus getCorpus(Corpus[] corpora, String corpusName) {
		for (Corpus c : corpora) {
            if (c.name.equalsIgnoreCase(corpusName)) {
                return c;
            }
        }
        return null;
	}

	/**
	 * 根据配置文件配置的信息获取特征
	 * @throws IOException IO异常
	 */
	private static void runFeature() throws IOException {
		//配置参数
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));
	
		//加载语料文件
        Properties config = new Properties();
        InputStream configStream = WordPosRun.class.getClassLoader().getResourceAsStream("com/wxw/wordsegpos/run/corpus.properties");
        config.load(configStream);
        Corpus[] corpora = getCorporaFromConf(config);//获取语料

        WordPosContextGenerator contextGen = getContextGenerator(config);

        runFeatureOnCorporaByFlag(contextGen, corpora, params);
	}

	/**
	 * 根据命令行参数执行相应的操作
	 * @param contextGen 上下文特征生成器
	 * @param corpora 语料信息内部类对象数组
	 * @param params 训练模型的参数
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private static void runFeatureOnCorporaByFlag(WordPosContextGenerator contextGen, Corpus[] corpora,
			TrainingParameters params) throws FileNotFoundException, IOException {
		if(flag == "train" || flag.equals("train")){
			for (int i = 0; i < corpora.length; i++) {
				trainOnCorpus(contextGen,corpora[i],params);
			}
		}else if(flag == "model" || flag.equals("model")){
			for (int i = 0; i < corpora.length; i++) {
				modelOutOnCorpus(contextGen,corpora[i],params);
			}
		}else if(flag == "evaluate" || flag.equals("evaluate")){
			for (int i = 0; i < corpora.length; i++) {
				evaluateOnCorpus(contextGen,corpora[i],params);
			}
		}	
	}
	
	/**
	 * 读取模型，评估模型
	 * @param contextGen 上下文特征生成器
	 * @param corpus 语料对象
	 * @param params 训练模型的参数
	 * @throws UnsupportedOperationException 
	 * @throws IOException 
	 */	
	private static void evaluateOnCorpus(WordPosContextGenerator contextGen, Corpus corpus,
			TrainingParameters params) throws IOException {
		System.out.println("ContextGenerator: " + contextGen);

        System.out.println("Reading on " + corpus.name + "...");
        WordPosModel model = WordPosME.readModel(new File(corpus.modeltxtFile), params, contextGen, corpus.encoding);
  
        System.out.println("Building dictionary on " + corpus.name + "...");
        //根据训练语料生成词典【根据词典中的空格生成词】
        HashSet<String> dict = WordPosME.buildDictionary(new File(corpus.trainFile), corpus.encoding);
        
        WordPosME tagger = new WordPosME(model,contextGen);
       
        WordPosMeasure measure = new WordPosMeasure(dict);
        WordPosEvaluator evaluator = null;
        WordPosErrorPrinter printer = null;
        if(corpus.errorFile != null){
        	System.out.println("Print error to file " + corpus.errorFile);
        	printer = new WordPosErrorPrinter(new FileOutputStream(corpus.errorFile));    	
        	evaluator = new WordPosEvaluator(tagger,printer);
        }else{
        	evaluator = new WordPosEvaluator(tagger);
        }
        evaluator.setMeasure(measure);
        ObjectStream<String> linesStreamNoNull = new PlainTextByLineStream(new FileInputStreamFactory(new File(corpus.testFile)), corpus.encoding);
        ObjectStream<WordPosSample> sampleStreamNoNull = new WordPosSampleStream(linesStreamNoNull);
        evaluator.evaluate(sampleStreamNoNull);
        WordPosMeasure measureRes = evaluator.getMeasure();
        System.out.println("--------结果--------");
        System.out.println(measureRes);
	}

	/**
	 * 训练模型，输出模型文件
	 * @param contextGen 上下文特征生成器
	 * @param corpus 语料对象
	 * @param params 训练模型的参数
	 * @throws UnsupportedOperationException 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */	
	private static void modelOutOnCorpus(WordPosContextGenerator contextGen, Corpus corpus,
			TrainingParameters params) {
		System.out.println("ContextGenerator: " + contextGen);
        System.out.println("Training on " + corpus.name + "...");
        //训练模型
        WordPosME.train(new File(corpus.trainFile), new File(corpus.modelbinaryFile), new File(corpus.modeltxtFile), params, contextGen, corpus.encoding);
		
	}

	/**
	 * 训练模型
	 * @param contextGen 上下文特征生成器
	 * @param corpus 语料对象
	 * @param params 训练模型的参数
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */	
	private static void trainOnCorpus(WordPosContextGenerator contextGen, Corpus corpus, TrainingParameters params) throws FileNotFoundException, IOException {
		System.out.println("ContextGenerator: " + contextGen);
        System.out.println("Training on " + corpus.name + "...");
        //训练模型
        WordPosME.train(new File(corpus.trainFile), params, contextGen, corpus.encoding);
		
	}

	/**
	 * 得到生成特征的实例对象
	 * @param config 配置文件
	 * @return
	 */
	private static WordPosContextGenerator getContextGenerator(Properties config) {
		String featureClass = config.getProperty("feature.class");
		if(featureClass.equals("com.wxw.wordsegpos.feature.WordSegPosContextGeneratorConf")){
			//初始化需要哪些特征
        	return  new WordPosContextGeneratorConf(config);
		}else{
			return null;
		} 
	}

	private static Corpus[] getCorporaFromConf(Properties config) {
		Corpus[] corpuses = new Corpus[corpusName.length];
		for (int i = 0; i < corpuses.length; i++) {
			String name = corpusName[i];
			String encoding = config.getProperty(name + "." + "corpus.encoding");
			String trainFile = config.getProperty(name + "." + "corpus.train.file");
			String testFile = config.getProperty(name+"."+"corpus.test.file");
			String modelbinaryFile = config.getProperty(name + "." + "corpus.modelbinary.file");
			String modeltxtFile = config.getProperty(name + "." + "corpus.modeltxt.file");
			String errorFile = config.getProperty(name + "." + "corpus.error.file");
			Corpus corpus = new Corpus();
			corpus.name = name;
			corpus.encoding = encoding;
			corpus.trainFile = trainFile;
			corpus.testFile = testFile;
			corpus.modeltxtFile = modeltxtFile;
			corpus.modelbinaryFile = modelbinaryFile;
			corpus.errorFile = errorFile;
			corpuses[i] = corpus;			
		}
		return corpuses;
	}

}
