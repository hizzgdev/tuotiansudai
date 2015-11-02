package com.tuotiansudai.service;

import com.tuotiansudai.dto.AnnouncementManagementDto;
import com.tuotiansudai.repository.model.AnnouncementManagementModel;

import java.util.List;

public interface AnnouncementManagementService {

    int findAnnouncementManagementCount(long id,String title);

    List<AnnouncementManagementModel> findAnnouncementManagement(long id, String title, int startLimit, int endLimit);

    void create(AnnouncementManagementDto announcementManagementDto);

    void update(AnnouncementManagementDto announcementManagementDto);

    void delete(AnnouncementManagementDto announcementManagementDto);

}
