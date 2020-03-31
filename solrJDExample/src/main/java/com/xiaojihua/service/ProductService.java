package com.xiaojihua.service;

import com.xiaojihua.pojo.PageBean;
import org.apache.solr.client.solrj.SolrServerException;

public interface ProductService {
    public PageBean queryProduct(int page, String queryString, String catalog_name, String price, String sort) throws SolrServerException;
}
