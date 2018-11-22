package csc.dao;

import csc.dao.cloudant.CloudantTestDaoImpl;
import csc.dao.mongo.MongoTestDaoImpl;
import csc.model.TestDocument;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Scope(value="request", proxyMode= ScopedProxyMode.TARGET_CLASS)
public class DynamicDataSource implements TestDAO , InitializingBean {
    private DBTYPE dbType = DBTYPE.CLOUDANT; // current database
    private TestDAO dao;                     // current DAO
    private Map<DBTYPE, TestDAO> mapDatasource = new HashMap<DBTYPE, TestDAO>();

    @Autowired
    private CloudantTestDaoImpl cloudantDao;
    @Autowired
    private MongoTestDaoImpl mongoDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            mapDatasource.put(DBTYPE.CLOUDANT, cloudantDao);
            mapDatasource.put(DBTYPE.MONGO, mongoDao);
            // set default database
            dao = mapDatasource.get(dbType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDatabaseType(DBTYPE db_type) {
        this.dbType = db_type;
        this.dao = mapDatasource.get(db_type);
    }

    public String getCurrentDatabaseName() {
        return dbType.name();
    }

    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void createDocument(TestDocument document) {
        dao.createDocument(document);
    }

    @Override
    public TestDocument findDocumentbyId(String id) {
        return dao.findDocumentbyId(id);
    }

    @Override
    public List<TestDocument> findAllDocuments() {
        return dao.findAllDocuments();
    }

    @Override
    public TestDocument updateDocument(TestDocument document) {
        return dao.updateDocument(document);
    }


    @Override
    public boolean documentExists(String id) {
        return dao.documentExists(id);
    }

    @Override
    public void deleteDocumentById(String id) {
        dao.deleteDocumentById(id);
    }

    @Override
    public void truncate() {
        dao.truncate();
    }

    @Override
    public long count() {
        return dao.count();
    }

}