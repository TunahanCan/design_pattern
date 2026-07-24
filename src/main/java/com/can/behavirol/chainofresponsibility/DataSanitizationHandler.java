package com.can.behavirol.chainofresponsibility;

public class DataSanitizationHandler extends BaseOrderRequestHandler
{
    @Override
    public boolean handle(OrderRequest request)
    {
        String sanitized = request.getPayload()
                .replace("<", "")
                .replace(">", "")
                .trim();
        request.setPayload(sanitized);
        if (sanitized.isBlank()) {
            request.completeAs(
                    OrderRequestOutcome.REJECTED,
                    "Payload temizleme sonrasında boş olamaz."
            );
            return false;
        }
        return checkNext(request);
    }
}
