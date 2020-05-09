package com.newblog.huil.quartz;

import com.newblog.huil.entity.Blog;
import com.newblog.huil.service.BlogService;
import com.newblog.huil.service.ElasticSearchService;
import com.newblog.huil.service.LikeService;
import com.newblog.huil.utils.NewBlogConstant;
import com.newblog.huil.utils.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author HuilLIN
 */
public class BlogScoreRefreshJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(BlogScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BlogService blogService;

    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticSearchService elasticSearchService;

    // 博客纪元
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-01-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化博客纪元异常!");
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getBlogScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if (operations.size() == 0) {
            logger.info("[任务取消] 冇有要刷新的博客哦！");
            return;
        }

        logger.info("[任务开始] 正在刷新博客的分数：" + operations.size());

        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 博客分数刷新完毕哦！");

    }

    private void refresh(Integer blogId) {
        Blog blog = blogService.findBlogById(blogId);
        if (blog == null) {
            logger.error("该博客不存在: id = " + blogId);
            return;
        }

        //是否精华
        boolean wonderful = blog.getStatus() == 1;
        // 评论数量
        int commentCount = blog.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(NewBlogConstant.ENTITY_TYPE_POST, blogId);

        //计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数 = 博客权重+距离天数
        double score = Math.log10(Math.max(w, 1))
                + (blog.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        // 更新博客分数
        blogService.updateScore(blogId,score);
        // 同步搜索
        blog.setScore(score);
        elasticSearchService.saveBlog(blog);
    }
}
