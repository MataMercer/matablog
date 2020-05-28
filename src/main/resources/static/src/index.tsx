import React from 'react';
import ReactDOM from 'react-dom';
import NewPostModal from '../javascript/components/NewPostModal';
import NavbarAccountDropdown from '../javascript/components/NavbarAccountDropdown';




const container = document.getElementById('NavbarAccountDropdown');

ReactDOM.render(
  <NavbarAccountDropdown username={container ? container.getAttribute('username') : null}/>,
  container
);
