import React, { Suspense } from 'react';
import ReactDOM from 'react-dom';

// const CreatePostForm = React.lazy(() =>
//     import('../javascript/components/CreatePostForm')
// );
import reactDomRender from './ReactDOMRender';

const RegisterForm = React.lazy(() =>
    import('../javascript/components/forms/RegisterForm')
);

// const CreatePostForm = React.lazy(() =>
//     import('../javascript/components/forms/CreatePostForm')
// );

const NavbarAccountDropdown = React.lazy(() =>
    import('../javascript/components/NavbarAccountDropdown')
);

// const ReactNavbarAccountDropDownContainer = document.getElementById(
//     'ReactNavbarAccountDropdown'
// );
// if (ReactNavbarAccountDropDownContainer) {
//     ReactDOM.render(
//         <Suspense fallback={<div>loading...</div>}>
//             <NavbarAccountDropdown
//                 username={ReactNavbarAccountDropDownContainer.getAttribute(
//                     'data-username'
//                 )}
//             />
//         </Suspense>,
//         ReactNavbarAccountDropDownContainer
//     );
// }
reactDomRender(NavbarAccountDropdown, 'ReactNavbarAccountDropdown');
reactDomRender(RegisterForm, 'ReactRegisterForm');
