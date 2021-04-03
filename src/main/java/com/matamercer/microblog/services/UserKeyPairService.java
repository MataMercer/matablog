package com.matamercer.microblog.services;

import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.entities.UserKeyPair;
import com.matamercer.microblog.models.repositories.UserKeyPairRepository;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class UserKeyPairService {
    private final UserKeyPairRepository userKeyPairRepository;

    public UserKeyPairService(UserKeyPairRepository userKeyPairRepository){
        this.userKeyPairRepository = userKeyPairRepository;
    }

    public UserKeyPair createUserKeyPairForUser(User user) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        UserKeyPair userKeyPair = new UserKeyPair(user,
                Base64.getMimeEncoder().encodeToString(keyPair.getPublic().getEncoded()),
                Base64.getMimeEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        return userKeyPairRepository.save(userKeyPair);
    }
}
