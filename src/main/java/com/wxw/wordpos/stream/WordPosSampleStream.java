package com.wxw.wordpos.stream;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wxw.wordpos.parse.WordPosParseContext;
import com.wxw.wordpos.parse.WordPosParseNews;

import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;

/**
 * 读取文件流，并解析成要的格式返回
 * @author 王馨苇
 *
 */
public class WordPosSampleStream extends FilterObjectStream<String,WordPosSample>{

	private static Logger logger = Logger.getLogger(WordPosSampleStream.class.getName());
	/**
	 * 有参构造函数
	 * @param samples 读取的一行未解析的样本流
	 */
	public WordPosSampleStream(ObjectStream<String> samples) {
		super(samples);
		
	}

	/**
	 * 读取一行的内容，并解析成词，词性的格式
	 * @return 返回解析之后的结果
	 */	
	public WordPosSample read() throws IOException {
		
		String sentence = samples.read();
		WordPosParseContext context = new WordPosParseContext(new WordPosParseNews(),sentence);
		WordPosSample sample = null;
		if(sentence != null){
			if(sentence.compareTo("") != 0){
				try{
					//System.out.println(sentences);
					sample = context.parseSample();;
				}catch(Exception e){
					if (logger.isLoggable(Level.WARNING)) {
						
	                    logger.warning("Error during parsing, ignoring sentence: " + sentence);
	                }
					sample = new WordPosSample(new String[]{},new String[]{},new String[]{});
				}

				return sample;
			}else {
				sample = new WordPosSample(new String[]{},new String[]{},new String[]{});
				return sample;
			}
		}
		else{
			return null;
		}

	}

}
