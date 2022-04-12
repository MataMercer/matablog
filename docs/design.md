# Design Doc - Natablog - A Configurable Blogging and Social Media WebApp

## Disclaimer
This is a work in progress. Some ideas listed have not been implemented yet.

## Abstract
This software is intended to serve as a website for general blogging around a single person or as a social media app.
Some key features is multiple blogs per user, image sharing, authorization controls, and configurable user profiles.

## Developers
- Matamercer
- Github Contributors

## Glossary
- Blog: A site for creating articles that usually involve the user.
- Social Media Site: A site that has multiple users that can interact with each other.
- REST: The usage in this document refers to HTTP RPC, not the original definition. Therefore, API's won't return
  links to operations. Spring Boot's documentation uses REST liberally, so I think it's fair to use it here as well.

## Purpose
This a blog that is being developed to supplement my portfolio site. It is to showcase articles (developer related) and
allow discussions in the comment section. However, I also want to allow approved users to blog on the site, making it a
small social media website.

## Scope
The app is targeted towards users who want a social platform with a small number of users, such as 100-200 users.

## Technology
The backend and front end are decoupled. There are two repositories: the Matablog (API) and Matablog-Frontend (NextJS app).

### Backend
- Language: Kotlin
- Framework: Spring Boot

The API is being built with Spring Boot in Kotlin. Spring Boot has a lot of documentation on the internet and has been
used in many apps over the years. Kotlin is used because Java is very verbose due to its age. As a result, plugins like
Lombok are used to help shorten the code. Kotlin improves upon this by making it all built into the language as well as
having cleaner functional programming syntax.

The database is PostGreSQL. SQL is helpful because, as a social media app, the app needs to build complex relationships
between many entities. Also, I have little experience with MongoDB.

### Frontend
- Language: TypeScript
- Framework: NextJS

The frontend is developed in TypeScript with React/NextJS as a framework. React is chosen because of its
familiarity with the developers working on it. Like Spring Boot, it is mature and has a wide array of support and
features online. NextJS is used over Create-react-app (CRA) because NextJS has static HTML generation support. This is better
for search results because many web crawlers cannot run JavaScript to generate HTML. Also, Vercel offers free hosting service 
for NextJS apps under a generous limit. 

## Functional Requirements

### Navbar

#### Navbar Brand

- Links users back to the home page.

#### Search Bar

- Allow users to search on keyword
- Allow users to search by username or blog
- Allow users to search by tag
- There is a cooldown for every 10 searches if they are not a registered user.

#### Create Post Button

Clicking the button opens up a new post creation page.

#### Switch Active blog

- Clicking opens a dropdown menu to select which blog to switch to. After switching, all activity will be made under
  that blog.

#### Liked Posts

- Clicking on it will take the user to a page of posts they liked.

#### Notification

- Displays how many notifications a blog has.

#### User menu

- Displays Avatar
- Displays number of followers
- Displays number of following.
- Access settings
- Access Help page
- Log out

### Home

- Show posts from followed users
- Show top 10 posts of the week
- Show recent posts
- Each section toggleable in settings.

### Post

- CRUD capabilities
- Each post will have a title, content, tags, likes, reblogs, and replies.
- Posts can have a parent so they can be chained up in a tree.
    - In other words, posts will have replies to them.
    - To simplify things, posts can only have one parent, but multiple children.
- Posts can have mentions.
- Saving drafts and publishing.
- Image embedding can only be done on a draft because a database entity has to be persisted in order to attach a file to
  an entity.

### Notifications

- Blog owners will be notified if they get a like or reply.
- Blog owners turn on notifications for certain users.
- Blog owners can choose to receive emails about notifications.
- Users can have up to 50 notifications before they are deleted.

### Blogs

- User is given one default blog on registration.
- User can make multiple blogs.
- Blogs follow other blogs. Users don't follow each other. This is to keep two areas of the website separate.
- Blogs can have multiple users that can contribute to making posts.

#### Blog page

- Users can paginate through posts.

### Security

#### Authentication

- The app uses token based authentication and authorization.
    - This allows other non-matablog related apps to easily access the API if permitted by just attaching a token to the
      request.
    - Token format is JWT.
        - This allows us to store some information about the user's authorizations.
        - The information in the token allows us to make decisions on the front end or the backend on what to access.
          With session-based authentication, we would have to query the current user endpoint on the API to determine
          what authorizations the user has and appropriately display the actions on the front end the user can take.
          Without a guarantee or expiration date on these authorizations, you would have to constantly query the server
          for authorizations to be very sure they are up to date on the app.
- Sessions are managed with a short lived access token and a long lived refresh token that can generate more access
  tokens.
    - The short lived access token is stateless and non revokable. The benefit of this no database look ups for the
      duration of its life. Because it is non revokable, it poses a risk if an attacker gets ahold of it. To limit this,
      it must only last up to an hour. In addition, if the admin wishes to revoke authorizations from the user, the
      user's abilities will be revoked in a timely manner.
        - This might be a security risk being unrevokable, but the time can be adjusted on how big you wish the window
          of opportunity to be for the hacker to act on the tokens if they are stolen. In any case, the hacker will have
          a window because it will take an admin to be notified and lock the account.
    - The long lived refresh token is stateful and revokable by being stored in a database.
        - This allows the access token is be very short lived without interrupting the user experience by asking the
          user to login again.
        - The one to one relationship between the refresh toke and the access token can be a security benefit because if
          there is a one to many relationship, someone has stolen the refresh token. (assuming browsers sync the local
          storage across tabs.)
- The app stores the tokens in local storage.
    - This is supposedly a security risk, but it's not.
        - No longer vulnerable to CSRF because requests cannot be made automatically. The user must run Javascript and
          be on the site to access local storage and attach tokens to the request.
        - With an XSS attack and the attacker stealing users' local storage, that's bad. However, the attacker could
          also make fraudulent requests to the API if the tokens were in cookies. Either scenario is bad so try to avoid
          it. In the worst case scenario, someone added a malicious script into an NPM dependency in the front end app
          used by all matablog users, you could invalidate all JWT tokens by changing the private key.

#### Why not use Graphql

- Lack of familiarity. Faster to use REST.
- It doesn't have HTTP status codes for the most part since it's all packaged in one POST request. It's a waste of the
  spec to not use the error statuses specified. Basically reinventing the wheel with error handling.
- REST has Swagger and other software which is an easy way to document an API.
- Security issues with people over querying. There is probably a solution, but I don't want to deal with it. I just
  don't trust the client and rather the client consume an API in a very conservative way.
- REST overfetching is hardly an issue on a small app with not many users.
- Most times the app isn't going to submit that many requests per "page" on the app.
- Easy to cache RESTful apis.
- Almost everyone knows REST in the case more contributors help out.

### Login

- Only 10 login attempts allowed until a captcha is asked.

### Registration

- Captcha is required for registering an account.
- New account owners will be sent a verification link that expires in 10 minutes.
- New account owners can resend the link if it fails.
- When the user gets a link, it should log them in.

### Oauth Support

- Users can register using Github to provide a quick way to login without hassle.

### Side bars

- Toggleable show recent posts visited

### Reporting

- Users can report content breaking terms of service.

### Scheduled Jobs

- Unverified accounts will be deleted in 48 hours
- Expired refreshtokens are deleted every 12 hours.

## Non-Functional Requirements

- Site must load fast
- Possible PWA support.

### Deployment

The deployment is based on a balance of ease of use and price.

The API is planned to be hosted on Linode instance. The details of which are still being planned out.

The frontend will be hosted on a Vercel.

The files can optionally be stored on AWS S3 or local storage.

## Budget

However long my eyes can stand looking at the screen.
