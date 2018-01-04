package com.wxw.wordpos.error;

import java.io.OutputStream;
import java.io.PrintStream;

import com.wxw.wordpos.evaluate.WordPosEvaluateMonitor;
import com.wxw.wordpos.stream.WordPosSample;

/**
 * 打印错误信息类 
 * @author 王馨苇
 *
 */
public class WordPosErrorPrinter extends WordPosEvaluateMonitor{

	private PrintStream errOut;
	
	public WordPosErrorPrinter(OutputStream out){
		errOut = new PrintStream(out);
	}
	
	/**
	 * 样本和预测的不一样的时候进行输出
	 * @param reference 参考的样本
	 * @param predict 预测的结果
	 */
	@Override
	public void missclassified(WordPosSample reference, WordPosSample predict) {
		 errOut.println("样本的结果：");
		 errOut.print(reference.toSample());
		 errOut.println();
		 errOut.println("预测的结果：");
		 errOut.print(predict.toSample());
		 errOut.println();
	}

	
}
