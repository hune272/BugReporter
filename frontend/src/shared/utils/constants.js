// Vite proxies '/api/*' to the Spring backend (see vite.config.js),
// so an empty base lets the browser treat the API as same-origin.
export const API_BASE_URL = '';

export const ENDPOINTS = {
  // Auth
  register: '/api/auth/register',
  login: '/api/auth/login',
  logout: '/api/auth/logout',
  me: '/api/auth/me',

  // Bugs
  bugs: '/api/bugs',
  bug: (id) => `/api/bugs/${id}`,
  bugTags: (bugId) => `/api/bugs/${bugId}/tags`,
  bugTag: (bugId, tagId) => `/api/bugs/${bugId}/tags/${tagId}`,

  // Comments
  comments: '/api/comments',
  comment: (id) => `/api/comments/${id}`,
  commentsByBug: (bugId) => `/api/comments/bug/${bugId}`,

  // Votes
  voteBug: '/api/votes/bug',
  voteComment: '/api/votes/comment',
  bugVoteCount: (bugId) => `/api/votes/bug/${bugId}/count`,
  commentVoteCount: (commentId) => `/api/votes/comment/${commentId}/count`,

  // Tags
  tags: '/api/tags',
  tag: (id) => `/api/tags/${id}`,
  tagSearch: (name) => `/api/tags/search?name=${encodeURIComponent(name)}`,

  // Users
  users: '/api/users',
  user: (id) => `/api/users/${id}`,
};

// Mirrors backend enum: com.bug_reporter.backend.model.enums.UserRole
export const USER_ROLES = {
  USER: 'USER',
  MODERATOR: 'MODERATOR',
};

// Mirrors backend enum: com.bug_reporter.backend.model.enums.BugStatus
export const BUG_STATUS = {
  RECEIVED: 'RECEIVED',
  IN_PROGRESS: 'IN_PROGRESS',
  SOLVED: 'SOLVED',
};

// Mirrors backend enum: com.bug_reporter.backend.model.enums.VoteType
export const VOTE_TYPE = {
  UPVOTE: 'UPVOTE',
  DOWNVOTE: 'DOWNVOTE',
};
