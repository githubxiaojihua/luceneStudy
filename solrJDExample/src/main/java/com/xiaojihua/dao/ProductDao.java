package com.xiaojihua.dao;

import com.xiaojihua.pojo.PageBean;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;

public interface ProductDao {
    PageBean queryProduct(SolrQuery solrQuery) throws SolrServerException;
}
