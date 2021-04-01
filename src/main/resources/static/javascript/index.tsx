import React, { Suspense } from 'react';
import reactDomRender from './ReactDOMRender';

const RegisterForm = React.lazy(() => import('./forms/RegisterForm'));

const CreatePostForm = React.lazy(() => import('./forms/CreatePostForm'));

const NavbarAccountDropdown = React.lazy(() =>
    import('./navbar/NavbarAccountDropdown')
);

reactDomRender(NavbarAccountDropdown, 'ReactNavbarAccountDropdown');
reactDomRender(RegisterForm, 'ReactRegisterForm');
reactDomRender(CreatePostForm, 'ReactCreatePostForm');
