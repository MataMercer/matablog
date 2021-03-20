import React, { Suspense } from 'react';
import ReactDOM from 'react-dom';

// const CreatePostForm = React.lazy(() =>
//     import('../javascript/components/CreatePostForm')
// );
import reactDomRender from './ReactDOMRender';

const RegisterForm = React.lazy(() =>
    import('../javascript/components/forms/RegisterForm')
);

const NavbarAccountDropdown = React.lazy(() =>
    import('../javascript/components/NavbarAccountDropdown')
);

reactDomRender(NavbarAccountDropdown, 'ReactNavbarAccountDropdown');
reactDomRender(RegisterForm, 'ReactRegisterForm');
