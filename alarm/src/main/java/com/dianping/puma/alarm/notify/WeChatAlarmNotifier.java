package com.dianping.puma.alarm.notify;

import com.dianping.puma.alarm.exception.PumaAlarmNotifyException;
import com.dianping.puma.alarm.exception.PumaAlarmNotifyUnsupportedException;
import com.dianping.puma.alarm.model.AlarmResult;
import com.dianping.puma.alarm.model.meta.AlarmMeta;
import com.dianping.puma.alarm.model.meta.WeChatAlarmMeta;
import com.dianping.puma.alarm.service.PumaWeChatService;
import com.dianping.puma.common.AbstractPumaLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by xiaotian.li on 16/3/17.
 * Email: lixiaotian07@gmail.com
 */
public class WeChatAlarmNotifier extends AbstractPumaLifeCycle implements PumaAlarmNotifier {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private PumaWeChatService weChatService;

    @Override
    public void notify(AlarmResult result, AlarmMeta meta) throws PumaAlarmNotifyException {
        if (!(meta instanceof WeChatAlarmMeta)) {
            throw new PumaAlarmNotifyUnsupportedException("unsupported alarm meta[%s]", meta);
        }

        if (!result.isAlarm()) {
            return;
        }

        String title = result.getTitle();
        String content = result.getContent();

        WeChatAlarmMeta weChatAlarmMeta = (WeChatAlarmMeta) meta;
        List<String> recipients = weChatAlarmMeta.getWeChatRecipients();

        for (String recipient : recipients) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Send wechat with message[{}] to recipient[{}].",
                            title + content, recipient);
                }
                weChatService.send(recipient, title + content);
            } catch (Throwable t) {
                logger.error("Failed to send wechat with message[{}] to recipient[{}].",
                        title + content, recipient, t);
            }
        }
    }

    public void setWeChatService(PumaWeChatService weChatService) {
        this.weChatService = weChatService;
    }
}