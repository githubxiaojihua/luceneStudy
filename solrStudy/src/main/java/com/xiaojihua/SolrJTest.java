package com.xiaojihua;

import org.apache.http.HttpServerConnection;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SolrJTest {

    /**
     * 测试添加文档索引
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void test() throws IOException, SolrServerException {
        // 和solr服务器创建连接
        // 参数：solr服务器的地址
        // 如果solr只有一个core那么下面的url中只写到solr就行，默认往第一个core中增加
        // 如果有多个core要像现在这样写，写那个core就是往哪个core中增加
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        // 创建一个文档对象
        SolrInputDocument document = new SolrInputDocument();
        // 向文档中添加域
        // 第一个参数：域的名称，域的名称必须是在schema.xml中定义的
        // 第二个参数：域的值
        document.addField("id","c0002");
        document.addField("name","solrj添加name2");
        document.addField("content","solrj添加的content2");
        // 把document对象添加到索引库中
        solrServer.add(document);
        // 提交修改
        solrServer.commit();

    }

    /**
     * 测试根据id删除文档
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void test2() throws IOException, SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        solrServer.deleteById("c0002");
        solrServer.commit();
    }

    /**
     * 测试根据查询语句来删除
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void test3() throws IOException, SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        solrServer.deleteByQuery("*:*");
        solrServer.commit();
    }


    /**
     * 测试简单查询
     * @throws SolrServerException
     */
    @Test
    public void test4() throws SolrServerException {
        // 创建与solr服务器的链接对象
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        // 创建一个query对象
        SolrQuery query = new SolrQuery();
        // 设置查询条件
        query.setQuery("*:*");
        // 执行查询
        QueryResponse response = solrServer.query(query);
        // 获取查询结果
        SolrDocumentList results = response.getResults();
        // 共查询到的商品总数
        System.out.println("共查询到的商品总数：" + results.getNumFound());
        // 遍历查询结果
        for(SolrDocument doc : results){
            System.out.println(doc.get("id"));
            System.out.println(doc.get("name"));
            System.out.println(doc.get("content"));
        }
    }

    /**
     * 测试复杂查询，
     * solr数据已经通过数据导入工具导入.
     * 模拟控制台的：主查询条件，过滤查询条件，起始位置和条数，默认查询域名，高亮显示获取，排序
     * 等复杂查询。
     */
    @Test
    public void test5() throws SolrServerException {
        // 1、创建链接
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        // 2、创建一个查询对象
        SolrQuery solrQuery = new SolrQuery();
        // 3、设置查询条件
        solrQuery.setQuery("花儿朵朵");
        //solrQuery.set("q","花儿朵朵");//设置主查询条件
        // 设置多条件查询，使用AND OR
        //solrQuery.set("q","product_keywords:浪漫樱花 AND product_keywords:韩国 OR product_catalog_name:与钟不同");
        // 4、设置过滤条件
        solrQuery.set("fq","product_catalog_name:时尚卫浴");
        solrQuery.set("fq","product_price:[0 TO 200]");
        // 5、设置起始位置和条数
        solrQuery.setStart(0);
        solrQuery.setRows(10);
        // 6、设置默认查询域名
        solrQuery.set("df","product_keywords");
        // 7、设置高亮
        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre("<span style=\"color:red\">");
        solrQuery.setHighlightSimplePost("</span>");
        solrQuery.addHighlightField("product_name");
        // 8、设置排序
        solrQuery.setSort("product_price", SolrQuery.ORDER.asc);//设置排序

        // 9、执行查询
        QueryResponse response = solrServer.query(solrQuery);
        SolrDocumentList results = response.getResults();

        System.out.println("总条数：" + results.getNumFound());

        // 10、遍历执行结果
        for (SolrDocument solrDocument : results) {
            System.out.println(solrDocument.get("id"));
            //判断是否有高亮内容
            /**
             * 数据格式为：
             * "highlighting": {
             *     "286": {
             *       "product_name": [
             *         "家天下<em>韩国</em>香皂 爱敬玫瑰保湿精油美容皂"
             *       ]
             *     },
             *     "499": {
             *       "product_name": [
             *         "家天下加厚<em>韩国</em>缤纷花语浴帘180*180cm"
             *       ]
             *     },
             *     "844": {
             *       "product_name": [
             *         "家天下<em>韩国</em>文具-纯纯心意彩胶套记事本笔记本3201-1"
             *       ]
             *     },
             */

            String productName = "";
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("product_name");
            if (null != list) {
                productName = list.get(0);
            } else {
                productName = (String) solrDocument.get("product_name");
            }

            System.out.println(productName);
            System.out.println(solrDocument.get("product_catalog_name"));
            System.out.println(solrDocument.get("product_price"));
            System.out.println(solrDocument.get("product_description"));
            System.out.println(solrDocument.get("product_picture"));
            System.out.println("-----------------------------");
        }

    }
}
