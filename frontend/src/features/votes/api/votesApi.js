import { apiRequest } from '@shared/api/client.js';
import { ENDPOINTS } from '@shared/utils/constants.js';

export const votesApi = {
  voteBug({ bugId, type }) {
    return apiRequest(ENDPOINTS.voteBug, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ bugId, type }),
    });
  },

  voteComment({ commentId, type }) {
    return apiRequest(ENDPOINTS.voteComment, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ commentId, type }),
    });
  },
};
