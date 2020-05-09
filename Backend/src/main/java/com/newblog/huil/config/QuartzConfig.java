package com.newblog.huil.config;

import com.newblog.huil.quartz.BlogScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @author HuilLIN
 */
@Configuration
public class QuartzConfig {
    /**
     * 刷新帖子分数任务
     * @return
     */
    @Bean
    public JobDetailFactoryBean blogScoreRefreshJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(BlogScoreRefreshJob.class);
        factoryBean.setName("blogScoreRefreshJob");
        factoryBean.setGroup("blogJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean blogScoreRefreshTrigger(JobDetail blogScoreRefreshJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(blogScoreRefreshJobDetail);
        factoryBean.setName("blogScoreRefreshTrigger");
        factoryBean.setGroup("blogTriggerGroup");
        /// 5分钟触发一次
        ///factoryBean.setRepeatInterval(1000 * 60 * 5);
        /** 一个小时触发一次*/
        factoryBean.setRepeatInterval(1000 * 60 * 60);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
