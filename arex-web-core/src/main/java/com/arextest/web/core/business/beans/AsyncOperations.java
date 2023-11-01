package com.arextest.web.core.business.beans;

import com.arextest.web.core.business.util.MailUtils;
import com.arextest.web.model.enums.SendEmailType;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2023/2/9
 */
@Component
public class AsyncOperations {

  @Resource
  private MailUtils mailUtils;

  @Async("sending-mail-executor")
  public void sendMailAsGuest(String userName, String subject) {
    mailUtils.sendEmail(userName, subject, StringUtils.EMPTY, SendEmailType.LOGIN_AS_GUEST);
  }
}
