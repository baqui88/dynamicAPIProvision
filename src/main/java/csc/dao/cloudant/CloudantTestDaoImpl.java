package csc.dao.cloudant;

import com.cloudant.client.api.Database;

import com.cloudant.client.api.model.Response;
import csc.dao.TestDAO;
import csc.model.TestDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CloudantTestDaoImpl implements TestDAO {

    @Autowired(required = false)
    Database db;

    @Override
    public void createDocument(TestDocument document) {
        Response response = db.post(document);
        document.set_id(response.getId());
    }

    @Override
    public TestDocument findDocumentbyId(String id) {
        TestDocument document = db.find(TestDocument.class, id);
        return document;
    }

    @Override
    public List<TestDocument> findAllDocuments() {
        List<TestDocument> list;
        try {
            list = db.getAllDocsRequestBuilder()
                    .includeDocs(true)
                    .build()
                    .getResponse().
                            getDocsAs(TestDocument.class);
            return list;
        }
        // error
        catch (IOException ex) {
            return null;
        }
    }

    @Override
    public TestDocument updateDocument(TestDocument document) {
        Response response = db.update(document);
        if (response.getError() != null)
            return null;

        return db.find(TestDocument.class, document.get_id());
    }

    @Override
    public void deleteDocumentById(String id) {
        TestDocument document = db.find(TestDocument.class, id);
        db.remove(document);
    }

    @Override
    public boolean documentExists(String id) {
        TestDocument document = db.find(TestDocument.class, id);
        if (document == null)
            return false;

        return true;
    }

    @Override
    public void truncate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }
}
