import { apiRequest } from './client.js';
import { ENDPOINTS } from '../utils/constants.js';

// Switch to 'application/x-www-form-urlencoded' if the backend uses Spring
// Security's default form-login filter instead of a JSON-aware controller.
const LOGIN_CONTENT_TYPE = 'application/json';

function buildLoginBody(username, password) {
  if (LOGIN_CONTENT_TYPE === 'application/x-www-form-urlencoded') {
    const params = new URLSearchParams();
    params.append('username', username);
    params.append('password', password);
    return params.toString();
  }
  return JSON.stringify({ username, password });
}

export const authApi = {
  async login({ username, password }) {
    const result = await apiRequest(ENDPOINTS.login, {
      method: 'POST',
      headers: { 'Content-Type': LOGIN_CONTENT_TYPE },
      body: buildLoginBody(username, password),
    });
    if (result.success) {
      return { success: true, status: result.status, user: result.data };
    }
    return result;
  },

  async checkSession() {
    const result = await apiRequest(ENDPOINTS.me);
    if (result.success) {
      return { authenticated: true, user: result.data };
    }
    return { authenticated: false };
  },

  async logout() {
    // Best-effort; ignore any failures.
    await apiRequest(ENDPOINTS.logout, { method: 'POST' });
  },
};

export const __testing__ = { LOGIN_CONTENT_TYPE };
