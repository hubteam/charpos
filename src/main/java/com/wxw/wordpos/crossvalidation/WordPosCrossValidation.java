package com.wxw.wordpos.crossvalidation;

import java.io.IOException;
import java.util.HashSet;

import com.wxw.wordpos.evaluate.WordPosEvaluateMonitor;
import com.wxw.wordpos.evaluate.WordPosEvaluator;
import com.wxw.wordpos.evaluate.WordPosMeasure;
import com.wxw.wordpos.feature.WordPosContextGenerator;
import com.wxw.wordpos.model.WordPosME;
import com.wxw.wordpos.model.WordPosModel;
import com.wxw.wordpos.stream.WordPosSample;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

/**
 * 交叉验证
 * @author 王馨苇
 *
 */
public class WordPosCrossValidation {

	private final String languageCode;
	private final TrainingParameters params;
	private WordPosEvaluateMonitor[] monitor;
	
	/**
	 * 构造
	 * @param languageCode 编码格式
	 * @param params 训练的参数
	 * @param listeners 监听器
	 */
	public WordPosCrossValidation(String languageCode,TrainingParameters params,WordPosEvaluateMonitor... monitor){
		this.languageCode = languageCode;
		this.params = params;
		this.monitor = monitor;
	}
	
	/**
	 * 交叉验证十折评估
	 * @param sample 样本流
	 * @param nFolds 折数
	 * @param contextGenerator 上下文
	 * @throws IOException io异常
	 */
	public void evaluate(ObjectStream<WordPosSample> sample, int nFolds,
			WordPosContextGenerator contextGenerator) throws IOException{
		CrossValidationPartitioner<WordPosSample> partitioner = new CrossValidationPartitioner<WordPosSample>(sample, nFolds);
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			CrossValidationPartitioner.TrainingSampleStream<WordPosSample> trainingSampleStream = partitioner.next();
			//生成词典
			HashSet<String> dict = WordPosME.buildDictionary(trainingSampleStream);
			//训练模型
			trainingSampleStream.reset();
			WordPosModel model = WordPosME.train(languageCode, trainingSampleStream, params, contextGenerator);

			WordPosEvaluator evaluator = new WordPosEvaluator(new WordPosME(model, contextGenerator), monitor);
			WordPosMeasure measure = new WordPosMeasure(dict);
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
//		System.out.println(measure);
	}
}
