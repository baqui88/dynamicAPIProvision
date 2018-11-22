package csc.dao;

import csc.model.TestDocument;

import java.util.List;

public interface TestDAO {

    public void createDocument(TestDocument document);

    public TestDocument findDocumentbyId(String id);

    public List<TestDocument> findAllDocuments();

    public TestDocument updateDocument(TestDocument document);

    public void deleteDocumentById(String id);

    public boolean documentExists(String id);

    public void truncate();

    public long count();
}
