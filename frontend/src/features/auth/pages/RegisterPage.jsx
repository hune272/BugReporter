import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.js';
import registerConfig from '../registerConfig.json';
import LoginHeader from '../components/LoginHeader.jsx';
import RegisterForm from '../components/RegisterForm.jsx';
import PageFooter from '../components/PageFooter.jsx';
import './LoginPage.css';

function validate(values, rules) {
  const errors = {};

  const username = values.username.trim();
  const uRules = rules.username;
  if (username.length === 0) {
    errors.username = uRules.messages.required;
  } else if (username.length < uRules.minLength) {
    errors.username = uRules.messages.minLength;
  } else if (username.length > uRules.maxLength) {
    errors.username = uRules.messages.maxLength;
  }

  const email = values.email.trim();
  const emailRules = rules.email;
  if (email.length === 0) {
    errors.email = emailRules.messages.required;
  } else if (!new RegExp(emailRules.pattern).test(email)) {
    errors.email = emailRules.messages.pattern;
  }

  const pRules = rules.password;
  if (values.password.length === 0) {
    errors.password = pRules.messages.required;
  } else if (values.password.length < pRules.minLength) {
    errors.password = pRules.messages.minLength;
  }

  const cRules = rules.confirmPassword;
  if (values.confirmPassword.length === 0) {
    errors.confirmPassword = cRules.messages.required;
  } else if (values.confirmPassword !== values.password) {
    errors.confirmPassword = cRules.messages.mismatch;
  }

  return errors;
}

function RegisterPage() {
  const { register, isLoading } = useAuth();
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});

  const config = useMemo(() => registerConfig, []);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

    const errors = validate(
      { username, email, password, confirmPassword },
      config.validation,
    );
    setFieldErrors(errors);
    if (Object.keys(errors).length > 0) {
      return;
    }

    const result = await register({
      username: username.trim(),
      email: email.trim(),
      password,
    });

    if (result.success) {
      setSuccessMessage(config.successMessage);
      setTimeout(() => navigate('/login'), 1200);
      return;
    }

    if (result.status === 0) {
      setErrorMessage(config.errors.network);
    } else {
      setErrorMessage(result.error || config.errors.unknown);
    }
  };

  return (
    <main className="login-page">
      <div className="login-page__inner">
        <LoginHeader title={config.title} subtitle={config.subtitle} />

        <RegisterForm
          config={config}
          username={username}
          email={email}
          password={password}
          confirmPassword={confirmPassword}
          isLoading={isLoading}
          fieldErrors={fieldErrors}
          errorMessage={errorMessage}
          successMessage={successMessage}
          onUsernameChange={setUsername}
          onEmailChange={setEmail}
          onPasswordChange={setPassword}
          onConfirmPasswordChange={setConfirmPassword}
          onSubmit={handleSubmit}
        />

        <PageFooter
          registerPrompt={config.loginPrompt}
          registerLinkLabel={config.loginLinkLabel}
          registerHref={config.loginHref}
          footer={config.footer}
        />
      </div>

      <div className="login-page__corner-bug" aria-hidden="true">
        <svg viewBox="0 0 24 24" width="150" height="150" fill="none">
          <path
            d="M12 2.5a3.5 3.5 0 0 0-3.46 3H7a1 1 0 1 0 0 2h.34a5.97 5.97 0 0 0-.84 2H5a1 1 0 1 0 0 2h1.07a6 6 0 0 0 .26 2H5a1 1 0 1 0 0 2h1.85a6 6 0 0 0 1.46 2H7a1 1 0 1 0 0 2h2.34c.79.32 1.66.5 2.66.5s1.87-.18 2.66-.5H17a1 1 0 1 0 0-2h-1.31a6 6 0 0 0 1.46-2H19a1 1 0 1 0 0-2h-1.33a6 6 0 0 0 .26-2H19a1 1 0 1 0 0-2h-1.5a5.97 5.97 0 0 0-.84-2H17a1 1 0 1 0 0-2h-1.54A3.5 3.5 0 0 0 12 2.5Z"
            fill="#111827"
          />
        </svg>
      </div>
    </main>
  );
}

export default RegisterPage;