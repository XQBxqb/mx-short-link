package cn.mx.link.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShortLinkRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String url;
}
