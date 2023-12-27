package com.unitech.banking.security;

import com.unitech.banking.model.entity.Client;
import com.unitech.banking.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String pin) throws UsernameNotFoundException {
        Client client = clientRepository.findByPin(pin)
                .orElseThrow(()-> new UsernameNotFoundException(pin));

        return new UserDetailsImpl(client);
    }
}
