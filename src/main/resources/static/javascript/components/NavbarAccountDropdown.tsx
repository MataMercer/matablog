import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { Navbar } from 'rbx';

type NavbarAccountDropdownProps = {
  username: string | null;
};

class NavbarAccountDropdown extends Component<NavbarAccountDropdownProps> {
  constructor(props: NavbarAccountDropdownProps) {
    super(props);
  }

  render() {
    return (
      <Navbar.Item dropdown>
        <Navbar.Link>Docdss</Navbar.Link>
        <Navbar.Dropdown>
          <Navbar.Item href={`/${this.props.username}`}>{this.props.username}</Navbar.Item>
          <Navbar.Item href="/analytics">Analytics</Navbar.Item>
          <Navbar.Item href="/settings">Settings</Navbar.Item>
          <Navbar.Divider />
          <Navbar.Item href="/logout">
            Logout
          </Navbar.Item>
        </Navbar.Dropdown>
      </Navbar.Item>
    );
  }
}

export default NavbarAccountDropdown;
