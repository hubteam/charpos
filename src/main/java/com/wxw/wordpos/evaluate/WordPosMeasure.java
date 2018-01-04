package com.wxw.wordpos.evaluate;

import java.util.HashSet;

/**
 * 计算指标
 * @author 王馨苇
 *
 */
public class WordPosMeasure {

	/**
     * |selected| = true positives + false positives <br>
     * 预测的样本数
     */
    private long selected;

    /**
     * |target| = true positives + false negatives <br>
     * 参考的样本数
     */
    private long target;

    /**
     * 预测正确的个数
     */
    private long truePositive;
    
    private long targetIV;
    private long targetOOV;
    private long truepositiveIV;
    private long truepositiveOOV;
    
    private HashSet<String> dictionary;
    
    public WordPosMeasure(){
    	
    }
    
    /**
     * 有参构造
     * @param dictionary 从训练语料生成的词典
     */
    public WordPosMeasure(HashSet<String> dictionary){
    	this.dictionary = dictionary;
    }
    
    /**
     * 更新计数变量
     * @param words 词语【这里的词语既是参考的，也是预测的】
     * @param posesRef 标准的词性标注
     * @param posesPre 预测的词性标注
     */
	public void update(String[] words, String[] posesRef, String[] posesPre) {
		boolean isIV = true;
		for (int i = 0; i < posesRef.length; i++) {
			if(this.dictionary != null){
				isIV = this.dictionary.contains(words[i]);
				if(isIV){
					targetIV++;
				}else{
					targetOOV++;
				}
			}
			
			if(posesRef[i].equals(posesPre[i])){
				truePositive++;
				if(this.dictionary != null){
					if(isIV){
						truepositiveIV++;
					}else{
						truepositiveOOV++;
					}
				}
				
			}
		}
		target += posesRef.length;
		selected += posesPre.length;	
	}
	
	/**
	 * 准确率
	 * @return
	 */
	public double getPrecisionScore() {
		return selected > 0 ? (double) truePositive / (double) selected : 0;
	}

	/**
	 * 召回率
	 * @return
	 */
	public double getRecallScore() { 
		return target > 0 ? (double) truePositive / (double) target : 0;
	}
	
	/**
	 * F值
	 * @return
	 */
	public double getMeasure() {

        if (getPrecisionScore() + getRecallScore() > 0) {
            return 2 * (getPrecisionScore() * getRecallScore())
                    / (getPrecisionScore() + getRecallScore());
        } else {
            // cannot divide by zero, return error code
            return -1;
        }
    }
	
	/**
	 * 登录词的召回率
	 * @return
	 */
	public double getRecallScoreIV(){
		return targetIV > 0 ? (double)truepositiveIV / (double)targetIV : 0;
	}
	
	/**
	 * 未登录词的召回率
	 * @return
	 */
	public double getRecallScoreOOV(){
		return targetOOV > 0 ? (double)truepositiveOOV / (double)targetOOV : 0;
	}
	
	/**
	 * 打印的格式
	 */
	@Override
    public String toString() {
        return "Precision: " + Double.toString(getPrecisionScore()) + "\n"
                + "Recall: " + Double.toString(getRecallScore()) + "\n" 
        		+ "F-Measure: "+ Double.toString(getMeasure()) + "\n"
        		+ "RIV: " + Double.toString(getRecallScoreIV()) + "\n"
        		+ "ROOV: " + Double.toString(getRecallScoreOOV());
    }

}
