package csc.web.controllers;

import csc.dao.DBTYPE;
import csc.dao.DynamicDataSource;
import csc.model.TestDocument;
import csc.response.resolver.ItemResponseResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(method = RequestMethod.POST,
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
public class CreateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateController.class);
    private DBTYPE source_dbType = DBTYPE.MONGO;

    @Autowired
    private DynamicDataSource repo;
    @Autowired
    private ApplicationContext context;

    @PostMapping("${CONSTANT.base_uri}")
    @ResponseStatus(HttpStatus.CREATED)
    public String createDocument(ItemResponseResolver itemResolver, HttpServletRequest request,
                                 @RequestBody TestDocument item) {
        LOGGER.info("POST - Create controller ");
        doPost(request, item);

        itemResolver.setApplicationContext(context);
        itemResolver.putContextObject(item);
        return itemResolver.responseJSONString();
    }

    @PostMapping("${CONSTANT.item_uri}")
    @ResponseStatus(HttpStatus.CREATED)
    public String createDocumentWithId(ItemResponseResolver itemResolver, HttpServletRequest request,
                                       @RequestBody TestDocument item, @PathVariable String test_id)
    {
        LOGGER.info("POST - Create controller - Id :");
        item.set_id(test_id);
        doPost(request, item);

        itemResolver.setApplicationContext(context);
        itemResolver.putContextObject(item);
        return itemResolver.responseJSONString();
    }

    private void doPost(HttpServletRequest request, TestDocument document) {
        String userName = request.getHeader("userName");
        String profileId = request.getHeader("profileId");

        repo.setDatabaseType(source_dbType);
        document.setUserProfile(userName);
        repo.createDocument(document);

    }
}
