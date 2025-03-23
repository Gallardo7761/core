package net.miarma.core.database.entities.miarmacraft;

import java.time.LocalDateTime;

import net.miarma.core.common.Table;

@Table("Suggestions")
public class SuggestionEntity {
	private int suggestionId;
    private int userId;
    private String title;
    private String tags;
    private String content;
    private int state;
    private LocalDateTime createdAt;

    public SuggestionEntity(int suggestionId, int userId, String title, String tags, String content, int state, LocalDateTime createdAt) {
        this.suggestionId = suggestionId;
        this.userId = userId;
        this.title = title;
        this.tags = tags;
        this.content = content;
        this.state = state;
        this.createdAt = createdAt;
    }

    public int getSuggestionId() { return suggestionId; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getTags() { return tags; }
    public String getContent() { return content; }
    public int getState() { return state; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setSuggestionId(int suggestionId) { this.suggestionId = suggestionId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setTags(String tags) { this.tags = tags; }
    public void setContent(String content) { this.content = content; }
    public void setState(int state) { this.state = state; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
