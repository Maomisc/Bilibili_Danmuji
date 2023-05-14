package xyz.acproject.danmuji.component.global;

import lombok.Data;

@Data
public class GlobalProperty {
    private String host;
    private String url;

    public GlobalProperty(String host, String url) {
        this.host = host;
        this.url = url;

    }
}
