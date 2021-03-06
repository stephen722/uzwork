package com.uzskill.base.spring;

import javax.servlet.ServletContextEvent;

import com.light.app.ApplicationContext;
import com.light.spring.SpringContextLoaderListener;
import com.uzskill.base.schedule.SynchronizeScheduleProcessor;
import com.uzskill.base.schedule.SynchronizeScheduleService;

/**
 * 该类继承自ContextLoaderListener，当一个Web应用上下文初始化成功后，系统将取得该上下文并将其设置到ApplicationContext，
 * 方便以后在Action，servlet之外的类中可以使用该上下文获取Bean。
 * 
 * <p>(C) 2015 www.uzwork.com (UZWork)</p>
 * Date:  2015-07-10
 * 
 * @author  Stephen Yang
 * @version UZWork-Base 1.0
 */
public class SkillSpringContextLoaderListener extends SpringContextLoaderListener {
	
	public void contextInitialized(ServletContextEvent event) {
		// Log4jServletContextListener.contextInitialized() executed before this method, so the logger works
		super.contextInitialized(event);
		SynchronizeScheduleProcessor scheduleProcess = (SynchronizeScheduleProcessor)ApplicationContext.getInstance().getBean("SynchronizeScheduleProcessor");
		scheduleProcess.scheduleInit();
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		// Log4jServletContextListener.contextDestroyed() executed before this method, so the logger doesn't work any longer.
		SynchronizeScheduleService.getInstance().shutdown();
		super.contextDestroyed(event);
	}
}
