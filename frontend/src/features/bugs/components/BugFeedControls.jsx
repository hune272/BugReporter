import './BugFeedControls.css';
import SearchInput from '@shared/components/inputs/SearchInput.jsx';

function BugFeedControls({
  searchTerm,
  onSearchChange,
  mineOnly,
  onMineOnlyChange,
  selectedUserId,
  selectedUserLabel,
  isUserOpen,
  setIsUserOpen,
  userSearchTerm,
  setUserSearchTerm,
  visibleUsers,
  onUserSelect,
  selectedTagId,
  selectedTagLabel,
  isTagOpen,
  setIsTagOpen,
  tags,
  onTagSelect,
}) {
  return (
    <div className="bug-feed-controls">
      <SearchInput
        className="bug-feed-search"
        value={searchTerm}
        onChange={onSearchChange}
        placeholder="Search bug titles..."
        ariaLabel="Search bugs"
      />

      <label className="bug-feed-toggle">
        <span>My Bugs</span>
        <input
          type="checkbox"
          checked={mineOnly}
          onChange={(event) => onMineOnlyChange(event.target.checked)}
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
            <SearchInput
              className="bug-user-menu__search"
              value={userSearchTerm}
              onChange={setUserSearchTerm}
              placeholder="Search users..."
              ariaLabel="Search users"
            />
            <button
              type="button"
              className={selectedUserId === 'all' ? 'is-selected' : ''}
              onClick={() => onUserSelect('all')}
            >
              All Users
            </button>
            {visibleUsers.map((item) => (
              <button
                key={item.id}
                type="button"
                className={String(selectedUserId) === String(item.id) ? 'is-selected' : ''}
                onClick={() => onUserSelect(item.id)}
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
              onClick={() => onTagSelect('all')}
            >
              All Tags
            </button>
            {tags.map((tag) => (
              <button
                key={tag.id}
                type="button"
                className={String(selectedTagId) === String(tag.id) ? 'is-selected' : ''}
                onClick={() => onTagSelect(tag.id)}
              >
                {tag.name}
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default BugFeedControls;
