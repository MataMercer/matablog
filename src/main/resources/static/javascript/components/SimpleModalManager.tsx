import React from 'react';

import { Modal, Button } from 'rbx';
import { ModalProps } from 'rbx/components/modal/modal';

interface SimpleModalManagerProps {
    modalProps: Omit<ModalProps, 'onClose' | 'active'>;
    children: React.ReactNode;
}

interface SimpleModalManagerState {
    active: boolean;
}

export class SimpleModalManager extends React.Component<
    SimpleModalManagerProps,
    SimpleModalManagerState
> {
    public static defaultProps = {
        modalProps: {},
    };

    public readonly state: SimpleModalManagerState = { active: false };

    public render() {
        return (
            <div>
                <Button onClick={this.open}>Open modal</Button>
                <Modal
                    active={this.state.active}
                    onClose={this.close}
                    {...this.props.modalProps}
                >
                    {this.props.children}
                </Modal>
            </div>
        );
    }

    private readonly close = () => {
        this.setState({ active: false });
    };
    private readonly open = () => {
        this.setState({ active: true });
    };
}
