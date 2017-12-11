package com.tuotiansudai.activity.repository.mapper;

import com.tuotiansudai.activity.repository.model.CashSnowballActivityModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashSnowballActivityMapper {

    void create(CashSnowballActivityModel cashSnowballActivityModel);

    void update(CashSnowballActivityModel cashSnowballActivityModel);

    CashSnowballActivityModel findByLoginName(@Param("loginName") String loginName);

    List<CashSnowballActivityModel> findAll();

}