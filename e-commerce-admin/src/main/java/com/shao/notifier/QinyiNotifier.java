package com.shao.notifier;


import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 自定义监控
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class QinyiNotifier extends AbstractEventNotifier {

    protected QinyiNotifier(InstanceRepository repository) {
        super(repository);
    }

    /**
     * <h2>实现对事件的通知</h2>
     * */
    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {

        /**
         * 当一个应用启动后，产生的日志：
         * 2022-08-27 23:46:53.474  INFO 14660 --- [ask-Scheduler-1] com.shao.notifier.QinyiNotifier          : Instance Info: [ecommerce-nacos-client], [888b77bf031b], [REGISTERED]
         * 2022-08-27 23:46:53.592  INFO 14660 --- [ctor-http-nio-4] com.shao.notifier.QinyiNotifier          : Instance Status Change: [ecommerce-nacos-client], [888b77bf031b], [UP]
         * 2022-08-27 23:46:53.627  INFO 14660 --- [ctor-http-nio-4] com.shao.notifier.QinyiNotifier          : Instance Info: [ecommerce-nacos-client], [888b77bf031b], [ENDPOINTS_DETECTED]
         */

        return Mono.fromRunnable(() -> {

            if (event instanceof InstanceStatusChangedEvent) {
                log.info("Instance Status Change: [{}], [{}], [{}]",
                        instance.getRegistration().getName(), event.getInstance(),
                        ((InstanceStatusChangedEvent) event).getStatusInfo().getStatus());
            } else {
                log.info("Instance Info: [{}], [{}], [{}]",
                        instance.getRegistration().getName(), event.getInstance(),
                        event.getType());
            }

        });
    }
}
