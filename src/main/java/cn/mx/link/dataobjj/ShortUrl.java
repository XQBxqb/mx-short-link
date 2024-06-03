package cn.mx.link.dataobjj;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortUrl {
    private Long id;
    private String sourceUrl;

    public ShortUrl(Long id, String sourceUrl) {
        this.id = id;
        this.sourceUrl = sourceUrl;
    }

    public ShortUrl() {
    }
}
