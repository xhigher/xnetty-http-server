package com.cheercent.xnetty.httpserver.base;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class XSearchModel {
	
	protected final static Logger logger = LoggerFactory.getLogger(XSearchModel.class);
	
	protected abstract String getIndexName();
	
	public void addDocument(String id, JSONObject data) {
		try {
			IndexRequest indexRequest = new IndexRequest(this.getIndexName());
			indexRequest.id(id).source(data.toJSONString(), XContentType.JSON);
			IndexResponse indexResponse = XElasticSearch.getClient().index(indexRequest, RequestOptions.DEFAULT);
			logger.info("addDocument: " + JSON.toJSONString(indexResponse));
			//return indexResponse.getResult()
		}catch(Exception e) {
			logger.error("addDocument.Exception", e);
		}
	}
	
	public void getDocument(String id) {
		try {
			GetRequest getRequest = new GetRequest(this.getIndexName(), id);
			GetResponse getResponse = XElasticSearch.getClient().get(getRequest, RequestOptions.DEFAULT);
			logger.info("getDocument: " + JSON.toJSONString(getResponse));
		}catch(Exception e) {
			logger.error("createIndex.Exception", e);
		}
	}
 
	public void updateDocument(String id, JSONObject data) {
		try {
			UpdateRequest request = new UpdateRequest(this.getIndexName(), id);
			request.doc(data.toJSONString(), XContentType.JSON);
			UpdateResponse updateResponse = XElasticSearch.getClient().update(request, RequestOptions.DEFAULT);
			logger.info("updateDocument: " + JSON.toJSONString(updateResponse));
		}catch(Exception e) {
			logger.error("updateDocument.Exception", e);
		}
	}
 
	public void deleteDocument(String id) {
		try {
			DeleteRequest deleteRequest = new DeleteRequest(this.getIndexName(), id);
			DeleteResponse response = XElasticSearch.getClient().delete(deleteRequest, RequestOptions.DEFAULT);
			logger.info("deleteDocument: " + JSON.toJSONString(response));
		}catch(Exception e) {
			logger.error("deleteDocument.Exception", e);
		}
	}
	
	public void bulkAddDocument(String idKey, JSONArray data) {
		try {
			BulkRequest bulkAddRequest = new BulkRequest();
			JSONObject item = null;
			for (int i = 0; i < data.size(); i++) {
				item = data.getJSONObject(i);
				IndexRequest indexRequest = new IndexRequest(this.getIndexName());
				indexRequest.id(item.getString(idKey)).source(item.toJSONString(), XContentType.JSON);
				bulkAddRequest.add(indexRequest);
			}
			BulkResponse bulkAddResponse  = XElasticSearch.getClient().bulk(bulkAddRequest, RequestOptions.DEFAULT);
			logger.info("bulkAddDocument: " + JSON.toJSONString(bulkAddResponse));
			//return indexResponse.getResult()
		}catch(Exception e) {
			logger.error("bulkAddDocument.Exception", e);
		}
	}
	
	public void bulkUpdateDocument(String idKey, JSONArray data) {
		try {
			BulkRequest bulkUpdateRequest = new BulkRequest();
			JSONObject item = null;
			for (int i = 0; i < data.size(); i++) {
				item = data.getJSONObject(i);
				UpdateRequest updateRequest = new UpdateRequest(this.getIndexName(), item.getString(idKey));
				updateRequest.doc(item.toJSONString(), XContentType.JSON);
				bulkUpdateRequest.add(updateRequest);
			}
			BulkResponse bulkUpdateResponse  = XElasticSearch.getClient().bulk(bulkUpdateRequest, RequestOptions.DEFAULT);
			logger.info("bulkUpdateDocument: " + JSON.toJSONString(bulkUpdateResponse));
			//return indexResponse.getResult()
		}catch(Exception e) {
			logger.error("bulkUpdateDocument.Exception", e);
		}
	}
	
	public void bulkDeleteDocument(String idKey, JSONArray data) {
		try {
			BulkRequest bulkDeleteRequest = new BulkRequest();
			JSONObject item = null;
			for (int i = 0; i < data.size(); i++) {
				item = data.getJSONObject(i);
				DeleteRequest deleteRequest = new DeleteRequest(this.getIndexName(), item.getString(idKey));
				bulkDeleteRequest.add(deleteRequest);
			}
			BulkResponse bulkDeleteResponse  = XElasticSearch.getClient().bulk(bulkDeleteRequest, RequestOptions.DEFAULT);
			logger.info("bulkDeleteDocument: " + JSON.toJSONString(bulkDeleteResponse));
			//return indexResponse.getResult()
		}catch(Exception e) {
			logger.error("bulkDeleteDocument.Exception", e);
		}
	}
	
	protected JSONArray search(BoolQueryBuilder boolBuilder, String[] resultFields, String orderName, SortOrder orderType, int pagenum, int pagesize, boolean onlyId) {
		try {
			int fromIndex = (pagenum >=1 ? pagenum-1 : pagenum) * pagesize;
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(boolBuilder);
			sourceBuilder.from(fromIndex);
			sourceBuilder.size(pagesize);
			if(orderName != null) {
				sourceBuilder.sort(orderName, orderType==null?SortOrder.ASC:orderType);
			}
			sourceBuilder.fetchSource(resultFields, null);
			SearchRequest searchRequest = new SearchRequest(this.getIndexName());
			searchRequest.source(sourceBuilder);
			
			logger.info("search: " + searchRequest.toString());
			SearchResponse response = XElasticSearch.getClient().search(searchRequest, RequestOptions.DEFAULT);
			JSONArray resutlData = null;
			if(response != null) {
				logger.info("search: " + JSON.toJSONString(response));
				resutlData = new JSONArray();
				SearchHit[] searchHits = response.getHits().getHits();
				if(searchHits.length > 0) {
					for(int i=0; i<searchHits.length; i++) {
						if(onlyId) {
							resutlData.add(searchHits[i].getId());
						}else {
							resutlData.add(new JSONObject(searchHits[i].getSourceAsMap()));
						}
					}
				}
			}
			return resutlData;
		}catch(Exception e) {
			logger.error("search.Exception", e);
		}
		return null;
	}

}
