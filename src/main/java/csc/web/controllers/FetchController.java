package csc.web.controllers;

import csc.dao.DBTYPE;
import csc.dao.DynamicDataSource;
import csc.model.TestDocument;
import csc.response.StaticInfoResolver;
import csc.response.resolver.CollectionResponseResolver;
import csc.response.resolver.ItemResponseResolver;
import csc.web.exception.DatabaseNotSupportedException;
import csc.web.exception.DocumentNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
public class FetchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchController.class);
    private DBTYPE source_dbType = DBTYPE.MONGO;

    @Autowired
    private DynamicDataSource repo;
    @Autowired
    private ApplicationContext context;

    @GetMapping("${CONSTANT.base_uri}")
    public String fetchAll(HttpServletRequest request, CollectionResponseResolver resolver) throws JSONException {
        LOGGER.info("GET - Fetch All");
        repo.setDatabaseType(source_dbType);
        List<TestDocument> collection = repo.findAllDocuments();

        resolver.setApplicationContext(context);
        resolver.putContextObject(collection);

        request.setAttribute(StaticInfoResolver.getBeanName(CollectionResponseResolver.class), resolver);
        return resolver.responseJSONString();
    }

    @GetMapping(value = "${CONSTANT.item_uri}")
    public String fetchById(ItemResponseResolver resolver, @PathVariable String test_id) {
        LOGGER.info("GET - Fetch Document By ID = " + test_id);

        repo.setDatabaseType(source_dbType);
        TestDocument item = repo.findDocumentbyId(test_id);
        if (item == null) {
            LOGGER.error("Document " + test_id + " not found");
            throw new DocumentNotFoundException(test_id);
        }

        resolver.setApplicationContext(context);
        resolver.putContextObject(item);
        return resolver.responseJSONString();
    }

    private void setDatabaseInRequest(HttpServletRequest request) {
        String database = (String) request.getAttribute("database");

        if (database != null) {
            try {
                DBTYPE temp = Enum.valueOf(DBTYPE.class, database.toUpperCase());
                source_dbType = temp;
            } catch (IllegalArgumentException ex) {
                throw new DatabaseNotSupportedException("Database '" + database + "' is not supported");
            }
        }
    }

}
