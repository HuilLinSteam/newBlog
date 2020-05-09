package com.newblog.huil.dao.elasticsearch;

import com.newblog.huil.entity.Blog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author HuilLIN
 */
public interface BlogRepository extends ElasticsearchRepository<Blog,Integer> {
}
