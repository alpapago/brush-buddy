package com.a205.brushbuddy.pay.dto.response;

import lombok.Data;

/**
 * @author SSAFY
 * https://developers.kakao.com/docs/latest/ko/kakaopay/single-payment#approve-response-body-amount
 * KakaopayApproveResponse에서 사용
 * 필수가 아닌 클래스
 */
@Data
public class KakaopayAmount {
    private int total;
    private int taxFree;
    private int vat;
    private int point;
    private int discount;
    private int greenDeposit;
}
