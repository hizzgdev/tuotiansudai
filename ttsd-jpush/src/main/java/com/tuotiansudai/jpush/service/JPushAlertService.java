package com.tuotiansudai.jpush.service;


import com.tuotiansudai.dto.BaseDataDto;
import com.tuotiansudai.dto.BaseDto;
import com.tuotiansudai.enums.PushSource;
import com.tuotiansudai.enums.PushType;
import com.tuotiansudai.jpush.dto.JPushAlertDto;
import com.tuotiansudai.jpush.dto.JpushReportDto;
import com.tuotiansudai.jpush.repository.model.JPushAlertModel;
import com.tuotiansudai.jpush.repository.model.PushStatus;
import com.tuotiansudai.jpush.repository.model.PushUserType;

import java.util.Date;
import java.util.List;

public interface JPushAlertService {
    void buildJPushAlert(String editBy, JPushAlertDto jPushAlertDto);

    int findMaxSerialNumByType(PushType pushType);

    int findPushAlertCount(PushType pushType,
                           PushSource pushSource, PushUserType pushUserType, PushStatus pushStatus,
                           Date startTime, Date endTime, boolean isAutomatic);

    List<JPushAlertModel> findPushAlerts(int index, int pageSize, PushType pushType,
                                         PushSource pushSource, PushUserType pushUserType, PushStatus pushStatus,
                                         Date startTime, Date endTime, boolean isAutomatic);

    JPushAlertModel findJPushAlertModelById(long id);

    BaseDto<JpushReportDto> refreshPushReport(long jpushId);

    void changeJPushAlertStatus(long id, PushStatus status, String loginName);

    void changeJPushAlertContent(long id, String content, String loginName);

    void autoJPushAlertSend(JPushAlertModel jPushAlertModel);

    void manualJPushAlert(long id);

    BaseDto<BaseDataDto> pass(String loginName, long id, String ip);

    void reject(String loginName, long id, String ip);

    void delete(String loginName, long id);

    void storeJPushId(String loginName, String platform, String jPushId);

    JPushAlertModel findJPushByMessageId(long messageId);
}
