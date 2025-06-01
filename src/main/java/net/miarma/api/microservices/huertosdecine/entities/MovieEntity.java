package net.miarma.api.microservices.huertosdecine.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

@Table("cine_movies")
public class MovieEntity extends AbstractEntity {
    private Integer movie_id;
    private String title;
    private String description;
    private String cover;

    public MovieEntity() {
        super();
    }

    public MovieEntity(Row row) {
        super(row);
    }

    public Integer getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(Integer movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
