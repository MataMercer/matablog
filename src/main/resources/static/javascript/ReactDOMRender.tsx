import { makePropTypes } from 'rbx/base/helpers/badge';
import React, { Suspense } from 'react';
import ReactDOM from 'react-dom';

const reactDomRender = (
    RenderedComponent: React.ComponentType<any>,
    elementId: string
) => {
    const ReactComponentContainer = document.getElementById(elementId);
    if (ReactComponentContainer) {
        const props = extractPropsFromHTMLElement(ReactComponentContainer);

        ReactDOM.render(
            <Suspense fallback={<div>Loading...</div>}>
                <RenderedComponent {...props} />
            </Suspense>,
            ReactComponentContainer
        );
    }
};

const extractPropsFromHTMLElement: any = (
    ReactComponentContainer: HTMLElement
) => {
    const propPrefix = 'data-';
    const propNames = ReactComponentContainer.getAttributeNames().filter(
        (name) => name.startsWith(propPrefix)
    );
    let props: any = {};
    propNames.forEach((propName) => {
        props[
            propName.substring(propPrefix.length, propName.length)
        ] = ReactComponentContainer.getAttribute(propName);
    });
    return props;
};

export default reactDomRender;
