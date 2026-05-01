import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { bugsApi } from '../api.js';
import { useBugs } from '../hooks/useBugs.js';
import { useAuth } from '@features/auth/hooks/useAuth.js';
import './BugListPage.css';

function statusLabel(status) {
  return {
    RECEIVED: 'RECEIVED',
    IN_PROGRESS: 'IN PROGRESS',
    SOLVED: 'SOLVED',
  }[status] ?? 'RECEIVED';
}

function statusClass(status) {
  return (status ?? 'RECEIVED').toLowerCase();
}

function formatRelativeDate(value) {
  if (!value) return 'recently';
  const hours = Math.max(1, Math.round((Date.now() - new Date(value).getTime()) / 3600000));
  return hours < 24 ? `${hours} hours ago` : `${Math.round(hours / 24)} day ago`;
}

function normalizeBug(bug, meta) {
  return {
    ...bug,
    votes: meta.votesByBug[bug.id] ?? 0,
    comments: meta.commentsByBug[bug.id] ?? 0,
    author: {
      ...bug.author,
      score: meta.userScores[bug.author?.id] ?? 0,
    },
    tags: meta.tagsByBug[bug.id] ?? [],
    tagIds: meta.tagIdsByBug[bug.id] ?? [],
  };
}

function isDisplayableImage(src) {
  return typeof src === 'string' && src.trim().length > 0;
}

function BugFeedCard({ bug }) {
  return (
    <article className="bug-card">
      <aside className="bug-card__votes">
        <button type="button" aria-label="Upvote">⌃</button>
        <strong>{bug.votes}</strong>
        <button type="button" aria-label="Downvote">⌄</button>
      </aside>

      <div className="bug-card__body">
        <header className="bug-card__header">
          <div>
            <p>
              <strong>{bug.author.username} ({bug.author.score} pts)</strong>
              <span>•</span>
              <span>{formatRelativeDate(bug.createdAt)}</span>
            </p>
            <h2>{bug.title}</h2>
          </div>
          <span className={`bug-card__status bug-card__status--${statusClass(bug.status)}`}>
            {statusLabel(bug.status)}
          </span>
        </header>

        <p className="bug-card__text">{bug.text}</p>

        {isDisplayableImage(bug.picture) && (
          <img className="bug-card__image" src={bug.picture} alt="" loading="lazy" />
        )}

        {!bug.picture && bug.hasCodeImage && (
          <div className="bug-card__image bug-card__image--code" aria-hidden="true">
            {Array.from({ length: 14 }).map((_, index) => (
              <span key={index} />
            ))}
          </div>
        )}

        {!bug.picture && bug.errorLog && (
          <pre className="bug-card__log">{`[ERROR] 2023-10-27 14:22:01 - Gateway Timeout
POST /api/v1/export HTTP/1.1
Host: api.bugtrac.prod
Status: 504 (Request exceeded 30s limit)`}</pre>
        )}

        <div className="bug-card__tags">
          {bug.tags.map((tag) => (
            <span key={tag}>{tag}</span>
          ))}
        </div>

        <footer className="bug-card__footer">
          <span>▱ {bug.comments} Comments</span>
        </footer>
      </div>
    </article>
  );
}

function BugListPage() {
  const { user } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTagId, setSelectedTagId] = useState('all');
  const [isTagOpen, setIsTagOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState('all');
  const [isUserOpen, setIsUserOpen] = useState(false);
  const [userSearchTerm, setUserSearchTerm] = useState('');
  const [mineOnly, setMineOnly] = useState(false);
  const bugFilters = useMemo(() => {
    const filters = {};
    const title = searchTerm.trim();

    if (title) {
      filters.title = title;
    }

    if (selectedTagId !== 'all') {
      filters.tagId = selectedTagId;
    }

    if (mineOnly && user?.id) {
      filters.authorId = user.id;
    } else if (selectedUserId !== 'all') {
      filters.authorId = selectedUserId;
    }

    return filters;
  }, [mineOnly, searchTerm, selectedTagId, selectedUserId, user?.id]);
  const { bugs, isLoading, errorMessage } = useBugs(bugFilters);
  const [meta, setMeta] = useState({
    commentsByBug: {},
    tagIdsByBug: {},
    tagsByBug: {},
    tags: [],
    tagCounts: [],
    users: [],
    userScores: {},
    topHunters: [],
    votesByBug: {},
  });

  useEffect(() => {
    let cancelled = false;

    async function loadMeta() {
      if (bugs.length === 0) {
        const [usersResult, allTagsResult] = await Promise.all([
          bugsApi.getUsers({ limit: 100 }),
          bugsApi.getTags(),
        ]);

        if (cancelled) return;

        setMeta({
          commentsByBug: {},
          tagIdsByBug: {},
          tagsByBug: {},
          tags: usersResult.success || allTagsResult.success
            ? (allTagsResult.data ?? []).sort((a, b) => a.name.localeCompare(b.name))
            : [],
          tagCounts: (allTagsResult.data ?? [])
            .map((tag) => ({ id: tag.id, name: tag.name, count: 0 }))
            .sort((a, b) => a.name.localeCompare(b.name))
            .slice(0, 6),
          users: usersResult.success
            ? (usersResult.data ?? []).sort((a, b) => a.username.localeCompare(b.username))
            : [],
          userScores: {},
          topHunters: [],
          votesByBug: {},
        });
        return;
      }

      const [userScoresResult, usersResult, allTagsResult, voteCountResults, commentsResults, tagResults] =
        await Promise.all([
          bugsApi.getUserScores(),
          bugsApi.getUsers({ limit: 100 }),
          bugsApi.getTags(),
          Promise.all(bugs.map((bug) => bugsApi.getBugVoteCount(bug.id))),
          Promise.all(bugs.map((bug) => bugsApi.getBugComments(bug.id))),
          Promise.all(bugs.map((bug) => bugsApi.getBugTags(bug.id))),
        ]);

      if (cancelled) return;

      const userScores = userScoresResult.success ? userScoresResult.data ?? {} : {};
      const users = usersResult.success ? usersResult.data ?? [] : [];
      const allTags = allTagsResult.success ? allTagsResult.data ?? [] : [];

      const votesByBug = {};
      voteCountResults.forEach((result, index) => {
        votesByBug[bugs[index].id] = result.success ? result.data ?? 0 : 0;
      });

      const commentsByBug = {};
      commentsResults.forEach((result, index) => {
        commentsByBug[bugs[index].id] = result.success ? result.data?.length ?? 0 : 0;
      });

      const tagIdsByBug = {};
      const tagsByBug = {};
      const tagCountMap = new Map();
      tagResults.forEach((result, index) => {
        const bugId = bugs[index].id;
        const tags = result.success ? result.data ?? [] : [];
        tagIdsByBug[bugId] = tags.map((tag) => tag.id);
        tagsByBug[bugId] = tags.map((tag) => tag.name.toUpperCase());
        tags.forEach((tag) => {
          const current = tagCountMap.get(tag.id) ?? { id: tag.id, name: tag.name, count: 0 };
          tagCountMap.set(tag.id, { ...current, count: current.count + 1 });
        });
      });

      allTags.forEach((tag) => {
        if (!tagCountMap.has(tag.id)) {
          tagCountMap.set(tag.id, { id: tag.id, name: tag.name, count: 0 });
        }
      });

      const solvedByUser = {};
      bugs.forEach((bug) => {
        if (bug.status === 'SOLVED' && bug.author?.id) {
          solvedByUser[bug.author.id] = (solvedByUser[bug.author.id] ?? 0) + 1;
        }
      });

      const topHunters = users
        .map((user) => ({
          id: user.id,
          username: user.username,
          score: userScores[user.id] ?? 0,
          solved: solvedByUser[user.id] ?? 0,
        }))
        .sort((a, b) => b.score - a.score || b.solved - a.solved)
        .slice(0, 3);

      setMeta({
        commentsByBug,
        tagIdsByBug,
        tagsByBug,
        tags: allTags
          .map((tag) => ({ id: tag.id, name: tag.name }))
          .sort((a, b) => a.name.localeCompare(b.name)),
        tagCounts: [...tagCountMap.values()]
          .sort((a, b) => b.count - a.count || a.name.localeCompare(b.name))
          .slice(0, 6),
        users: users
          .map((user) => ({ id: user.id, username: user.username, email: user.email }))
          .sort((a, b) => a.username.localeCompare(b.username)),
        userScores,
        topHunters,
        votesByBug,
      });
    }

    loadMeta();

    return () => {
      cancelled = true;
    };
  }, [bugs]);

  const feedBugs = useMemo(() => {
    const normalized = bugs.map((bug) => normalizeBug(bug, meta));

    return [...normalized].sort((a, b) => {
      const aDate = new Date(a.createdAt).getTime();
      const bDate = new Date(b.createdAt).getTime();
      return bDate - aDate;
    });
  }, [bugs, meta]);

  const selectedTag = meta.tags.find((tag) => String(tag.id) === String(selectedTagId));
  const selectedTagLabel = selectedTagId === 'all' ? 'All Tags' : selectedTag?.name ?? 'All Tags';
  const selectedUser = meta.users.find((item) => String(item.id) === String(selectedUserId));
  const selectedUserLabel = mineOnly
    ? 'My Bugs'
    : selectedUserId === 'all'
      ? 'All Users'
      : selectedUser?.username ?? 'All Users';
  const visibleUsers = useMemo(() => {
    const query = userSearchTerm.trim().toLowerCase();
    if (!query) return meta.users;

    return meta.users.filter((item) =>
      [item.username, item.email].filter(Boolean).some((value) =>
        value.toLowerCase().includes(query),
      ),
    );
  }, [meta.users, userSearchTerm]);

  return (
    <div className="bug-feed-page">
      <header className="bug-feed-top">
        <h1>Bug Feed</h1>
        <div className="bug-feed-controls">
          <label className="bug-feed-search">
            <span aria-hidden="true" />
            <input
              type="search"
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
              placeholder="Search bug titles..."
              aria-label="Search bugs"
            />
          </label>
          <label className="bug-feed-toggle">
            <span>My Bugs</span>
            <input
              type="checkbox"
              checked={mineOnly}
              onChange={(event) => {
                setMineOnly(event.target.checked);
                if (event.target.checked) {
                  setSelectedUserId('all');
                  setIsUserOpen(false);
                }
              }}
            />
            <span className="bug-feed-toggle__switch" aria-hidden="true" />
          </label>
          <div className="bug-user-menu">
            <span>User</span>
            <button
              type="button"
              className="bug-sort-menu__trigger"
              aria-expanded={isUserOpen}
              disabled={mineOnly}
              onClick={() => {
                setUserSearchTerm('');
                setIsUserOpen((isOpen) => !isOpen);
              }}
            >
              {selectedUserLabel}
              <span className="bug-sort-menu__chevron" aria-hidden="true" />
            </button>

            {isUserOpen && !mineOnly && (
              <div className="bug-sort-menu__list" role="menu">
                <label className="bug-user-menu__search">
                  <span aria-hidden="true" />
                  <input
                    type="search"
                    value={userSearchTerm}
                    onChange={(event) => setUserSearchTerm(event.target.value)}
                    placeholder="Search users..."
                    aria-label="Search users"
                  />
                </label>
                <button
                  type="button"
                  className={selectedUserId === 'all' ? 'is-selected' : ''}
                  onClick={() => {
                    setSelectedUserId('all');
                    setUserSearchTerm('');
                    setIsUserOpen(false);
                  }}
                >
                  All Users
                </button>
                {visibleUsers.map((item) => (
                  <button
                    key={item.id}
                    type="button"
                    className={String(selectedUserId) === String(item.id) ? 'is-selected' : ''}
                    onClick={() => {
                      setSelectedUserId(item.id);
                      setUserSearchTerm('');
                      setIsUserOpen(false);
                    }}
                  >
                    {item.username}
                  </button>
                ))}
                {visibleUsers.length === 0 && (
                  <p className="bug-user-menu__empty">No users found.</p>
                )}
              </div>
            )}
          </div>
          <div className="bug-tag-menu">
            <span>Tag</span>
            <button
              type="button"
              className="bug-sort-menu__trigger"
              aria-expanded={isTagOpen}
              onClick={() => setIsTagOpen((isOpen) => !isOpen)}
            >
              {selectedTagLabel}
              <span className="bug-sort-menu__chevron" aria-hidden="true" />
            </button>

            {isTagOpen && (
              <div className="bug-sort-menu__list" role="menu">
                <button
                  type="button"
                  className={selectedTagId === 'all' ? 'is-selected' : ''}
                  onClick={() => {
                    setSelectedTagId('all');
                    setIsTagOpen(false);
                  }}
                >
                  All Tags
                </button>
                {meta.tags.map((tag) => (
                  <button
                    key={tag.id}
                    type="button"
                    className={String(selectedTagId) === String(tag.id) ? 'is-selected' : ''}
                    onClick={() => {
                      setSelectedTagId(tag.id);
                      setIsTagOpen(false);
                    }}
                  >
                    {tag.name}
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>
      </header>

      <main className="bug-feed-grid">
        <section className="bug-feed-list" aria-label="Bug feed">
          {isLoading && <div className="bug-feed-state">Loading bugs...</div>}
          {errorMessage && <div className="bug-feed-state" role="alert">{errorMessage}</div>}
          {!isLoading && feedBugs.map((bug) => <BugFeedCard key={bug.id} bug={bug} />)}
          {!isLoading && !errorMessage && feedBugs.length === 0 && (
            <div className="bug-feed-state">
              {mineOnly ? 'No bugs reported by your account.' : 'No bugs found.'}
            </div>
          )}
        </section>

        <aside className="bug-feed-sidebar">
          <section className="bug-panel">
            <h2>Trending Tags</h2>
            <ul className="tag-list">
              {meta.tagCounts.map((tag) => (
                <li key={tag.id}>
                  <button
                    type="button"
                    className={String(selectedTagId) === String(tag.id) ? 'is-selected' : ''}
                    onClick={() => setSelectedTagId(tag.id)}
                  >
                    {tag.name}
                  </button>
                  <strong>{tag.count}</strong>
                </li>
              ))}
            </ul>
          </section>

          <section className="bug-panel">
            <h2>Top Bug Hunters</h2>
            <div className="hunter-list">
              {meta.topHunters.map((hunter) => (
                <p key={hunter.id}>
                  <span>{hunter.username.slice(0, 2).toUpperCase()}</span>
                  <b>{hunter.username}</b>
                  <small>{hunter.score} pts • {hunter.solved} Solved</small>
                </p>
              ))}
            </div>
          </section>
        </aside>
      </main>

      <Link to="/bugs/new" className="bug-feed-floating" aria-label="Report new bug">
        <span aria-hidden="true">+</span>
      </Link>
    </div>
  );
}

export default BugListPage;
