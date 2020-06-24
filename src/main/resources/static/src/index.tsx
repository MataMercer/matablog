import React from 'react';
import ReactDOM from 'react-dom';
import NewPostModal from '../javascript/components/NewPostModal';
import NavbarAccountDropdown from '../javascript/components/NavbarAccountDropdown';
import CreatePostForm from '../javascript/components/CreatePostForm';



const container = document.getElementById('NavbarAccountDropdown');

ReactDOM.render(
  <NavbarAccountDropdown username={container ? container.getAttribute('username') : null}/>,
  container
);


ReactDOM.render(<CreatePostForm/>, document.getElementById('createPostForm'));
