package com.xiaojihua;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * 索引库的维护
 */
public class C04IndexRepoTest {

    private IndexWriter indexWriter;
    private IndexReader indexReader;

    @Before
    public void init() throws IOException {
        // 1、设置索引库的位置
        FSDirectory directory = FSDirectory.open(new File("I:\\indexRepo"));
        // 2、创建indexWriter
        // 2.1指定分词器，创建indexWriterConfig
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);
        // 2.2创建indexWriter
        indexWriter = new IndexWriter(directory, indexWriterConfig);

        // 3、创建indexReader
        indexReader = DirectoryReader.open(directory);


    }
    /**
     * 测试索引库的增加
     */
    @Test
    public void testAdd() throws IOException {
        // 创建document
        Document doc = new Document();
        // 向document中增加域field
        // 不同的document可以有不同的域，同一个document可以有相同的域
        doc.add(new TextField("filename","新添加的文档",Field.Store.YES));
        doc.add(new TextField("content","新添加的文档的内容",Field.Store.NO));
        doc.add(new TextField("content","新添加的文档的内容第二个content",Field.Store.NO));
        doc.add(new TextField("content","新添加的文档的内容需要能看到",Field.Store.YES));
        // 添加到索引库
        indexWriter.addDocument(doc);
        // 关闭资源，默认包含着commit
        indexWriter.close();
    }

    /**
     * 测试删除所有
     */
    @Test
    public void testDeleteAll() throws IOException {
        // 删除全部索引
        indexWriter.deleteAll();
        // 关闭资源，默认包含着commit
        indexWriter.close();
    }

    /**
     * 根据条件删除
     */
    @Test
    public void testDelteByQuery() throws IOException {
        TermQuery termQuery = new TermQuery(new Term("name", "apache"));
        // 根据查询条件删除
        indexWriter.deleteDocuments(termQuery);
        // 关闭资源，默认包含着commit
        indexWriter.close();
    }

    /**
     * 索引库的更改，先删除，后增加
     * 删除term查询出来的所有doc然后增加新的doc
     * 与其说是更改，不如说是替换
     * @throws IOException
     */
    @Test
    public void testUpdateIndex() throws IOException {
        // 创建document对象
        Document doc = new Document();
        // 向document中添加域，
        // 不同的document可以有不同的域，同一个document可以有相同的域。
        doc.add(new TextField("filename","要更新的文档",Field.Store.YES));
        doc.add(new TextField("content", "2013年11月18日 - Lucene 简介 Lucene 是一个基于 Java 的全文信息检索工具包,它不是一个完整的搜索应用程序,而是为你的应用程序提供索引和搜索功能。", Field.Store.YES));
        indexWriter.updateDocument(new Term("content","java"),doc);
        indexWriter.close();
    }

    /**
     * 测试的查询所有文档
     */
    @Test
    public void testMatchAllDocsQuery() throws IOException {
        // 创建查询条件
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        // 打印结果
        printResult(query);


    }

    /**
     * 通过Term查询，相当于精确查询
     * TermQuery不使用分析器所以建议匹配不分词的Field域查询，比如订单号、分类ID号等
     * 上面的意思是不对查询的内容进行分词，需要完全匹配比如查询为
     * new Term("content","lucene sdfds sdfsdf")
     * 则content需要完全匹配lucene sdfds sdfsdf才行
     */
    @Test
    public void testTermQuery() throws IOException {
        // 创建查询条件
        Query query = new TermQuery(new Term("content","lucene"));
        // 打印结果
        printResult(query);


    }

    /**
     * 通过数字范围进行查询
     * NumericRangeQuery是确定一个范围，用来查询数字来行的范围
     * 注意被查询的field必须为数字类型，否则查询不出数据来
     * @throws IOException
     */
    @Test
    public void testNumericRangeQuery() throws IOException {
        // 创建查询条件
        // 参数：
        // 1.域名
        // 2.最小值
        // 3.最大值
        // 4.是否包含最小值
        // 5.是否包含最大值
        // 注意size域必须为数字类型，这里是LongField
        Query query = NumericRangeQuery.newLongRange("size",1l,1000l,true,true);
        // 打印结果
        printResult(query);
    }

    /**
     * 组合查询
     * Occur.MUST：必须满足此条件，相当于and
     * Occur.SHOULD：应该满足，但是不满足也可以，相当于or
     * Occur.MUST_NOT：必须不满足。相当于not
     * @throws IOException
     */
    @Test
    public void testBooleanQuery() throws IOException {

        // 创建一个布尔查询对象
        BooleanQuery query = new BooleanQuery();
        // 创建查询条件
        TermQuery termQuery1 = new TermQuery(new Term("name", "apache"));
        TermQuery termQuery2 = new TermQuery(new Term("content", "apache"));
        // 组合查询条件
        query.add(termQuery1,BooleanClause.Occur.MUST);
        query.add(termQuery2,BooleanClause.Occur.SHOULD);
        // 执行查询
        printResult(query);
    }

    /**
     * 测试queryparser查询
     * queryparser有两个作用：
     * 1、可以对查询的内容按照分词器进行分词，比如查询的是 content:Lucene是java开发的
     *   那么会对“Lucene是java开发的”这句话进行分词，然后组合成相应的语法进行查询（content:lucene content:是 content:java content:开发 content:的）
     * 2、可以手写查询语法进行查询比如：Query query = queryParser.parse("-filename:apache content:apache");
     * @throws ParseException
     * @throws IOException
     */
    @Test
    public void testQueryParser() throws ParseException, IOException {
        //创建queryparser对象
        //第一个参数默认搜索的域
        //第二个参数就是分析器对象
        QueryParser queryParser = new QueryParser("content", new IKAnalyzer());
        Query query = queryParser.parse("Lucene是java开发的");
        // 不使用默认的域进行查询而根据自己写的语法进行查询
        //Query query = queryParser.parse("-filename:apache content:apache");
        // 执行查询
        printResult(query);
    }

    /**
     * 根据多个域进行分析查询
     * @throws ParseException
     * @throws IOException
     */
    @Test
    public void testMulitFieldQueryParserTest() throws ParseException, IOException {
        // 可以指定默认搜索的域是多个
        String[] fields = {"filename","content"};
        // 创建MulitFieldQueryParser对象
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields, new IKAnalyzer());
        Query query = multiFieldQueryParser.parse("java and apache");
        // 执行查询
        printResult(query);
    }

    /**
     * 测试加权值
     * 在C01IndexWriterTest的test1方法中，创建索引的时候将springmvc.txt的content域做了
     * 加权处理，所以在查询的时候如果符合查询条件，则此文件的排名是靠前的。
     * if(fileName.equals("springmvc.txt")){
     *                 contentField.setBoost(20f);
     *             }
     * 如果没有上面的设置，则按照原始打分来排序
     * @throws ParseException
     * @throws IOException
     */
    @Test
    public void testBoost() throws ParseException, IOException {
        String[] fields = {"content"};
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields, new IKAnalyzer());
        Query query = multiFieldQueryParser.parse("spring");
        printResult(query);
    }

    /**
     * 公共方法
     * @param query
     * @throws IOException
     */
    public void printResult(Query query) throws IOException {
        // 创建searcher
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        // 打印查询的语法
        System.out.println(query);
        // 执行查询
        TopDocs docs = indexSearcher.search(query,100);
        System.out.println("总记录数：" + docs.totalHits);
        System.out.println("=============================");
        ScoreDoc[] scoreDocs = docs.scoreDocs;
        for(ScoreDoc doc : scoreDocs){
            Document document = indexSearcher.doc(doc.doc);
            System.out.println("name:" + document.get("name"));
            System.out.println("size:" + document.get("size"));
            System.out.println("path:" + document.get("path"));
            System.out.println("=============================");
        }
        // 关闭资源
        indexReader.close();
    }
}
