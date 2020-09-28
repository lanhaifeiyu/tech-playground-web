package com.lhfeiyu.tech.task;

import com.zom.statistics.handler.StaLog1SourceHandler;
import com.zom.statistics.handler.StaLog2ParseHandler;
import com.zom.statistics.handler.StaLog4SumHandler;
import com.zom.statistics.handler.StaTestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private StaLog1SourceHandler staLog1SourceHandler;
    @Autowired
    private StaLog2ParseHandler staLog2ParseHandler;
    @Autowired
    private StaLog4SumHandler staLog4SumHandler;
    @Autowired
    private StaTestHandler staTestHandler;

    private boolean no_parse = false;

    private static Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Scheduled(fixedRate = 1000 * 30)
    public void insertOriginalLog() {
        try {
            logger.debug("-- redis start --");
            staLog1SourceHandler.insertOriginalLog();
            logger.debug("-- redis end --");
            // 捕获不到异步的异常，比如REDIS连接超时
        } catch (Exception e) {
            logger.error("insertOriginalLog error:{}", e.getMessage());
            e.printStackTrace();
            logger.debug("-- redis end with error --");
        }

        // XXX 隔一轮执行一次
        no_parse = !no_parse;
        if (no_parse) {
            return;
        }
        logger.debug("-- parse start --");
        staLog2ParseHandler.parseOriginLog();
        staLog4SumHandler.doSumDomain();
        logger.debug("-- parse end --");
    }

    // XXX FOR TEST
    /*@Scheduled(fixedRate = 1000 * 3600)
    public void test_mock_data() {
        staTestHandler.test_mock_data();
    }*/


    /**
     * TODO:
     * TODO 看看数据库结构是否有优化的空间
     * DONE: 数据库有改动，staDayUser, staDayDept新增了uniqueId字段, 设置唯一索引
     * DONE: 配置事务拦截
     * TODO: 可以考虑启一个定时任务，专门检查异常的数据，正常的逻辑下不应该出现异常的数据，这算是一个安全逻辑，保障不因业务逻辑造成重大性能问题
     * 比如说：检查同一个用户是否有多个持续的业务在定时刷新，这种场景会造成大量的定时刷新，影响性能同时产生大量log
     *
     */


    // old blow ----------------------------------------------

    /*@Scheduled(fixedRate = 1000 * 60)
    public void task1() {
        //staLogService.parseLog4();
    }

    @Autowired
    private IStaLogService staLogService;*/

}
