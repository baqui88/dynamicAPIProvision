package csc.dao.mongo;

import com.mongodb.client.result.UpdateResult;
import csc.dao.TestDAO;
import csc.model.TestDocument;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Created by qui on 2018/11/13.
 */
@Service
public class MongoTestDaoImpl implements TestDAO {

    private MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void createDocument(TestDocument document) {
        mongoTemplate.save(document);
    }

    @Override
    public TestDocument findDocumentbyId(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        TestDocument document = mongoTemplate.findOne(query, TestDocument.class);
        return document;
    }

    @Override
    public List<TestDocument> findAllDocuments() {
        List<TestDocument> list = mongoTemplate.findAll(TestDocument.class);
        return list;
    }

    @Override
    public TestDocument updateDocument(TestDocument document) {
        Query query = new Query(Criteria.where("_id").is(document.get_id()));
        Update update = new Update();
        if (document.getText() != null) {
            update = update.set("text", document.getText());
        }

        UpdateResult result = mongoTemplate.updateFirst(query, update, TestDocument.class);
        // error
        if (!result.wasAcknowledged())
            return null;

        return findDocumentbyId(document.get_id());
    }

    @Override
    public void deleteDocumentById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, TestDocument.class);
    }

    @Override
    public boolean documentExists(String id) {
        TestDocument document = findDocumentbyId(id);
        if (document != null)
            return true;
        else {
            return false;
        }
    }

    // reset collection
    @Override
    public void truncate() {
        mongoTemplate.remove(new Query(), TestDocument.class);
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), TestDocument.class);
    }
}