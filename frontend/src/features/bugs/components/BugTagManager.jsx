import './BugTagManager.css';

function BugTagManager({
  tags,
  selectedTags,
  selectedExistingTag,
  selectedExistingTagId,
  isTagMenuOpen,
  newTagName,
  onMenuToggle,
  onExistingTagSelect,
  onExistingTagAdd,
  onNewTagNameChange,
  onNewTagCreate,
  onSelectedTagRemove,
}) {
  return (
    <div className="bug-report-tags">
      <div className="bug-report-tags__header">
        <span>Tag management</span>
        <div>
          {selectedTags.map((tag) => (
            <button key={tag.id} type="button" onClick={() => onSelectedTagRemove(tag.id)}>
              {tag.name} x
            </button>
          ))}
        </div>
      </div>

      <div className="bug-report-tags__controls">
        <div className="bug-report-tag-picker">
          <button
            type="button"
            className="bug-report-tag-picker__trigger"
            aria-expanded={isTagMenuOpen}
            onClick={onMenuToggle}
          >
            {selectedExistingTag?.name ?? 'Select tag...'}
            <span className="bug-report-tag-picker__chevron" aria-hidden="true" />
          </button>

          {isTagMenuOpen && (
            <div className="bug-report-tag-picker__menu">
              <div className="bug-report-tag-picker__list">
                {tags.map((tag) => (
                  <button
                    key={tag.id}
                    type="button"
                    className={String(selectedExistingTagId) === String(tag.id) ? 'is-selected' : ''}
                    onClick={() => onExistingTagSelect(tag.id)}
                  >
                    {tag.name}
                  </button>
                ))}
              </div>

              <div className="bug-report-tag-picker__create">
                <span>Create tag</span>
                <div>
                  <input
                    value={newTagName}
                    onChange={(event) => onNewTagNameChange(event.target.value)}
                    onKeyDown={(event) => {
                      if (event.key === 'Enter') {
                        event.preventDefault();
                        onNewTagCreate();
                      }
                    }}
                    placeholder="New tag name..."
                  />
                  <button type="button" onClick={onNewTagCreate} disabled={!newTagName.trim()}>
                    Create
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
        <button
          type="button"
          onClick={onExistingTagAdd}
          disabled={!selectedExistingTagId}
        >
          Add
        </button>
      </div>
    </div>
  );
}

export default BugTagManager;
