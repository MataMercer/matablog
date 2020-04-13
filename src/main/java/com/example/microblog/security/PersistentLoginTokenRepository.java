package com.example.microblog.security;


import com.example.microblog.models.LoginToken;
import com.example.microblog.repositories.LoginTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository("persistentTokenRepository")
@Transactional
public class PersistentLoginTokenRepository implements PersistentTokenRepository  {

    @Autowired
    private LoginTokenRepository loginTokenRepository;

    @Override
    public void createNewToken(PersistentRememberMeToken persistentRememberMeToken) {

        loginTokenRepository.save(
                new LoginToken(
                        persistentRememberMeToken.getSeries(),
                        persistentRememberMeToken.getUsername(),
                        persistentRememberMeToken.getTokenValue(),
                        persistentRememberMeToken.getDate()
                )
        );
    }

    @Override
    public void updateToken(String seriesId, String tokenValue, Date lastUsed) {

        LoginToken loginToken = loginTokenRepository.findBySeries(seriesId);
        loginToken.setToken(tokenValue);
        loginToken.setLastUsed(lastUsed);
        loginTokenRepository.save(loginToken);

    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {


        LoginToken loginToken = loginTokenRepository.findBySeries(seriesId);
        if(loginToken != null){
            return new PersistentRememberMeToken(
                    loginToken.getUsername(),
                    loginToken.getSeries(),
                    loginToken.getToken(),
                    loginToken.getLastUsed()
            );
        }

        return null;
    }

    @Override
    public void removeUserTokens(String username) {

        LoginToken loginToken = loginTokenRepository.findByUsername(username);
        loginTokenRepository.delete(loginToken);
    }
}
