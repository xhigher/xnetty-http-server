package com.cheercent.xnetty.httpserver.model.search;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.alibaba.fastjson.JSONArray;
import com.cheercent.xnetty.httpserver.base.XSearchModel;


public class UserSearchModel extends XSearchModel {

	public static final String indexName = "xnetty_user";
	
	@Override
	protected String getIndexName() {
		return indexName;
	}
	
	public JSONArray getList(String keywords, int pagenum, int pagesize) {
		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
		boolBuilder.should(QueryBuilders.matchPhraseQuery("nickname", keywords));
		boolBuilder.should(QueryBuilders.matchPhraseQuery("nickname2", keywords));
		
		JSONArray data = this.search(boolBuilder, null, "level", SortOrder.DESC, pagenum, pagesize, false);
		return data;
	}
}
