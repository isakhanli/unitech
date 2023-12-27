package com.unitech.banking.service;

import com.unitech.banking.model.dto.AccountListResponse;
import com.unitech.banking.model.dto.TransferRequest;
import com.unitech.banking.model.dto.TransferResponse;

public interface AccountService {
    AccountListResponse getActiveAccountList();
    TransferResponse transfer(TransferRequest request);
}
