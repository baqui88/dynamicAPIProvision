package csc.web.controllers;

import csc.dao.DBTYPE;
import csc.dao.DynamicDataSource;
import csc.model.TestDocument;
import csc.response.resolver.ItemResponseResolver;
import csc.web.exception.DatabaseNotSupportedException;
import csc.web.exception.DocumentNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${CONSTANT.item_uri}/copy", method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class CopyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyController.class);
    private DBTYPE source_dbType = DBTYPE.CLOUDANT;

    @Autowired
    private DynamicDataSource repo;
    @Autowired
    private ApplicationContext context;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String copyTo(ItemResponseResolver itemResolver, @PathVariable String test_id,
                         @RequestParam String database) {

        LOGGER.info("POST - Copy Controller : testID = " + test_id);

        //if database doesn't exist
        if (!database.equalsIgnoreCase("mongo")) {
            LOGGER.error("Database " + database + " is not supported");
            throw new DatabaseNotSupportedException("Only support mongo");
        }

        repo.setDatabaseType(source_dbType);
        TestDocument sourceItem = repo.findDocumentbyId(test_id);
        if (sourceItem == null) {
            LOGGER.error("Document " + test_id + " not found");
            throw new DocumentNotFoundException(test_id);
        }

        // copy to target database
        repo.setDatabaseType(DBTYPE.MONGO);
        repo.createDocument(sourceItem);

        itemResolver.setApplicationContext(context);
        itemResolver.putContextObject(sourceItem);
        return itemResolver.responseJSONString();
    }

}
