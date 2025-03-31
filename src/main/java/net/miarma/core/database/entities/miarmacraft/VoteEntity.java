package net.miarma.core.database.entities.miarmacraft;

import net.miarma.core.common.Table;

@Table("Votes")
public class VoteEntity {
	private int voteId;
    private int suggestionId;
    private int userId;
    private int voteType;

    public VoteEntity(int voteId, int suggestionId, int userId, int voteType) {
        this.voteId = voteId;
        this.suggestionId = suggestionId;
        this.userId = userId;
        this.voteType = voteType;
    }

    public int getVoteId() { return voteId; }
    public int getSuggestionId() { return suggestionId; }
    public int getUserId() { return userId; }
    public int getVoteType() { return voteType; }

    public void setVoteId(int voteId) { this.voteId = voteId; }
    public void setSuggestionId(int suggestionId) { this.suggestionId = suggestionId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setVoteType(int voteType) { this.voteType = voteType; }
}
