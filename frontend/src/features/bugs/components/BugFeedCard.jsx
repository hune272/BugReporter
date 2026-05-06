import './BugFeedCard.css';
import { useState } from 'react';
import BugComment from './BugComment';
import { bugsApi } from '../api/bugsApi';
import { useQueryClient } from '@tanstack/react-query';
import { bugKeys } from '@shared/api/queryKeys.js';

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
    const [commentText, setCommentText] = useState('');
    const [imageUrl, setImageUrl] = useState('');

    const queryClient = useQueryClient();

    const [isImageModalOpen, setIsImageModalOpen] = useState(false);
    const [tempUrl, setTempUrl] = useState('');

    const [sortType, setSortType] = useState('NEWEST');

     const handlePostComment = async () => {
         if (!commentText.trim() || !imageUrl.trim()) {
             alert("Atat textul, cat și imaginea sunt obligatorii!");
             return;
         }

         try {
             const finalImageUrl = imageUrl.trim().length > 0 ? imageUrl.trim() : null;
             const result = await bugsApi.createComment(bug.id, commentText, finalImageUrl);

             if(result.success){
                 setCommentText('');
                 setImageUrl('');
                 alert("Comentariu adaugat cu succes!");
                 queryClient.invalidateQueries({ queryKey: bugKeys.root });
             }else{
                 alert(result.error || "Eroare la comunicarea cu serverul.");
             }
         } catch (error) {
           console.error("Eroare la trimiterea comentariului:", error);
           alert("Eroare la comunicarea cu serverul.");
         }
       };

    const openModal = () => {
            setTempUrl(imageUrl);
            setIsImageModalOpen(true);
        };
    const saveModal = () => {
            setImageUrl(tempUrl);
            setIsImageModalOpen(false);
        }
    const closeModal = () => {
            setTempUrl('');
            setIsImageModalOpen(false);
        };
    const removeAttachment = () => {
            setImageUrl('');
        };

    const statusClass = (status) => (status ?? 'RECEIVED').toLowerCase();
        const formatRelativeDate = (value) => {
        if (!value) return 'recently';
            const hours = Math.max(1, Math.round((Date.now() - new Date(value).getTime()) / 3600000));
        return hours < 24 ? `${hours} hours ago` : `${Math.round(hours / 24)} day ago`;
    };

    const sortedComments = bug.comments ? [...bug.comments].sort((a, b) => {
            switch (sortType) {
                case 'HIGHEST_VOTES':
                    return (b.voteCount || 0) - (a.voteCount || 0);
                case 'LOWEST_VOTES':
                    return (a.voteCount || 0) - (b.voteCount || 0);
                case 'NEWEST':
                    return new Date(b.createdAt) - new Date(a.createdAt);
                case 'OLDEST':
                    return new Date(a.createdAt) - new Date(b.createdAt);
                default:
                    return new Date(b.createdAt) - new Date(a.createdAt);
            }
        }) : [];

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
            <span>▱ {bug.comments?.length || 0} Comments</span>
        </footer>

        <div className="bug-discussion-section">
            <hr className="discussion-divider" />
                <div className="discussion-header">
                    <h3>Discussion</h3>
                        <div className="discussion-sort">
                            <label htmlFor="sort-comments" style={{ color: '#6b7280', fontSize: '0.9rem', marginRight: '4px' }}>Sort by:</label>
                            <select
                                id="sort-comments"
                                value={sortType}
                                onChange={(e) => setSortType(e.target.value)}
                                style={{ fontWeight: 'bold', border: 'none', background: 'transparent', outline: 'none', cursor: 'pointer', fontSize: '0.95rem', color: '#111827' }}
                            >
                                <option value="NEWEST">Newest</option>
                                <option value="OLDEST">Oldest</option>
                                <option value="HIGHEST_VOTES">Highest Votes</option>
                                <option value="LOWEST_VOTES">Lowest Votes</option>
                            </select>                        </div>
                </div>

                <div className="comment-input-container">
                    <div className="user-avatar-placeholder">T</div>
                        <div className="comment-box">
                            <textarea
                                placeholder="Add a comment or suggest a fix..."
                                value={commentText}
                                onChange={(e) => setCommentText(e.target.value)}
                                />
                                <div className="comment-actions-bar">
                                   <div className="formatting-icons" style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                                       <span style={{cursor: 'pointer', fontSize: '1.2rem'}} title="Attach Image" onClick={openModal}>📎</span>
                                            {imageUrl && (
                                                <span style={{ fontSize: '0.8rem', color: '#10b981', display: 'flex', alignItems: 'center', gap: '6px', fontWeight: 'bold' }}>
                                                    Image attached
                                                <button type="button" onClick={removeAttachment} style={{ background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer', fontSize: '1rem', padding: 0 }}>×</button>
                                                </span>
                                            )}
                                   </div>
                                   <button
                                       className="post-comment-btn"
                                       type="button"
                                       onClick={handlePostComment}
                                   >
                                   Post Comment
                                   </button>
                                </div>
                        </div>
                    </div>

                    <div className="comments-list">
                        {sortedComments.length > 0 ? (
                            sortedComments.map((comment) => (
                                <BugComment key={comment.id} comment={comment} bugId={bug.id} />
                                   ))
                               ) : (
                                   <p className="no-comments-text">No comments yet. Be the first to suggest a fix!</p>
                                   )}
                    </div>
                </div>
            </div>

            {isImageModalOpen && (
                      <div style={{
                          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                          backgroundColor: 'rgba(15, 37, 67, 0.6)', zIndex: 1000,
                          display: 'flex', justifyContent: 'center', alignItems: 'center', backdropFilter: 'blur(2px)'
                      }}>
                          <div style={{
                              background: '#ffffff', padding: '24px', borderRadius: '12px',
                              width: '450px', maxWidth: '90%', boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)'
                          }}>
                              <h3 style={{ marginTop: 0, marginBottom: '16px', color: '#0f2543', fontSize: '1.2rem' }}>Attach Image</h3>
                              <p style={{ fontSize: '0.85rem', color: '#6b7280', marginBottom: '12px' }}>Provide a valid image URL to attach it to your comment.</p>

                              <input
                                  type="text"
                                  placeholder="https://example.com/image.png"
                                  value={tempUrl}
                                  onChange={(e) => setTempUrl(e.target.value)}
                                  style={{
                                      width: '100%', padding: '10px 12px', marginBottom: '16px',
                                      borderRadius: '6px', border: '1px solid #d1d5db',
                                      outline: 'none', fontSize: '0.9rem'
                                  }}
                              />

                              {tempUrl && (
                                  <div style={{ marginBottom: '16px', textAlign: 'center', backgroundColor: '#f9fafb', padding: '10px', borderRadius: '6px' }}>
                                      <img
                                          src={tempUrl}
                                          alt="Preview"
                                          style={{ maxHeight: '120px', borderRadius: '4px', maxWidth: '100%', objectFit: 'contain' }}
                                          onError={(e) => e.target.style.display = 'none'}
                                          onLoad={(e) => e.target.style.display = 'inline-block'}
                                      />
                                  </div>
                              )}

                              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
                                  <button type="button" onClick={closeModal} style={{
                                      background: 'transparent', border: '1px solid #d1d5db', padding: '8px 16px',
                                      borderRadius: '6px', cursor: 'pointer', color: '#374151', fontWeight: 'bold'
                                  }}>
                                      Cancel
                                  </button>
                                  <button type="button" onClick={saveModal} style={{
                                      background: '#0f2543', border: 'none', padding: '8px 16px',
                                      borderRadius: '6px', cursor: 'pointer', color: 'white', fontWeight: 'bold'
                                  }}>
                                      Save Attachment
                                  </button>
                              </div>
                          </div>
                      </div>
                  )}
    </article>
  );
}




export default BugFeedCard;
