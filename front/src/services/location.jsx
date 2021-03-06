import queryString from 'query-string';
import { updateSessionInfo, isAuthenticated } from 'services/session.jsx';
import { getViewComponent } from 'services/view-components.jsx';
import views from 'constants/views.json';

/**
 * Sets the location change listener up.
 */
export function listenLocationChange() {

    history().listen(handleLocationChange);
    handleLocationChange(location());

}

function handleLocationChange(location) {

    var path = location.pathname;

    updateSessionInfo({
        callback: () => {

            if (path === '/') {
                changeLocation(isAuthenticated() ? views.defaultPath : views.viewPaths.login);
            } else if (path === views.viewPaths.login && isAuthenticated()) {
                changeLocation(views.defaultPath);
            } else {
                handleLocationChangeToPath(path);
            }

        }
    });

}

function handleLocationChangeToPath(path) {

    for (var view in views.viewPaths) {
        if (views.viewPaths[view] === path) {
            var component = getViewComponent(view);
            if (component && component.handleLocationChange) component.handleLocationChange();
            return;
        }
    }

}

/**
 * @returns {string} The current location path.
 */
export function currentLocationPath() {

    return location().pathname;

}

/**
 * @returns {object} The current location parameters.
 */
export function currentLocationParams() {

    return queryString.parse(location().search);

}

/**
 * @param {string} path A location path.
 * @returns {boolean} Boolean indicating if the location path is public.
 */
export function isLocationPublic(path) {

    return views.publicPaths.indexOf(path) >= 0;

}

/**
 * Changes the current location path.
 * 
 * @param {string} path The new path.
 */
export function changeLocation(path) {

    if (path !== currentLocationPath()) {
        history().push(path);
    }

}

function history() {

    return getViewComponent('app').props.history;

}

function location() {

    return history().location;

}
