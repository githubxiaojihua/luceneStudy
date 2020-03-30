package com.xiaojihua;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * 创建索引
 */
public class C01IndexWriterTest {

    @Test
    public void test1() throws IOException {
        // 1、指定索引库位置
        FSDirectory directory = FSDirectory.open(new File("I:\\indexRepo"));
        //索引库还可以存放到内存中
        //Directory directory = new RAMDirectory();

        // 2、指定分词器
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);
        // 3、创建索引写入对象
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        // 4、获取源文档
        File srcFile = new File("I:\\source");
        File[] files = srcFile.listFiles();
        // 每个文件创建一个Document对象
        for (File file : files) {
            Document doc = new Document();
            // 1、文件名称
            String fileName = file.getName();
            //第一个参数：域的名称
            //第二个参数：域的内容
            //第三个参数：是否存储
            Field nameField = new TextField("name", fileName, Field.Store.YES);
            doc.add(nameField);
            // 2、文件大小
            long fileSize = FileUtils.sizeOf(file);
            Field sizeField = new LongField("size", fileSize, Field.Store.YES);
            doc.add(sizeField);
            // 3、文件路径
            String filePath = file.getPath();
            Field pathField = new TextField("path", filePath, Field.Store.YES);
            doc.add(pathField);
            // 4、文件内容
            String fileContent = FileUtils.readFileToString(file);
            Field contentField = new TextField("content", fileContent, Field.Store.YES);

            // 在文件内容上设置加权值来影响最终的搜索排名
            if(fileName.equals("springmvc.txt")){
                contentField.setBoost(20f);
            }

            doc.add(contentField);

            // 4、把文档写入索引库
            indexWriter.addDocument(doc);
        }

        // 5、关闭资源
        indexWriter.close();
    }


}
