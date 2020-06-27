import React, { Suspense } from 'react';
import ReactDOM from 'react-dom';
import NewPostModal from '../javascript/components/NewPostModal';
import NavbarAccountDropdown from '../javascript/components/NavbarAccountDropdown';
import {PageLoader} from 'rbx';

const CreatePostForm = React.lazy(() =>
    import('../javascript/components/CreatePostForm')
);

const container = document.getElementById('NavbarAccountDropdown');

ReactDOM.render(
    <NavbarAccountDropdown
        username={container ? container.getAttribute('username') : null}
    />,
    container
);

const container1 = document.getElementById('createPostForm');
container1
    ? ReactDOM.render(
          <Suspense fallback={<PageLoader/>}>
              <CreatePostForm />
          </Suspense>,
          container1
      )
    : null;
