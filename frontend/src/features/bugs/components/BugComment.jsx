import { useState } from 'react';
import { bugsApi } from '../api/bugsApi';
import { useAuth } from '@features/auth/hooks/useAuth.js';
import { useQueryClient } from '@tanstack/react-query';
import { bugKeys } from '@shared/api/queryKeys.js';

function formatRelativeDate(dateString) {
  if (!dateString) return 'recently';

  const date = new Date(dateString);
  const now = new Date();
  const diffInSeconds = Math.floor((now - date) / 1000);

  if (diffInSeconds < 60) return 'just now';

  const diffInMinutes = Math.floor(diffInSeconds / 60);
  if (diffInMinutes < 60) return `${diffInMinutes}m ago`;

  const diffInHours = Math.floor(diffInMinutes / 60);
  if (diffInHours < 24) return `${diffInHours}h ago`;

  const diffInDays = Math.floor(diffInHours / 24);
  if (diffInDays === 1) return 'yesterday';
  if (diffInDays < 7) return `${diffInDays} days ago`;

  const options = { day: '2-digit', month: 'short', year: 'numeric' };
  return date.toLocaleDateString('en-GB', options);
}

function formatExactDate(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
  });
}

function BugComment({ comment, bugId }) {
    const { user } = useAuth();

    const queryClient = useQueryClient();

    const [isEditing, setIsEditing] = useState(false);
    const [editValue, setEditValue] = useState(comment?.text || comment?.comment || '');

    const isAuthor = user?.id === comment.author?.id;
    const isModerator = user?.role === 'MODERATOR';
    const canModify = isAuthor || isModerator;

    const handleDelete = async () => {
          if (!isAuthor && !isModerator) {
              alert("Nu poti sterge acest comentariu deoarece nu esti autorul lui!");
              return;
          }

          if (!window.confirm("Esti sigur ca vrei sa stergi comentariul?")) return;

          const result = await bugsApi.deleteComment(bugId, comment.id);
          if (result.success) {
              queryClient.invalidateQueries({ queryKey: bugKeys.root });
          } else {
              alert(result.error || "Eroare: Nu am putut sterge comentariul.");
          }
      };

    const handleVote = async (voteType) => {
        try {
          await bugsApi.voteComment(bugId, comment.id, voteType);
          queryClient.invalidateQueries({ queryKey: bugKeys.root });
        } catch (error) {
          console.error("Eroare la votare:", error);
        }
      };

    const handleSaveEdit = async () => {
          if (!editValue.trim()) {
              alert("Comentariul nu poate fi gol!");
              return;
          }

          const result = await bugsApi.updateComment(comment.id, editValue);
          if (result.success) {
              setIsEditing(false);
              queryClient.invalidateQueries({ queryKey: bugKeys.root });
          } else {
              alert(result.error || "Eroare la salvarea modificărilor.");
          }
      };

  const handleCancelEdit = () => {
          setIsEditing(false);
          setEditValue(comment?.text || comment?.comment || '');
      };

  return (
    <div className="single-comment">
      <div className="comment-votes">
          <button type="button" onClick={() => handleVote('UPVOTE')}>▲</button>
          <span>{comment?.voteCount || 0}</span>
          <button type="button" onClick={() => handleVote('DOWNVOTE')}>▼</button>
      </div>
      <div className="comment-content">
          <div className="comment-user-info" style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
              <strong>{comment.author?.username || 'Anonymous'}</strong>
              {comment.isStaff && <span className="user-label-staff">STAFF</span>}
              <span style={{ color: '#9ca3af', fontSize: '0.8rem' }}>•</span>
              <span className="comment-time" style={{ color: '#6b7280', fontSize: '0.85rem' }}>
                  {formatRelativeDate(comment.createdAt)}
              </span>
          </div>

          {comment.imageUrl && (
              <div style={{ marginBottom: '10px' }}>
                  <img
                    src={comment.imageUrl}
                    alt="Comment attachment"
                    style={{ maxWidth: '100%', maxHeight: '250px', borderRadius: '8px', objectFit: 'contain', border: '1px solid #e5e7eb' }}
                    loading="lazy"
                  />
              </div>
          )}

          {isEditing ? (
          <div style={{ marginBottom: '10px' }}>
            <textarea
              value={editValue}
              onChange={(e) => setEditValue(e.target.value)}
              style={{
                  width: '100%', minHeight: '60px', padding: '8px',
                  borderRadius: '6px', border: '1px solid #d1d5db',
                  fontFamily: 'inherit', resize: 'vertical'
              }}
            />
            <div style={{ display: 'flex', gap: '8px', marginTop: '8px' }}>
                <button className="post-comment-btn" style={{ padding: '0.25rem 0.75rem', fontSize: '0.85rem' }} onClick={handleSaveEdit}>
                  Save
                </button>
                <button className="link-btn" onClick={handleCancelEdit}>
                  Cancel
                </button>
            </div>
          </div>
        ) : (
          <p style={{ margin: '0 0 10px 0', color: '#374151', fontSize: '0.95rem' }}>
              {comment?.text || comment?.comment}
          </p>
        )}

        <div className="comment-footer-links" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
            <div style={{ display: 'flex', gap: '1rem' }}>
                {canModify && !isEditing && (
                    <>
                    <button className="link-btn" type="button" onClick={() => setIsEditing(true)}>
                        Edit
                    </button>
                    <button className="link-btn" type="button" onClick={handleDelete} style={{color: '#dc2626'}}>
                        Delete
                    </button>
                    </>
                )}
            </div>
            <span style={{ fontSize: '0.75rem', color: '#9ca3af' }} title="Exact time of posting">
                {formatExactDate(comment.createdAt)}
            </span>
        </div>
      </div>
    </div>
  );
}

export default BugComment;