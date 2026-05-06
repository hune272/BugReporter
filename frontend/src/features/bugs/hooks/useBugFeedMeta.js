import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { tagKeys, userKeys } from '@shared/api/queryKeys.js';
import { tagsApi } from '@features/tags/api/tagsApi.js';
import { usersApi } from '@features/users/api/usersApi.js';

const EMPTY_META = {
  tags: [],
  tagCounts: [],
  users: [],
  userScores: {},
  topHunters: [],
};

async function loadTagUsage() {
  const result = await tagsApi.getTagUsage();
  if (!result.success) {
    throw new Error(result.error || 'Could not load tag usage.');
  }
  return result.data ?? [];
}

async function loadUsers() {
  const result = await usersApi.getUsers({ limit: 100 });
  if (!result.success) {
    throw new Error(result.error || 'Could not load users.');
  }
  return result.data ?? [];
}

async function loadUserScores() {
  const result = await usersApi.getUserScores();
  if (!result.success) {
    throw new Error(result.error || 'Could not load user scores.');
  }
  return result.data ?? {};
}

function normalizeBug(bug, meta) {
  return {
    ...bug,
    votes: bug.voteCount ?? 0,
    comments: bug.comments ?? [],
    author: {
      ...bug.author,
      score: bug.author?.score ?? meta.userScores[bug.author?.id] ?? 0,
    },
    tags: (bug.tags ?? []).map((tag) => tag.name.toUpperCase()),
    tagIds: (bug.tags ?? []).map((tag) => tag.id),
  };
}

export function useBugFeedMeta(bugs) {
  const tagsQuery = useQuery({
    queryKey: tagKeys.usage,
    queryFn: loadTagUsage,
    staleTime: 5 * 60_000,
  });

  const usersQuery = useQuery({
    queryKey: userKeys.list({ limit: 100 }),
    queryFn: loadUsers,
    staleTime: 60_000,
  });

  const scoresQuery = useQuery({
    queryKey: userKeys.scores,
    queryFn: loadUserScores,
    staleTime: 30_000,
  });

  const meta = useMemo(() => {
    const allTags = tagsQuery.data ?? EMPTY_META.tags;
    const users = usersQuery.data ?? EMPTY_META.users;
    const userScores = scoresQuery.data ?? EMPTY_META.userScores;

    const solvedByUser = {};
    bugs.forEach((bug) => {
      if (bug.status === 'SOLVED' && bug.author?.id) {
        solvedByUser[bug.author.id] = (solvedByUser[bug.author.id] ?? 0) + 1;
      }
    });

    return {
      tags: allTags
        .map((tag) => ({ id: tag.id, name: tag.name }))
        .sort((a, b) => a.name.localeCompare(b.name)),
      tagCounts: allTags
        .map((tag) => ({ id: tag.id, name: tag.name, count: tag.count ?? 0 }))
        .sort((a, b) => b.count - a.count || a.name.localeCompare(b.name))
        .slice(0, 6),
      users: users
        .map((item) => ({ id: item.id, username: item.username, email: item.email }))
        .sort((a, b) => a.username.localeCompare(b.username)),
      userScores,
      topHunters: users
        .map((item) => ({
          id: item.id,
          username: item.username,
          score: userScores[item.id] ?? 0,
          solved: solvedByUser[item.id] ?? 0,
        }))
        .sort((a, b) => b.score - a.score || b.solved - a.solved)
        .slice(0, 3),
    };
  }, [bugs, scoresQuery.data, tagsQuery.data, usersQuery.data]);

  const feedBugs = useMemo(() => {
    const normalized = bugs.map((bug) => normalizeBug(bug, meta));

    return [...normalized].sort((a, b) => {
      const aDate = new Date(a.createdAt).getTime();
      const bDate = new Date(b.createdAt).getTime();
      return bDate - aDate;
    });
  }, [bugs, meta]);

  return { meta, feedBugs };
}
