import { API_BASE_URL } from '../utils/constants.js';

async function safeReadError(response) {
  try {
    const data = await response.clone().json();
    return data?.message || data?.error || null;
  } catch {
    try {
      const text = await response.text();
      return text || null;
    } catch {
      return null;
    }
  }
}

async function safeReadJson(response) {
  try {
    return await response.json();
  } catch {
    return undefined;
  }
}

// Wraps fetch so every API call shares: same-origin base URL,
// session-cookie credentials, JSON Accept header, and a normalized
// { success, status, data?, error? } result instead of throwing.
export async function apiRequest(path, { method = 'GET', body, headers } = {}) {
  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      method,
      credentials: 'include',
      headers: {
        Accept: 'application/json',
        ...headers,
      },
      body,
    });

    if (response.ok) {
      return {
        success: true,
        status: response.status,
        data: await safeReadJson(response),
      };
    }

    const message = await safeReadError(response);
    return {
      success: false,
      status: response.status,
      error: message || `Request failed (HTTP ${response.status}).`,
    };
  } catch (networkError) {
    return {
      success: false,
      status: 0,
      error: networkError?.message || 'Network error.',
    };
  }
}
