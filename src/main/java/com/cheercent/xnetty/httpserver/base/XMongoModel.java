package com.cheercent.xnetty.httpserver.base;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

public abstract class XMongoModel {

	private static Logger logger = LoggerFactory.getLogger(XMongoModel.class);

	public MongoCollection<Document> collection;

    protected abstract String collectionName();

    protected abstract String databaseName();
	
	public XMongoModel() {
		
		collection = XMongo.getDatabase(databaseName()).getCollection(collectionName());
	}
	
	public Document findById(String id) {
		ObjectId _idobj = null;
		try {
		    _idobj = new ObjectId(id);
		} catch (Exception e) {
		    return null;
		}
		Document myDoc = collection.find(Filters.eq("_id", _idobj)).first();
		return myDoc;
	}

    public int getCount() {
        int count = (int) collection.count();
        return count;
    }

    public MongoCursor<Document> find(Bson filter) {
        return collection.find(filter).iterator();
    }

    public MongoCursor<Document> findByPage(Bson filter, int pageNo, int pageSize) {
        Bson orderBy = new BasicDBObject("_id", 1);
        return collection.find(filter).sort(orderBy).skip((pageNo - 1) * pageSize).limit(pageSize).iterator();
    }
    
    public int deleteById(String id) {
        int count = 0;
        ObjectId _id = null;
        try {
            _id = new ObjectId(id);
        } catch (Exception e) {
            return 0;
        }
        Bson filter = Filters.eq("_id", _id);
        DeleteResult deleteResult = collection.deleteOne(filter);
        count = (int) deleteResult.getDeletedCount();
        return count;
    }

    public Document updateById(String id, Document newdoc) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Bson filter = Filters.eq("_id", _idobj);
        collection.updateOne(filter, new Document("$set", newdoc));
        return newdoc;
    }
	
    public void destroy() {
        collection.drop();
    }
}
