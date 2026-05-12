import { NavLink } from 'react-router-dom';
import { useAuth } from '@features/auth/hooks/useAuth.js';
import './Sidebar.css';

function Sidebar() {
  const { user, logout } = useAuth();
  const displayName = user?.username || 'User';

  return (
    <aside className="sidebar">
      <NavLink to="/bugs" className="sidebar__brand">
        <span className="sidebar__brand-icon" aria-hidden="true">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none">
            <path
              d="M12 2.5a3.5 3.5 0 0 0-3.46 3H7a1 1 0 1 0 0 2h.34a5.97 5.97 0 0 0-.84 2H5a1 1 0 1 0 0 2h1.07a6 6 0 0 0 .26 2H5a1 1 0 1 0 0 2h1.85a6 6 0 0 0 1.46 2H7a1 1 0 1 0 0 2h2.34c.79.32 1.66.5 2.66.5s1.87-.18 2.66-.5H17a1 1 0 1 0 0-2h-1.31a6 6 0 0 0 1.46-2H19a1 1 0 1 0 0-2h-1.33a6 6 0 0 0 .26-2H19a1 1 0 1 0 0-2h-1.5a5.97 5.97 0 0 0-.84-2H17a1 1 0 1 0 0-2h-1.54A3.5 3.5 0 0 0 12 2.5Z"
              fill="currentColor"
            />
          </svg>
        </span>
        <strong>Bug Reporter</strong>
      </NavLink>

      <nav className="sidebar__nav" aria-label="Main navigation">
        <NavLink to="/bugs" end className="sidebar__link">
          <span className="sidebar__link-icon sidebar__link-icon--feed" aria-hidden="true">
            <span />
            <span />
            <span />
          </span>
          Bug Feed
        </NavLink>
        <NavLink to="/profile" className="sidebar__link">
          <span className="sidebar__link-icon sidebar__link-icon--mine" aria-hidden="true" />
          My Profile
        </NavLink>
        <NavLink to="/bugs/new" className="sidebar__link">
          <span className="sidebar__link-icon sidebar__link-icon--report" aria-hidden="true" />
          Report Bug
        </NavLink>
        {user?.role === 'MODERATOR' && (
          <NavLink to="/moderation/users" className="sidebar__link">
            <span className="sidebar__link-icon sidebar__link-icon--moderation" aria-hidden="true" />
            Moderation
          </NavLink>
        )}
      </nav>

      <div className="sidebar__user">
        <div className="sidebar__avatar">
          {displayName.slice(0, 1).toUpperCase()}
        </div>
        <div className="sidebar__user-info">
          <strong>{displayName}</strong>
        </div>
        <button type="button" className="sidebar__logout" onClick={logout}>
          Log out
        </button>
      </div>
    </aside>
  );
}

export default Sidebar;
