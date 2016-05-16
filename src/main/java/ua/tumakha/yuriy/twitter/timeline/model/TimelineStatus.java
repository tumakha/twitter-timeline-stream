package ua.tumakha.yuriy.twitter.timeline.model;

import com.twitter.Autolink;
import twitter4j.HashtagEntity;
import twitter4j.Status;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Yuriy Tumakha
 */
public class TimelineStatus {

    private long id;

    private long userId;

    private String text;

    private Set<String> hashtags;

    private Date createdAt;

    public static TimelineStatus valueOf(Status status) {
        TimelineStatus ts = new TimelineStatus();
        ts.setId(status.getId());
        ts.setUserId(status.getUser() != null ? status.getUser().getId() : null);
        Autolink linker = new Autolink();
        ts.setText(linker.autoLink(status.getText()));
        ts.setCreatedAt(status.getCreatedAt());
        HashtagEntity[] hashtagEntities = status.getHashtagEntities();
        if (hashtagEntities != null) {
            Set<String> textHashtags = new LinkedHashSet<>();
            for (int i = 0; i < hashtagEntities.length; i++) {
                textHashtags.add(hashtagEntities[i].getText());
            }
            ts.setHashtags(textHashtags);
        }
        return ts;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TimelineStatus{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", text='").append(text).append('\'');
        sb.append(", hashtags=").append(hashtags);
        sb.append(", createdAt='").append(createdAt).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
