package com.xiaojihua.service.impl;

import com.xiaojihua.dao.ProductDao;
import com.xiaojihua.pojo.PageBean;
import com.xiaojihua.service.ProductService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl implements ProductService {

    private static int pageNum = 60;// 默认的每页显示数量

    @Autowired
    private ProductDao productDao;

    @Override
    public PageBean queryProduct(int page, String queryString, String catalog_name, String price, String sort) throws SolrServerException {
        SolrQuery query = new SolrQuery();
        // 设置查询条件
        if(!StringUtils.isEmpty(queryString)){
            query.setQuery(queryString);
        }else{
            query.setQuery("*:*");
        }

        // 设置过滤条件
        if(!StringUtils.isEmpty(catalog_name)){
            query.set("fq","product_catalog_name:"+catalog_name);
        }

        if(!StringUtils.isEmpty(price)){
            String[] split = price.split("-");
            query.set("fq","product_price:["+split[0]+" TO "+split[1]+"]");
        }

        // 设置起始位置
        query.setStart((page - 1) * pageNum);
        // 设置条数
        query.setRows(pageNum);
        // 设置默认查询的域名
        query.set("df",  "product_name");

        // 设置高亮
        query.setHighlight(true);
        query.setHighlightSimplePre("<span style=\"color:red\">");
        query.setHighlightSimplePost("</span>");
        query.addHighlightField("product_name");

        // 设置排序
        if(sort.equals("1")){
            query.setSort("product_price", SolrQuery.ORDER.asc);//设置排序
        }else{
            query.setSort("product_price", SolrQuery.ORDER.desc);//设置排序
        }

        PageBean pageBean = productDao.queryProduct(query);

        // 设置pageBean剩下的内容
        pageBean.setCurPage(page);
        Long pageCount = pageBean.getRecordCount()/pageNum;  //计算总页数   总页数=总数/每页显示条数
        if(pageBean.getRecordCount()%pageNum>0){         //判断是否有余数，如果有余数 总页数需要加1
            pageCount++;
        }
        pageBean.setPageCount(pageCount.intValue());
        return pageBean;
    }
}
