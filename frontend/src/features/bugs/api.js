import { apiRequest } from '@shared/api/client.js';

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

        const query = params.toString();

        return apiRequest(`/api/bugs${query ? `?${query}` : ''}`);
    },

    getBugById(id) {
        return apiRequest(`/api/bugs/${id}`);
    },

    createBug(data) {
        return apiRequest('/api/bugs', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });
    },

    updateBug(id, data) {
        return apiRequest(`/api/bugs/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });
    },

    deleteBug(id) {
        return apiRequest(`/api/bugs/${id}`, {
            method: 'DELETE',
        });
    },

    getBugTags(id) {
        return apiRequest(`/api/bugs/${id}/tags`);
    },

    addTagToBug(bugId, tagId) {
        return apiRequest(`/api/bugs/${bugId}/tags/${tagId}`, {
            method: 'POST',
        });
    },

    removeAllBugTags(bugId) {
        return apiRequest(`/api/bugs/${bugId}/tags`, {
            method: 'DELETE',
        });
    },

    getBugComments(id) {
        return apiRequest(`/api/comments/bug/${id}`);
    },

    getBugVoteCount(id) {
        return apiRequest(`/api/votes/bug/${id}/count`);
    },

    getTags() {
        return apiRequest('/api/tags');
    },

    createTag(data) {
        return apiRequest('/api/tags', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });
    },

    getUsers(filters = {}) {
        const params = new URLSearchParams();

        if (filters.search) {
            params.set('search', filters.search);
        }

        if (filters.limit) {
            params.set('limit', filters.limit);
        }

        const query = params.toString();

        return apiRequest(`/api/users${query ? `?${query}` : ''}`);
    },

    getUserScores() {
        return apiRequest('/api/users/scores');
    },
};
