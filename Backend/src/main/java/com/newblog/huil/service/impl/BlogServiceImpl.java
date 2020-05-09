package com.newblog.huil.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.newblog.huil.dao.BlogMapper;
import com.newblog.huil.entity.Blog;
import com.newblog.huil.service.BlogService;
import com.newblog.huil.utils.MdToHtml;
import com.newblog.huil.utils.RedisKeyUtil;
import com.newblog.huil.utils.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author HuilLIN
 */
@Service
public class BlogServiceImpl implements BlogService {
    private static final Logger logger = LoggerFactory.getLogger(BlogServiceImpl.class);
    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${caffeine.blogs.max-size}")
    private int maxSize;

    @Value("${caffeine.blogs.expire-seconds}")
    private int expireSeconds;

    /**博客缓存列表*/
    private LoadingCache<String,List<Blog>> blogListCache;

    /** 博客总数缓存*/
    private LoadingCache<Integer,Integer> blogRowsCache;

    @PostConstruct
    public void init(){
        // 初始化
        blogListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<Blog>>() {
                    @Nullable
                    @Override
                    public List<Blog> load(@NonNull String key) throws Exception {
                        if(key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误");
                        }
                        String[] params = key.split(":");
                        if(params == null || params.length!=2){
                            throw new IllegalArgumentException("参数错误!");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        // 二级缓存: Redis -> mysql
                        logger.debug("load blog list from DB.");
                        return blogMapper.selectBlog(0,offset,limit,1);
                    }
                });
        //初始化博客总数缓存
        blogRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load blog rows from DB.");
                        return blogMapper.selectAllBlogRows(key);
                    }
                });
    }

    @Override
    public List<Blog> findBlog(int userId, int offset, int limit, int orderMode) {
        if(userId == 0 && orderMode == 1){
            return blogListCache.get(offset+":"+limit);
        }
        logger.debug("load blog list from DB.");
        return blogMapper.selectBlog(userId,offset,limit,orderMode);
    }

    @Override
    public int findBlogRows(int userId) {
        if(userId == 0){
            return blogRowsCache.get(userId);
        }
        logger.debug("load blog rows from DB.");
        return blogMapper.selectAllBlogRows(userId);
    }


    @Override
    public int saveBlog(Blog blog) {
        if(blog == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义HTML标记
        blog.setTitle(HtmlUtils.htmlEscape(blog.getTitle()));
        blog.setSummary(HtmlUtils.htmlEscape(blog.getSummary()));
        blog.setContent(HtmlUtils.htmlEscape(blog.getContent()));
        // 过滤敏感词
        blog.setTitle(sensitiveFilter.filter(blog.getTitle()));
        blog.setSummary(sensitiveFilter.filter(blog.getSummary()));
        blog.setContent(sensitiveFilter.filter(blog.getContent()));
        blog.setHtmlContent(MdToHtml.convert(blog.getContent()));
        blogMapper.insertBlog(blog);
        int blogId = blog.getId();
        String enjoyBlogKey = RedisKeyUtil.getEnjoyBlogKey(blogId);
        redisTemplate.opsForValue().set(enjoyBlogKey,0);
        return blogId;
    }

    @Override
    public Blog findBlogById(int id) {
        return blogMapper.selectBlogById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return blogMapper.updateCommentCount(id,commentCount);
    }

    @Override
    public int getEnjoyCount(int blogId) {
        String enjoyBlogKey = RedisKeyUtil.getEnjoyBlogKey(blogId);
        Integer count = (Integer) redisTemplate.opsForValue().get(enjoyBlogKey);
        int readCount = count == null ? 0 : count.intValue();
        return readCount;
    }

    @Override
    public void addEnjoyCount(int blogId) {
        String enjoyBlogKey = RedisKeyUtil.getEnjoyBlogKey(blogId);
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                Boolean hasKey = redisTemplate.hasKey(enjoyBlogKey);
                redisOperations.multi();
                if(hasKey){
                    redisTemplate.opsForValue().increment(enjoyBlogKey);
                }else{
                    redisTemplate.opsForValue().set(enjoyBlogKey,0);
                }
                return redisOperations.exec();
            }
        });
    }

    @Override
    public int updateType(int id, int type) {
        return blogMapper.updateType(id,type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return blogMapper.updateStatus(id,status);
    }

    @Override
    public int updateScore(int id, double score) {
        return blogMapper.updateScore(id,score);
    }

    @Override
    public int updateBlog(Blog blog) {
        //转义HTML标记
        blog.setTitle(HtmlUtils.htmlEscape(blog.getTitle()));
        blog.setSummary(HtmlUtils.htmlEscape(blog.getSummary()));
        blog.setContent(HtmlUtils.htmlEscape(blog.getContent()));
        // 过滤敏感词
        blog.setTitle(sensitiveFilter.filter(blog.getTitle()));
        blog.setSummary(sensitiveFilter.filter(blog.getSummary()));
        blog.setContent(sensitiveFilter.filter(blog.getContent()));
        blog.setHtmlContent(MdToHtml.convert(blog.getContent()));
        return blogMapper.updateBlog(blog);
    }


}
