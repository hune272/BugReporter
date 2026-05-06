import { apiRequest } from '@shared/api/client.js';
import { ENDPOINTS } from '@shared/utils/constants.js';

export const bugsApi = {
  getBugs(filters = {}) {
    const params = new URLSearchParams();

    if (filters.title) {
      params.set('title', filters.title);
    }

    if (filters.authorId) {
      params.set('authorId', filters.authorId);
    }

    if (filters.tagId) {
      params.set('tagId', filters.tagId);
    }

    params.set('page', filters.page ?? 0);
    params.set('size', filters.size ?? 10);

    return apiRequest(`${ENDPOINTS.bugs}?${params.toString()}`);
  },

  getBugById(id) {
    return apiRequest(ENDPOINTS.bug(id));
  },

  createBug(data) {
    return apiRequest(ENDPOINTS.bugs, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
  },

  updateBug(id, data) {
    return apiRequest(ENDPOINTS.bug(id), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
  },

  deleteBug(id) {
    return apiRequest(ENDPOINTS.bug(id), {
      method: 'DELETE',
    });
  },

  createComment(bugId, commentText, imageUrl) {
    return apiRequest(`/api/comments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                comment: commentText,
                bugId: bugId,
                imageUrl: imageUrl
            }),
        });
  },

  deleteComment(bugId, commentId) {
      return apiRequest(`/api/comments/${commentId}`, {
              method: 'DELETE',
            });
    },

  updateComment(commentId, commentText) {
      return apiRequest(`/api/comments/${commentId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ comment: commentText }),
      });
    },

  voteComment(bugId, commentId, voteType) {
      return apiRequest(`/api/votes/comment`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                commentId: commentId,
                type: voteType
            }),
          });
    },

  voteBug(bugId, voteType) {
    return apiRequest(`${ENDPOINTS.bugs}/${bugId}/vote`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ type: voteType }),
        });
  }

};
