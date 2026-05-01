import FormField from '../../../components/forms/FormField.jsx';
import './LoginForm.css';

function LoginForm({
  config,
  username,
  password,
  keepActive,
  isLoading,
  fieldErrors,
  errorMessage,
  onUsernameChange,
  onPasswordChange,
  onKeepActiveChange,
  onSubmit,
}) {
  const { fields, keepActiveLabel, submitLabel, submittingLabel } = config;
  const isSubmitDisabled =
    isLoading || username.trim().length === 0 || password.length === 0;

  return (
    <form className="login-form" onSubmit={onSubmit} noValidate>
      <FormField
        id="username"
        label={fields.username.label}
        type="text"
        value={username}
        onChange={onUsernameChange}
        placeholder={fields.username.placeholder}
        iconName={fields.username.icon}
        monospacePlaceholder
        autoComplete="username"
        disabled={isLoading}
        required
      />
      {fieldErrors.username && (
        <p className="login-form__field-error" role="alert">
          {fieldErrors.username}
        </p>
      )}

      <FormField
        id="password"
        label={fields.password.label}
        type="password"
        value={password}
        onChange={onPasswordChange}
        placeholder={fields.password.placeholder}
        iconName={fields.password.icon}
        autoComplete="current-password"
        disabled={isLoading}
        required
        rightSlot={
          <a
            className="login-form__forgot"
            href={fields.password.forgotHref}
            tabIndex={isLoading ? -1 : 0}
          >
            {fields.password.forgotLabel}
          </a>
        }
      />
      {fieldErrors.password && (
        <p className="login-form__field-error" role="alert">
          {fieldErrors.password}
        </p>
      )}

      <label className="login-form__checkbox">
        <input
          type="checkbox"
          checked={keepActive}
          onChange={(event) => onKeepActiveChange(event.target.checked)}
          disabled={isLoading}
        />
        <span>{keepActiveLabel}</span>
      </label>

      {errorMessage && (
        <p className="login-form__submit-error" role="alert">
          {errorMessage}
        </p>
      )}

      <button
        type="submit"
        className="login-form__submit"
        disabled={isSubmitDisabled}
      >
        <span>{isLoading ? submittingLabel : submitLabel}</span>
        <svg
          viewBox="0 0 24 24"
          width="14"
          height="14"
          fill="none"
          aria-hidden="true"
        >
          <path
            d="M5 12h13m0 0-5-5m5 5-5 5"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      </button>
    </form>
  );
}

export default LoginForm;
