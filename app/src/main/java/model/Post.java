package model;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@ParseClassName("Post")
public class Post extends ParseObject implements Serializable {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_USERS_WHO_LIKED = "usersWhoLiked";

    public boolean likedByUser = false;
    public Integer likes = 0;

    public void setLikedByUser(boolean liked) {
        likedByUser = liked;
    }
    public void setLikeCount(Integer likes) {
        this.likes = likes;
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) { put(KEY_IMAGE, image); }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getTimestamp() { return getRelativeTimeAgo(getCreatedAt().toString()); }

    public JSONArray getUsersWhoLiked() { return getJSONArray(KEY_USERS_WHO_LIKED); }

    public void setUsersWhoLiked(ParseUser user) { put(KEY_USERS_WHO_LIKED, user); }

    public static class Query extends ParseQuery {
        public Query() {
            super(Post.class);
        }

        public Query getTop() {
            setLimit(5);
            addDescendingOrder(KEY_CREATED_AT);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }

        public Query profileQuery(ParseUser user) {
            setLimit(20);
            addDescendingOrder(KEY_CREATED_AT);
            whereEqualTo(KEY_USER, user);
            return this;
        }
    }

    // "Tue Jul 09 17:22:36 PDT 2019"
    public static String getRelativeTimeAgo(String rawDate) {
        String parseFormat = "EEE MMM dd HH:mm:ss zzz yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(parseFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
