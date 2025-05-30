package net.miarma.api.core.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.CoreFileContext;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

import java.time.LocalDateTime;

@Table("files")
public class FileEntity extends AbstractEntity {
    private Integer file_id;
    private String file_name;
    private String file_path;
    private String mime_type;
    private Integer uploaded_by;
    private CoreFileContext context;
    private LocalDateTime uploaded_at;
    
    public FileEntity() {
		super();
	}
    
    public FileEntity(Row row) {
    	super(row);
    }

    public Integer getFile_id() {
        return file_id;
    }

    public void setFile_id(Integer file_id) {
        this.file_id = file_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public Integer getUploaded_by() {
        return uploaded_by;
    }

    public void setUploaded_by(Integer uploaded_by) {
        this.uploaded_by = uploaded_by;
    }

    public CoreFileContext getContext() {
        return context;
    }

    public void setContext(CoreFileContext context) {
        this.context = context;
    }

    public LocalDateTime getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(LocalDateTime uploaded_at) {
        this.uploaded_at = uploaded_at;
    }
}
