package net.miarma.api.microservices.huertosdecine.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

import java.time.LocalDateTime;

@Table("cine_votes")
public class VoteEntity extends AbstractEntity {
    private Integer user_id;
    private Integer movie_id;
    private Integer vote;
    private LocalDateTime voted_at;

    public VoteEntity() { super(); }

    public VoteEntity(Row row) { super(row); }

    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public Integer getMovie_id() { return movie_id; }
    public void setMovie_id(Integer movie_id) { this.movie_id = movie_id; }

    public Integer getVote() { return vote; }
    public void setVote(Integer vote) { this.vote = vote; }

    public LocalDateTime getVoted_at() { return voted_at; }
    public void setVoted_at(LocalDateTime voted_at) { this.voted_at = voted_at; }
}