export const authKeys = {
  root: ['auth'],
  me: ['auth', 'me'],
};

export const bugKeys = {
  root: ['bugs'],
  list: (filters) => ['bugs', filters],
  detail: (id) => ['bug', id],
};

export const tagKeys = {
  root: ['tags'],
  list: ['tags'],
  usage: ['tagUsage'],
};

export const userKeys = {
  root: ['users'],
  list: (filters) => ['users', filters],
  scores: ['userScores'],
};
