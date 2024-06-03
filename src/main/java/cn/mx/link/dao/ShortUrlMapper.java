package cn.mx.link.dao;

import cn.mx.link.dataobjj.ShortUrl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShortUrlMapper extends BaseMapper<ShortUrl> {
    @Select("select id from short_url where source_url = #{item} LIMIT 1")
    public Long selectId(String id);
}
