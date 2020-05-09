package com.newblog.huil.service.impl;

import com.newblog.huil.dao.elasticsearch.BlogRepository;
import com.newblog.huil.entity.Blog;
import com.newblog.huil.service.ElasticSearchService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author HuilLIN
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public void saveBlog(Blog blog) {
        blogRepository.save(blog);
    }

    @Override
    public void deleteBlog(int id) {
        blogRepository.deleteById(id);
    }

    @Override
    public Page<Blog> searchBlog(String keyword, int current, int limit) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "summary", "htmlContent"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<span style='color:red'>").postTags("</span>"),
                        new HighlightBuilder.Field("htmlContent").preTags("<span style='color:red'>").postTags("</span>"),
                        new HighlightBuilder.Field("summary").preTags("<span style='color:red'>").postTags("</span>")
                ).build();

        return elasticsearchTemplate.queryForPage(searchQuery, Blog.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                SearchHits hits = response.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }

                List<Blog> list = new ArrayList<>();
                for (SearchHit hit : hits) {
                    Blog blog = new Blog();

                    String id = hit.getSourceAsMap().get("id").toString();
                    blog.setId(Integer.valueOf(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    blog.setUserId(Integer.valueOf(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    blog.setTitle(title);

                    String summary = hit.getSourceAsMap().get("summary").toString();
                    blog.setSummary(summary);

                    String content = hit.getSourceAsMap().get("content").toString();
                    blog.setContent(content);

                    String status = hit.getSourceAsMap().get("status").toString();
                    blog.setStatus(Integer.valueOf(status));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    blog.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    blog.setCommentCount(Integer.valueOf(commentCount));

                    // 处理高亮显示
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        blog.setTitle(titleField.getFragments()[0].toString());
                    }
                    HighlightField summaryField = hit.getHighlightFields().get("summary");
                    if (summaryField != null) {
                        blog.setSummary(summaryField.getFragments()[0].toString());
                    }
                    HighlightField htmlContent = hit.getHighlightFields().get("htmlContent");
                    if (htmlContent != null){
                        blog.setHtmlContent(htmlContent.getFragments()[0].toString());
                    }
                    list.add(blog);

                }

                return new AggregatedPageImpl(
                        list,pageable,hits.getTotalHits(),
                        response.getAggregations(),response.getScrollId(),
                        hits.getMaxScore());
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> type) {
                return null;
            }
        });
    }
}
