import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { Button, Modal, Delete } from 'rbx';
// import Modal from '../bulma/Modal';

type NewPostModalState = {
  modalState: boolean;
};

class NewPostModal extends Component<{}, NewPostModalState> {
  constructor(props: {}) {
    super(props);

    this.state = {
      modalState: false,
    };

    this.toggleModal = this.toggleModal.bind(this);
  }

  toggleModal() {
    this.setState((prev, props) => {
      const newState = !prev.modalState;
      return { modalState: newState };
    });

    this.render();
  }

  public render() {
    return (
      <div>
        <Button color="primary" onClick={this.toggleModal.bind(this)}>
          <strong>New Post</strong>
        </Button>
        {this.state.modalState ? (
          <Modal
            active={this.state.modalState}
            onClose={this.toggleModal}
            closeOnBlur={true}
          >
            <Modal.Background />
            <Modal.Card>
              <Modal.Card.Head>
                <Modal.Card.Title>New Post</Modal.Card.Title>
                <Delete />
              </Modal.Card.Head>
              <Modal.Card.Body>
                <form action="/posts/newpost" method="post">
                  <p>
                    Content: <input type="text" />
                  </p>
                </form>
              </Modal.Card.Body>
              <Modal.Card.Foot>
                <Button color="success">Submit</Button>
                <Button>Cancel</Button>
              </Modal.Card.Foot>
            </Modal.Card>
          </Modal>
        ) : (
          ''
        )}
      </div>
    );
  }
}

export default NewPostModal;
