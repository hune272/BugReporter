import './BugFeedCard.css';

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

function isDisplayableImage(src) {
  return typeof src === 'string' && src.trim().length > 0;
}

function BugFeedCard({ bug, isVoting, onVote }) {
  return (
    <article className="bug-card">
      <aside className="bug-card__votes">
        <button
          type="button"
          aria-label="Upvote"
          disabled={isVoting}
          onClick={() => onVote(bug.id, 'UPVOTE')}
        >
          ⌃
        </button>
        <strong>{bug.votes}</strong>
        <button
          type="button"
          aria-label="Downvote"
          disabled={isVoting}
          onClick={() => onVote(bug.id, 'DOWNVOTE')}
        >
          ⌄
        </button>
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

export default BugFeedCard;
