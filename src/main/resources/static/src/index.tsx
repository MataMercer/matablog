import React, { Suspense } from 'react';
import ReactDOM from 'react-dom';
import NewPostModal from '../javascript/components/NewPostModal';
// import RegisterForm from '../javascript/components/forms/RegisterForm';
// import NavbarAccountDropdown from '../javascript/components/NavbarAccountDropdown';
// const CreatePostForm = React.lazy(() =>
//     import('../javascript/components/CreatePostForm')
// );

const RegisterForm = React.lazy(() =>
    import('../javascript/components/forms/RegisterForm')
);

// const CreatePostForm = React.lazy(() =>
//     import('../javascript/components/forms/CreatePostForm')
// );

const NavbarAccountDropdown = React.lazy(() =>
    import('../javascript/components/NavbarAccountDropdown')
);

const ReactNavbarAccountDropDownContainer = document.getElementById(
    'ReactNavbarAccountDropdown'
);
if (ReactNavbarAccountDropDownContainer) {
    ReactDOM.render(
        <Suspense fallback={<div>loading...</div>}>
            <NavbarAccountDropdown
                username={ReactNavbarAccountDropDownContainer.getAttribute(
                    'data-username'
                )}
            />
        </Suspense>,
        ReactNavbarAccountDropDownContainer
    );
}

const ReactRegisterFormContainer = document.getElementById('ReactRegisterForm');
if (ReactRegisterFormContainer) {
    ReactDOM.render(
        <Suspense fallback={<div>Loading...</div>}>
            <RegisterForm />
        </Suspense>,
        ReactRegisterFormContainer
    );
}

// const ReactCreatePostFormContainer = document.getElementById('ReactRegisterForm');
// if(ReactCreatePostFormContainer) {
//     ReactDOM.render(
//         <Suspense fallback={<div>Loading...</div>}>
//             <CreatePostForm csrfParameterName={} csrfToken={}/>
//         </Suspense>,
//         ReactCreatePostFormContainer
//     )
// }

// const container1 = document.getElementById('createPostForm');
// container1
//     ? ReactDOM.render(
//           <Suspense fallback={<PageLoader />}>
//               <CreatePostForm
//                   csrfParameterName={container1.getAttribute(
//                       'data-csrfParameterName'
//                   )}
//                   csrfToken={container1.getAttribute('data-csrfToken')}
//               />
//           </Suspense>,
//           container1
//       )
//     : null;
