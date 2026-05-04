import './FormField.css';

const ICONS = {
  user: (
    <svg viewBox="0 0 24 24" width="16" height="16" fill="none" aria-hidden="true">
      <path
        d="M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm0 2c-3.866 0-7 2.239-7 5v1h14v-1c0-2.761-3.134-5-7-5Z"
        fill="currentColor"
      />
    </svg>
  ),
  lock: (
    <svg viewBox="0 0 24 24" width="16" height="16" fill="none" aria-hidden="true">
      <path
        d="M7 10V8a5 5 0 0 1 10 0v2h1a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-8a2 2 0 0 1 2-2h1Zm2 0h6V8a3 3 0 0 0-6 0v2Zm3 4a1.5 1.5 0 0 0-.75 2.8V18a.75.75 0 0 0 1.5 0v-1.2A1.5 1.5 0 0 0 12 14Z"
        fill="currentColor"
      />
    </svg>
  ),
};

function FormField({
  id,
  label,
  type = 'text',
  value,
  onChange,
  placeholder = '',
  iconName,
  monospacePlaceholder = false,
  autoComplete,
  rightSlot,
  disabled = false,
  required = false,
}) {
  const inputClassName = [
    'form-field__input',
    iconName ? 'form-field__input--with-icon' : '',
    monospacePlaceholder ? 'form-field__input--mono-placeholder' : '',
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <div className="form-field">
      <div className="form-field__label-row">
        <label className="form-field__label" htmlFor={id}>
          {label}
        </label>
        {rightSlot && <div className="form-field__label-aside">{rightSlot}</div>}
      </div>
      <div className="form-field__control">
        {iconName && ICONS[iconName] && (
          <span className="form-field__icon" aria-hidden="true">
            {ICONS[iconName]}
          </span>
        )}
        <input
          id={id}
          name={id}
          type={type}
          className={inputClassName}
          value={value}
          onChange={(event) => onChange(event.target.value)}
          placeholder={placeholder}
          autoComplete={autoComplete}
          disabled={disabled}
          required={required}
        />
      </div>
    </div>
  );
}

export default FormField;
