package com.wxw.wordpos.evaluate;

import com.wxw.wordpos.stream.WordPosSample;

import opennlp.tools.util.eval.EvaluationMonitor;

/**
 * 评估监控器
 * @author 王馨苇
 *
 */
public class WordPosEvaluateMonitor implements EvaluationMonitor<WordPosSample>{

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	public void correctlyClassified(WordPosSample arg0, WordPosSample arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	public void missclassified(WordPosSample arg0, WordPosSample arg1) {
		// TODO Auto-generated method stub
		
	}

}
