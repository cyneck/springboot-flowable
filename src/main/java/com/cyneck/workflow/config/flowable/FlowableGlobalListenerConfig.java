package com.cyneck.workflow.config.flowable;

import com.cyneck.workflow.listener.TaskBeforeListener;
import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * flowable全局监听器配置
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/6 15:38
 **/
@Configuration
@RequiredArgsConstructor
public class FlowableGlobalListenerConfig implements ApplicationListener<ContextRefreshedEvent> {
    private final SpringProcessEngineConfiguration configuration;

    private final TaskBeforeListener taskBeforeListener;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FlowableEventDispatcher dispatcher = configuration.getEventDispatcher();
        // 任务创建全局监听-待办、催办等消息发送
        dispatcher.addEventListener(taskBeforeListener, FlowableEngineEventType.TASK_CREATED);
    }
}
