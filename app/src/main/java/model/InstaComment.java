package model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("InstaComment")
public class InstaComment extends ParseObject {
    public static final String KEY_TEXT = "text";
    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";
    public static final String KEY_CREATED_AT = "createdAt";

    public InstaComment() { }

    public String getText() { return getString(KEY_TEXT); }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public ParseObject getPost() {
        return getParseObject(KEY_POST);
    }

    public void setPost(Post post) { put(KEY_POST, post); }

    public String getTimestamp() { return Post.getRelativeTimeAgo(getCreatedAt().toString()); }

    public static class Query extends ParseQuery {
        public Query() {
            super(InstaComment.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query forPost(Post post) {
            addDescendingOrder(KEY_CREATED_AT);
            whereEqualTo(KEY_POST, post);
            return this;
        }

    }
}
