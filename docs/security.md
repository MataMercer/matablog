# Security

## Authorization

The server uses JWTs to authorize requests to protected API endpoints.

### Definitions

- Jwt/Token: An encrypted token that is signed by the server to guarantee that the information it contains is untampered
  and the token was solely made by the server. As a note, these terms are used interchangeably and mean the same thing
  in this document.
- Session: Classic authorization where the client sends an ID in a cookie to the server in every request, and the server
  checks its database if that ID corresponds to an ongoing login session.
- SPA: Single Page Application 
- MPA: Multi Page Application

### Why JWT

#### Performance

Unlike typical MPAs, SPAs often make very frequent requests to many protected routes because not everything is
prerendered all in one page. For example, a React app might render a user's dashboard by fetching new posts, popular
posts, and trending topics in separate requests. It would slow down if on each of these requests the database was hit to
authorize the user. JWTs solve this by not hitting the database at all and using the signing of the JWT to authorize the
user's session.

### Refresh Tokens

There are two JWT tokens involved.

- Access token: This allows users to make requests. It is short lived.
- Refresh token: This allows users to get more access tokens without username/password credentials by submitting it to
  the server. It is long lived, allowing the user to sustain access for a long time.

#### Why two tokens

The main problem with JWTs is you cannot revoke them once issued. This is because they do not hit the database to check
if they are valid still. This is a problem if the user login has been hacked, and you need to lock a user out of their
account.

The solution is to make the tokens very short-lived, so even if they are stolen, the attacker has very little time to
act upon them. To ensure a good UX, the app should not ask the user to relogin frequently. To get around this, the
server issues a refresh token upon logging in. This can be used to sustain or refresh a session for a long time. The
refresh token differs from the access token in that it is checked by the database every time it is used to generate a
new refresh token. If the database says the refresh token is banned or the user logged out, the client will not give a
new access token.

### Implementation

#### How it works

Login Process

- The users sends their username/password in a form to /login route.
- The server checks if the password is correct and generates the access and refresh token.
- The access token's subject is the username. The claims are a map of the user's role to authorization rights.
- In the response, the authorization header is set to the access token. The refresh token is set to a https only cookie.
- The client receives the response. It stores the refresh token in a secure HTTP cookie, and the access token in memory.

Authorizing Routes

- The client accesses a protected route by sending a request with the access token in the authorization header. The
  refresh token is not sent in the cookie.
- The server recognizes the route is protected and sends the request through JwtTokenVerifier filter.
- The JwtTokenVerifier filter checks the token is legit or not. If it is, set the security context with the user
  information and let the request continue.

Refreshing Tokens

#### Refresh Token Entity

The refresh token entity represents a single login session on 1 client. A user may have multiple refresh tokens if they
login to multiple devices. On logout, password change, or account lock out, the refresh token is destroyed on the server. 
On the client the access token is simply deleted from local storage.

- user: The owner of the refresh token.
- token: The token itself
- accessToken: The last access token sent to the user.

The user field is to know who owns the token and quickly lookup the token. The token itself is to match the token with
the refresh token the user sent to see if it still exists. The access token is used to set off a trigger to warn
suspicious activity when someone requests an access token when there is still a non-expired access token in existence. 
