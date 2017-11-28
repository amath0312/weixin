package tech.amath.common.web;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;

@Aspect
@Order(5)
@Component
public class LogAspect {

	private Logger logger = LoggerFactory.getLogger(LogAspect.class);

	ThreadLocal<Long> startTime = new ThreadLocal<Long>();

	@Pointcut("execution( * tech.amath.*.*..*.*(..))")
	public void webLog() {
	}

	@Before("webLog()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {
		logger.debug("enter weblog[do before]");
		startTime.set(System.currentTimeMillis());

		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			logger.debug("not in  RequestContext");
			return;
		}
		HttpServletRequest request = attributes.getRequest();
		// 记录下请求内容
		logger.info("===请求日志:================================================");
		logger.info("===请求URL : " + request.getRequestURL().toString());
		logger.info("===请求METHOD : " + request.getMethod());
		// logger.info("===请求IP : " + NetworkUtil.getIpAddr(request));
		logger.info("===请求调用方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "."
				+ joinPoint.getSignature().getName());
		logger.info("===请求参数 : " + Arrays.toString(joinPoint.getArgs()));
		logger.debug("leave weblog[do before]");
	}

	@AfterReturning(returning = "ret", pointcut = "webLog()")
	public void doAfterReturning(Object ret) throws Throwable {
		logger.debug("enter weblog[do after]");
		// 处理完请求，返回内容
		logger.info("===返回日志:================================================");
		logger.info("===返回值 : " + JSON.toJSONString(ret));
		logger.info("===本次请求消耗时间 (ms): " + (System.currentTimeMillis() - startTime.get()));
		logger.debug("leave weblog[do after]");
	}

}
