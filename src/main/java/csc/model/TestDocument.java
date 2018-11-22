package csc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import csc.response.StaticInfoResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Document(collection = "tests")
public class TestDocument implements Serializable {

    private static final long serialVersionUID = 22064509L;
    private String _rev;

    @Id
    private String _id;
    @Field("create_user")
    private String userProfile;
    @Field("created_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdDateTime;
    @Field("text")
    private String text;
    @Field("resourceName")
    private String resourceName = "test";

    public TestDocument() {
        createdDateTime = new Date();
    }

    public TestDocument(String id, String create_user, String text) {
        this();
        this._id = id;
        this.userProfile = create_user;
        this.text = text;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String id) {
        this._id = id;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    private String getDateValue(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(this.createdDateTime);
    }

    public String getHref(){
        String s_href = StaticInfoResolver.getItemHref();
        return s_href.replace("/{test_id}","/"+this._id);
    }

    @Override
    public String toString() {

        return String.format("{_id : %s, " +
                        "userProfile : %s, " +
                        "createdTime : %s, " +
                        "text : %s, " +
                        "resourceName : %s}",
                _id, userProfile, getDateValue("yyyy-MM-dd"), text, resourceName);
    }

}