package org.example.dataprotal.payment.service;

import org.example.dataprotal.exception.InvoiceCanNotBeCreatedException;
import org.example.dataprotal.exception.ResourceCanNotFoundException;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.payment.dto.PayriffInvoiceRequest;

public interface PayriffService {

    String createInvoiceWithUser(PayriffInvoiceRequest payriffInvoiceRequest, User user) throws InvoiceCanNotBeCreatedException;
}