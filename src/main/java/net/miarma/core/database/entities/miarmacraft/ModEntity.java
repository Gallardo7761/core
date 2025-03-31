package net.miarma.core.database.entities.miarmacraft;

import java.time.LocalDateTime;

import net.miarma.core.common.Table;

@Table("Mods")
public class ModEntity {
    private int modId;
    private String name;
    private String url;
    private String state;
    private LocalDateTime timestamp;

    public ModEntity(int modId, String name, String url, String state, LocalDateTime timestamp) {
        this.modId = modId;
        this.name = name;
        this.url = url;
        this.state = state;
        this.timestamp = timestamp;
    }

    public int getModId() { return modId; }
    public String getName() { return name; }
    public String getUrl() { return url; }
    public String getState() { return state; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setModId(int modId) { this.modId = modId; }
    public void setName(String name) { this.name = name; }
    public void setUrl(String url) { this.url = url; }
    public void setState(String state) { this.state = state; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}