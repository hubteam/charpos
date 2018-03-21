package com.wxw.wordpos.parse;

import com.wxw.wordpos.stream.WordPosSample;

/**
 * 解析不同类型的文本语料的策略模式上下文类
 * @author 王馨苇
 *
 */
public class WordPosParseContext {

	private String sentence;
	private WordPosParseStrage strage;
	
	/**
	 * 构造函数
	 * @param strage 解析语料对应的策略类
	 * @param sentence 要解析的语句
	 */
	public WordPosParseContext(WordPosParseStrage strage,String sentence){
		this.sentence = sentence;
		this.strage = strage;
	}
	
	/**
	 * 解析语句
	 * @return 解析之后要的格式
	 */
	public WordPosSample parseSample(){
		return strage.parse(sentence);
	}
}
