package com.xiaojihua;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;

public class AnalyzerTest {

    /**
     * 测试分词器，获得分词器的处理结果
     */
    @Test
    public void test1() throws IOException {
        // 创建一个标准分析器对象
        StandardAnalyzer analyzer = new StandardAnalyzer();
        // 获得tokenStream对象
        // 第一个参数：域名，可以随便给一个
        // 第二个参数：要分析的文本内容
        TokenStream tokenStream = analyzer.tokenStream("test", "The Spring Framework provides a comprehensive programming and configuration model.");
        // 添加一个引用，可以获得每个关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //将指针调整到列表的头部
        tokenStream.reset();
        //遍历关键词列表，通过incrementToken方法判断列表是否结束
        while(tokenStream.incrementToken()){
            //取关键词
            System.out.println(charTermAttribute);
        }
        tokenStream.close();
    }

    /**
     * 测试中文分词器 IKAnalyzer
     * 包括三个关键配置文件
     * IKAnalyzer.cfg.xml，配置文件用于指定停止字典和扩展字典
     * stopword.dic停止字典，里面的单词不进行索引也不进行存储，比如：反政府、邪教等，忽略做这些词，
     * 但是源文档中是否包含不做要求
     * ext.dic扩展字典，里面的此可以作为扩展词，让分词器进行整体分词，否则的话可能分出来的就是一个字一个字的
     * 比如：传智播客这样的公司名称。
     *
     */
    @Test
    public void test2() throws IOException {
// 创建一个标准分析器对象
        IKAnalyzer analyzer = new IKAnalyzer();
        // 获得tokenStream对象
        // 第一个参数：域名，可以随便给一个
        // 第二个参数：要分析的文本内容
        TokenStream tokenStream = analyzer.tokenStream("test", "apache 全文检索是将整本书java、整篇文章中的任意内容信息查找出来的检索，java。传智播客");
        // 添加一个引用，可以获得每个关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //将指针调整到列表的头部
        tokenStream.reset();
        //遍历关键词列表，通过incrementToken方法判断列表是否结束
        while(tokenStream.incrementToken()){
            //取关键词
            System.out.println(charTermAttribute);
        }
        tokenStream.close();
    }
}
