// Vite proxies '/api/*' to the Spring backend (see vite.config.js),
// so an empty base lets the browser treat the API as same-origin.
export const API_BASE_URL = '';

export const ENDPOINTS = {
  login: '/api/auth/login',
  logout: '/api/auth/logout',
  me: '/api/auth/me',
};