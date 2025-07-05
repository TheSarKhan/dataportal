package org.example.dataprotal.payment.service;

import org.example.dataprotal.exception.InvoiceCanNotBeCreatedException;
import org.example.dataprotal.exception.ResourceCanNotFoundException;
import org.example.dataprotal.payment.dto.PayriffInvoiceRequest;

public interface PayriffService {

    String createInvoice(PayriffInvoiceRequest payriffInvoiceRequest,String token) throws ResourceCanNotFoundException, InvoiceCanNotBeCreatedException;}
