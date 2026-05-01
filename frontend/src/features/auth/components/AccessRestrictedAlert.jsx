import './AccessRestrictedAlert.css';

function AccessRestrictedAlert({ title, message }) {
  return (
    <div className="access-alert" role="alert">
      <div className="access-alert__icon" aria-hidden="true">
        <svg viewBox="0 0 24 24" width="20" height="20" fill="none">
          <circle cx="12" cy="12" r="9" stroke="currentColor" strokeWidth="2" />
          <line
            x1="6.2"
            y1="6.2"
            x2="17.8"
            y2="17.8"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
          />
        </svg>
      </div>
      <div className="access-alert__body">
        <p className="access-alert__title">{title}</p>
        <p className="access-alert__message">{message}</p>
      </div>
    </div>
  );
}

export default AccessRestrictedAlert;
