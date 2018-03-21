package com.wxw.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.wxw.wordpos.feature.WordPosContextGenerator;
import com.wxw.wordpos.feature.WordPosContextGeneratorConf;
import com.wxw.wordpos.model.WordPos;
import com.wxw.wordpos.model.WordPosME;
import com.wxw.wordpos.model.WordPosModel;

public class Test {

	@org.junit.Test
	public void test() throws IOException{
		Properties config = new Properties();
		InputStream configStream = Test.class.getClassLoader().getResourceAsStream("com/wxw/wordsegpos/run/corpus.properties");
		config.load(configStream);
		WordPosModel model = new WordPosModel(new File(config.getProperty("pos.corpus.modelbinary.file")));
		WordPosContextGenerator contextGenerator = new WordPosContextGeneratorConf(config);
		WordPos postagger = new WordPosME(model, contextGenerator);
		String[] str = {"北京","举行","新年","音乐会"};
		String[] res = postagger.wordpos(str);
		for (int i = 0; i < res.length; i++) {
			System.out.println(res[i]);
		}
	}
}
