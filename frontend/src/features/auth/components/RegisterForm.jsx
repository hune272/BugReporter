import FormField from '@shared/components/forms/FormField.jsx';
import './LoginForm.css';

function RegisterForm({
  config,
  username,
  email,
  password,
  confirmPassword,
  isLoading,
  fieldErrors,
  errorMessage,
  successMessage,
  onUsernameChange,
  onEmailChange,
  onPasswordChange,
  onConfirmPasswordChange,
  onSubmit,
}) {
  const { fields, submitLabel, submittingLabel } = config;
  const isSubmitDisabled =
    isLoading ||
    username.trim().length === 0 ||
    email.trim().length === 0 ||
    password.length === 0 ||
    confirmPassword.length === 0;

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
        id="email"
        label={fields.email.label}
        type="email"
        value={email}
        onChange={onEmailChange}
        placeholder={fields.email.placeholder}
        iconName={fields.email.icon}
        monospacePlaceholder
        autoComplete="email"
        disabled={isLoading}
        required
      />
      {fieldErrors.email && (
        <p className="login-form__field-error" role="alert">
          {fieldErrors.email}
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
        autoComplete="new-password"
        disabled={isLoading}
        required
      />
      {fieldErrors.password && (
        <p className="login-form__field-error" role="alert">
          {fieldErrors.password}
        </p>
      )}

      <FormField
        id="confirmPassword"
        label={fields.confirmPassword.label}
        type="password"
        value={confirmPassword}
        onChange={onConfirmPasswordChange}
        placeholder={fields.confirmPassword.placeholder}
        iconName={fields.confirmPassword.icon}
        autoComplete="new-password"
        disabled={isLoading}
        required
      />
      {fieldErrors.confirmPassword && (
        <p className="login-form__field-error" role="alert">
          {fieldErrors.confirmPassword}
        </p>
      )}

      {errorMessage && (
        <p className="login-form__submit-error" role="alert">
          {errorMessage}
        </p>
      )}

      {successMessage && (
        <p
          className="login-form__submit-error"
          role="status"
          style={{
            color: '#065f46',
            background: '#d1fae5',
            borderColor: 'rgba(5, 150, 105, 0.25)',
          }}
        >
          {successMessage}
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

export default RegisterForm;