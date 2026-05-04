import './PageFooter.css';

const META_ICONS = {
  lock: (
    <svg viewBox="0 0 24 24" width="12" height="12" fill="none" aria-hidden="true">
      <path
        d="M7 10V8a5 5 0 0 1 10 0v2h1a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-8a2 2 0 0 1 2-2h1Zm2 0h6V8a3 3 0 0 0-6 0v2Z"
        fill="currentColor"
      />
    </svg>
  ),
  package: (
    <svg viewBox="0 0 24 24" width="12" height="12" fill="none" aria-hidden="true">
      <path
        d="M12 3 4 7v10l8 4 8-4V7l-8-4Zm0 2.18L17.82 8 12 10.82 6.18 8 12 5.18ZM6 9.7l5 2.5v6.6l-5-2.5V9.7Zm7 9.1v-6.6l5-2.5v6.6l-5 2.5Z"
        fill="currentColor"
      />
    </svg>
  ),
};

function PageFooter({ registerPrompt, registerLinkLabel, registerHref, footer }) {
  return (
    <div className="page-footer">
      <p className="page-footer__register">
        {registerPrompt}{' '}
        <a className="page-footer__register-link" href={registerHref}>
          {registerLinkLabel}
        </a>
      </p>

      <div className="page-footer__meta" aria-label="Build metadata">
        {footer.metaItems.map((item, index) => (
          <span className="page-footer__meta-item" key={item.text}>
            {META_ICONS[item.icon]}
            <span>{item.text}</span>
            {index < footer.metaItems.length - 1 && (
              <span className="page-footer__meta-divider" aria-hidden="true">
                |
              </span>
            )}
          </span>
        ))}
      </div>

      <p className="page-footer__disclaimer">{footer.disclaimer}</p>
    </div>
  );
}

export default PageFooter;
