package com.gwana.server.mapper;

import com.gwana.server.dto.payment.PaymentInfoRequest;
import com.gwana.server.dto.payment.PaymentInfoResponse;
import com.gwana.server.dto.payment.PaymentSession;
import com.gwana.server.dto.payment.PaymentSessionRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentMapper {
    void deletePaymentSession(String userId);

    void createPaymentSession(PaymentSessionRequest paymentSessionRequest);

    List<PaymentSession> getPaymentSession(String sessionId);

    void upsertPaymentInfo(PaymentInfoRequest paymentInfoRequest);

    PaymentInfoResponse findByOrderId(String orderId);
}
