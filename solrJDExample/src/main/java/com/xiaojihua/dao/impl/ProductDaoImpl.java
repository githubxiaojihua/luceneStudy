package com.xiaojihua.dao.impl;

import com.xiaojihua.dao.ProductDao;
import com.xiaojihua.pojo.PageBean;
import com.xiaojihua.pojo.Product;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private SolrServer solrServer;

    @Override
    public PageBean queryProduct(SolrQuery solrQuery) throws SolrServerException {
        PageBean page = new PageBean();
        QueryResponse queryResponse = solrServer.query(solrQuery);
        SolrDocumentList results = queryResponse.getResults();
        page.setRecordCount(results.getNumFound());
        List<Product> products = new ArrayList<>();
        for(SolrDocument document : results){
            Product product = new Product();
            product.setPid((String) document.get("id"));
            String productName = "";
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            List<String> list = highlighting.get(document.get("id")).get("product_name");
            if (null != list) {
                productName = list.get(0);
            } else {
                productName = (String) document.get("product_name");
            }
            product.setName(productName);
            product.setDescription(null);
            product.setPicture((String) document.get("product_picture"));
            product.setPrice((float) document.get("product_price"));

            products.add(product);

        }

        page.setProductList(products);
        return page;
    }
}
