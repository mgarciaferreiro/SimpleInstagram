package model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";
    public static final String KEY_CREATED_AT = "createdAt";

    public Like() { }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public ParseObject getPost() {
        return getParseObject(KEY_POST);
    }

    public void setPost(Post post) { put(KEY_POST, post); }

    public static class Query extends ParseQuery {
        public Query() {
            super(Like.class);
        }

        public Query forPost(Post post) {
            whereEqualTo(KEY_POST, post);
            return this;
        }

        public Query forUser(ParseUser user) {
            addDescendingOrder(KEY_CREATED_AT);
            whereEqualTo(KEY_USER, user);
            return this;
        }

    }
}
