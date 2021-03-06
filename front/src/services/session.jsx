import { getViewComponent } from 'services/view-components.jsx';
import { rest } from 'services/rest.jsx';
import { changeLocation, currentLocationPath, isLocationPublic } from 'services/location.jsx';
import { areSessionKeysGenerated, deleteSessionKeys } from 'services/crypto/crypto.jsx';
import { loading } from 'services/loading.jsx';
import views from 'constants/views.json';

var currentSession = null;

/**
 * Updates the current session info.
 * 
 * @param {object} options The options.
 * @param {boolean} [options.loading = true] Set the loading state while the session info is being updated.
 * @param {boolean} [options.loadingChain = false] Indicates if the loading state should not be deactivated once the session info has been updated.
 * @param {boolean} [options.loadingChained = false] Indicates if the loading state was already active.
 * @param {function} [options.callback] Invoked once the session info has been updated.
 */
export function updateSessionInfo({
    loading = false,
    loadingChain = false,
    loadingChained = false,
    callback
}) {

    rest({
        method: 'get',
        url: '/api/session',
        loading: loading,
        loadingChain: loadingChain,
        loadingChained: loadingChained,
        callback: (response) => {

            var previousSession = currentSession || {};
            currentSession = response;

            if (!isLocationPublic(currentLocationPath())
                && (!isAuthenticated() || (currentSession.email !== previousSession.email))) {
                reset();
                return;
            }

            if (callback) callback();

        }
    });

}

/**
 * @returns {boolean} Indicates if the current session is authenticated.
 */
export function isAuthenticated() {

    return Boolean(currentSession)
        && currentSession.authenticated
        && areSessionKeysGenerated();

}

/**
 * @returns {string} Indicates the current session email, in case it is authenticated, or null otherwise.
 */
export function sessionEmail() {

    return currentSession ? currentSession.email : null;

}

/**
 * Terminates the current session.
 */
export function logout() {

    rest({
        method: 'post',
        url: '/api/session/logout',
        callback: (response) => {

            reset();

        }
    });

}

/**
 * Resets the webpage.
 */
export function reset() {

    loading(() => {
        window.location = '/';
    });

}
