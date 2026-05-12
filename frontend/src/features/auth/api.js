import { apiRequest } from '@shared/api/client.js';
import { ENDPOINTS } from '@shared/utils/constants.js';

// Switch to 'application/x-www-form-urlencoded' if the backend uses Spring
// Security's default form-login filter instead of a JSON-aware controller.
const LOGIN_CONTENT_TYPE = 'application/json';

function buildLoginBody(email, password) {
  if (LOGIN_CONTENT_TYPE === 'application/x-www-form-urlencoded') {
    const params = new URLSearchParams();
    params.append('email', email);
    params.append('password', password);
    return params.toString();
  }
  return JSON.stringify({ email, password });
}

export const authApi = {
  async register({ username, email, password }) {
    const result = await apiRequest(ENDPOINTS.register, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password }),
    });
    if (result.success) {
      return { success: true, status: result.status, user: result.data };
    }
    return result;
  },

  async login({ email, password }) {
    const result = await apiRequest(ENDPOINTS.login, {
      method: 'POST',
      headers: { 'Content-Type': LOGIN_CONTENT_TYPE },
      body: buildLoginBody(email, password),
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
