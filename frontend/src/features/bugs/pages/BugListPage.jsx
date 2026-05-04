import { Link } from 'react-router-dom';
import { useState } from 'react';
import { useAuth } from '@features/auth/hooks/useAuth.js';
import { useVoteBug } from '@features/votes/hooks/useVoteBug.js';
import BugFeedCard from '../components/BugFeedCard.jsx';
import BugFeedControls from '../components/BugFeedControls.jsx';
import BugFeedPagination from '../components/BugFeedPagination.jsx';
import BugFeedSidebar from '../components/BugFeedSidebar.jsx';
import { useBugFeedFilters } from '../hooks/useBugFeedFilters.js';
import { useBugFeedMeta } from '../hooks/useBugFeedMeta.js';
import { useBugs } from '../hooks/useBugs.js';
import './BugListPage.css';

function BugListPage() {
  const { user } = useAuth();
  const voteBugMutation = useVoteBug();
  const [voteError, setVoteError] = useState('');
  const filters = useBugFeedFilters({ userId: user?.id });
  const { bugs, pageInfo, isLoading, errorMessage } = useBugs(filters.bugFilters);
  const { meta, feedBugs } = useBugFeedMeta(bugs);
  const controlState = filters.getControlState(meta);

  async function voteBug(bugId, type) {
    setVoteError('');
    const result = await voteBugMutation.mutateAsync({ bugId, type });
    if (!result.success) {
      setVoteError(result.error || 'Could not save your vote.');
    }
  }

  return (
    <div className="bug-feed-page">
      <header className="bug-feed-top">
        <h1>Bug Feed</h1>
        <BugFeedControls
          searchTerm={filters.searchTerm}
          onSearchChange={filters.updateSearchTerm}
          mineOnly={filters.mineOnly}
          onMineOnlyChange={filters.updateMineOnly}
          selectedUserId={filters.selectedUserId}
          selectedUserLabel={controlState.selectedUserLabel}
          isUserOpen={filters.isUserOpen}
          setIsUserOpen={filters.setIsUserOpen}
          userSearchTerm={filters.userSearchTerm}
          setUserSearchTerm={filters.setUserSearchTerm}
          visibleUsers={controlState.visibleUsers}
          onUserSelect={filters.selectUser}
          selectedTagId={filters.selectedTagId}
          selectedTagLabel={controlState.selectedTagLabel}
          isTagOpen={filters.isTagOpen}
          setIsTagOpen={filters.setIsTagOpen}
          tags={meta.tags}
          onTagSelect={filters.selectTag}
        />
      </header>

      <main className="bug-feed-grid">
        <section className="bug-feed-list" aria-label="Bug feed">
          {isLoading && <div className="bug-feed-state">Loading bugs...</div>}
          {errorMessage && <div className="bug-feed-state" role="alert">{errorMessage}</div>}
          {voteError && <div className="bug-feed-state" role="alert">{voteError}</div>}
          {!isLoading && feedBugs.map((bug) => (
            <BugFeedCard
              key={bug.id}
              bug={bug}
              isVoting={voteBugMutation.isPending}
              onVote={voteBug}
            />
          ))}
          {!isLoading && !errorMessage && feedBugs.length === 0 && (
            <div className="bug-feed-state">
              {filters.mineOnly ? 'No bugs reported by your account.' : 'No bugs found.'}
            </div>
          )}

          {!isLoading && !errorMessage && (
            <BugFeedPagination pageInfo={pageInfo} onPageChange={filters.setPage} />
          )}
        </section>

        <BugFeedSidebar
          meta={meta}
          selectedTagId={filters.selectedTagId}
          onTagSelect={filters.selectTag}
        />
      </main>

      <Link to="/bugs/new" className="bug-feed-floating" aria-label="Report new bug">
        <span aria-hidden="true">+</span>
      </Link>
    </div>
  );
}

export default BugListPage;
