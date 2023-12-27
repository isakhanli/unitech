package com.unitech.banking.service.impl;

import com.unitech.banking.service.PinService;
import org.springframework.stereotype.Service;

@Service
public class PinServiceImpl implements PinService {
    @Override
    public boolean isValid(String pin) {
        return true;
    }
}
