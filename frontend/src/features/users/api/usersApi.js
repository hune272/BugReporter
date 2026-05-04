import { apiRequest } from '@shared/api/client.js';
import { ENDPOINTS } from '@shared/utils/constants.js';

export const usersApi = {
  getUsers(filters = {}) {
    const params = new URLSearchParams();

    if (filters.search) {
      params.set('search', filters.search);
    }

    if (filters.limit) {
      params.set('limit', filters.limit);
    }

    const query = params.toString();

    return apiRequest(`${ENDPOINTS.users}${query ? `?${query}` : ''}`);
  },

  getUserScores() {
    return apiRequest(ENDPOINTS.userScores);
  },
};
