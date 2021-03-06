package com.tuotiansudai.api.dto.v1_0;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class InvestRepayListResponseDataDto extends BaseResponseDataDto {

    private Integer index;
    private Integer pageSize;
    private Integer totalCount;
    @ApiModelProperty(value = "收款明细", example = "list")
    private List<InvestRepayRecordResponseDataDto> recordList;

    public List<InvestRepayRecordResponseDataDto> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<InvestRepayRecordResponseDataDto> recordList) {
        this.recordList = recordList;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

}
