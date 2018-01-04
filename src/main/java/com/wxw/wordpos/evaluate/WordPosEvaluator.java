package com.wxw.wordpos.evaluate;

import com.wxw.wordpos.model.WordPosME;
import com.wxw.wordpos.stream.WordPosSample;

import opennlp.tools.util.eval.Evaluator;

/**
 * 评估器
 * @author 王馨苇
 *
 */
public class WordPosEvaluator extends Evaluator<WordPosSample>{

	private WordPosME tagger;
	private WordPosMeasure measure;
	
	/**
	 * 构造
	 * @param tagger 训练得到的模型
	 */
	public WordPosEvaluator(WordPosME tagger) {
		this.tagger = tagger;
	}
	
	/**
	 * 构造
	 * @param tagger 训练得到的模型
	 * @param evaluateMonitors 评估的监控管理器
	 */
	public WordPosEvaluator(WordPosME tagger,WordPosEvaluateMonitor... evaluateMonitors) {
		super(evaluateMonitors);
		this.tagger = tagger;
	}
	
	/**
	 * 设置评估指标的对象
	 * @param measure 评估指标计算的对象
	 */
	public void setMeasure(WordPosMeasure measure){
		this.measure = measure;
	}
	
	/**
	 * 得到评估的指标
	 * @return
	 */
	public WordPosMeasure getMeasure(){
		return this.measure;
	}
	
	/**
	 * 评估得到指标
	 * @param reference 参考的语料
	 */
	@Override
	protected WordPosSample processSample(WordPosSample reference) {
		String[] wordsRef = reference.getWords();
		String[] charactersRef = reference.getCharacters();
		String[] tagsRef = reference.getTagsAndPoses();
		String[] posesRef = WordPosSample.toPos(tagsRef);
		String[] tagsandposesPre = tagger.tag(charactersRef,wordsRef);
		String[] posesPre = WordPosSample.toPos(tagsandposesPre);
//		for (int i = 0; i < posesRef.length; i++) {
//			System.out.print(wordsRef[i]+"/"+posesRef[i]+"\t");
//		}
//		System.out.println();
//	
//		for (int i = 0; i < posesPre.length; i++) {
//			System.out.print(wordsRef[i]+"/"+posesPre[i]+"\t");
//		}
//		System.out.println();

		WordPosSample prediction = new WordPosSample(charactersRef,wordsRef,tagsandposesPre);
		measure.update(wordsRef,posesRef,posesPre);
		return prediction;
	}
}
