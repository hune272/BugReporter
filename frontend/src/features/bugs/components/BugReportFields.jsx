import './BugReportFields.css';

function BugReportFields({ form, onFieldChange }) {
  return (
    <>
      <label className="bug-report-field">
        <span>Bug title</span>
        <input
          value={form.title}
          onChange={(event) => onFieldChange('title', event.target.value)}
          placeholder="e.g. Critical: Auth token expiration on checkout"
        />
      </label>

      <label className="bug-report-field bug-report-field--textarea">
        <span>Description</span>
        <textarea
          value={form.text}
          onChange={(event) => onFieldChange('text', event.target.value)}
          placeholder="Steps to reproduce, expected vs actual behavior..."
        />
      </label>

      <label className="bug-report-field bug-report-field--url">
        <span>Screenshot / attachment URL</span>
        <input
          value={form.picture}
          onChange={(event) => onFieldChange('picture', event.target.value)}
          placeholder="Paste image URL from database/storage..."
        />
      </label>
    </>
  );
}

export default BugReportFields;
