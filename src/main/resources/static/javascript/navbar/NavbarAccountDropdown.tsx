import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { Navbar } from 'rbx';

type NavbarAccountDropdownProps = {
    username: string | null;
};

const NavbarAccountDropdown = ({ username }: NavbarAccountDropdownProps) => {
    return (
        <Navbar.Item dropdown>
            <Navbar.Link>{username}</Navbar.Link>
            <Navbar.Dropdown>
                <Navbar.Item href={`/profile/${username}`}>
                    My Profile
                </Navbar.Item>
                <Navbar.Item href="/settings">Settings</Navbar.Item>
                <Navbar.Divider />
                <Navbar.Item href="/logout">Logout</Navbar.Item>
            </Navbar.Dropdown>
        </Navbar.Item>
    );
};

export default NavbarAccountDropdown;
