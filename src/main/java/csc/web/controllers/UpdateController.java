package csc.web.controllers;

import csc.dao.DBTYPE;
import csc.dao.DynamicDataSource;
import csc.model.TestDocument;
import csc.response.resolver.ItemResponseResolver;
import csc.web.exception.DataIntegrityViolationException;
import csc.web.exception.DocumentNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "${CONSTANT.item_uri}", method = RequestMethod.PATCH,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class UpdateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateController.class);
    private DBTYPE source_dbType = DBTYPE.MONGO;

    @Autowired
    private DynamicDataSource repo;
    @Autowired
    private ApplicationContext context;

    @PatchMapping
    public String update(ItemResponseResolver itemResolver, @RequestBody Map<String, Object> payload,
                         @PathVariable String test_id) {
        LOGGER.info("PATCH - UPDATE Document " + test_id);

        repo.setDatabaseType(source_dbType);
        if (!repo.documentExists(test_id)) {
            LOGGER.error("Document " + test_id + " not found");
            throw new DocumentNotFoundException(test_id);
        }

        String text = (String) payload.get("text");
        TestDocument updated_item = repo.updateDocument(new TestDocument(test_id, null, text));
        // document is not updated
        if (updated_item == null) {
            LOGGER.error("Conflict when create document " + test_id);
            throw new DataIntegrityViolationException("Conflict when create document " + test_id);
        }

        itemResolver.setApplicationContext(context);
        itemResolver.putContextObject(updated_item);

        return itemResolver.responseJSONString();
    }

}
