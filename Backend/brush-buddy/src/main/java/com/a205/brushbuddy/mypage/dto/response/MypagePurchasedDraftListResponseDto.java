package com.a205.brushbuddy.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MypagePurchasedDraftListResponseDto {
    private List<MypageBookmarkedDraftListResponseDto.DraftDto> drafts;
    private Integer pageNum;
    private Integer length;
    private Integer totalPage; // 전체 페이지

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DraftDto{
        private Long draftId;
        private String draftThumbnail;
        private Timestamp draftTimestamp;
        private int draftDownload;
        private int draftBookmark;
    }
}
