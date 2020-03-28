package com.xiaojihua;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * 读取索引
 */
public class C02IndexReaderTest {

    @Test
    public void test() throws IOException {
        // 1、指定索引库的路径
        FSDirectory directory = FSDirectory.open(new File("I:\\indexRepo"));
        // 2、创建indexReader对象
        DirectoryReader reader = DirectoryReader.open(directory);
        // 3、创建indexSearcher对象
        IndexSearcher searcher = new IndexSearcher(reader);
        // 4、创建查询 格式就是一个term   content:spring
        TermQuery termQuery = new TermQuery(new Term("content", "spring"));
        // 5、执行查询
        // 第一个参数是查询对象，第二个参数是查询结果返回的最大值
        TopDocs topDocs = searcher.search(termQuery, 10);
        // 获取查询结果的总条数
        System.out.println(topDocs.totalHits);
        // 遍历查询结果
        //topDocs.scoreDocs存储了document对象的id
        for(ScoreDoc doc : topDocs.scoreDocs){
            //scoreDoc.doc属性就是document对象的id
            //根据document的id找到document对象
            Document document = searcher.doc(doc.doc);
            System.out.println(document.get("name"));
            //System.out.println(document.get("content"));
            System.out.println(document.get("path"));
            System.out.println(document.get("size"));// byte
            System.out.println("======================================");
        }
        //关闭indexreader对象
        reader.close();

    }
}
