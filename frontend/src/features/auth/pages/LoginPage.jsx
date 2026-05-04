import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.js';
import loginConfig from '../loginConfig.json';
import LoginHeader from '../components/LoginHeader.jsx';
import AccessRestrictedAlert from '../components/AccessRestrictedAlert.jsx';
import LoginForm from '../components/LoginForm.jsx';
import PageFooter from '../components/PageFooter.jsx';
import './LoginPage.css';

function validate(values, rules) {
  const errors = {};

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

  return errors;
}

function LoginPage() {
  const { login, isLoading } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [keepActive, setKeepActive] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});
  const [isAccessRestricted, setIsAccessRestricted] = useState(false);

  const config = useMemo(() => loginConfig, []);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage('');

    const errors = validate({ email, password }, config.validation);
    setFieldErrors(errors);
    if (Object.keys(errors).length > 0) {
      return;
    }

    const result = await login({
      email: email.trim(),
      password,
    });

    if (result.success) {
      setIsAccessRestricted(false);
      navigate('/bugs');
      return;
    }

    if (result.status === 401) {
      setIsAccessRestricted(true);
      setErrorMessage(result.error || config.errors.invalidCredentials);
    } else if (result.status === 0) {
      setErrorMessage(config.errors.network);
    } else {
      setErrorMessage(result.error || config.errors.unknown);
    }
  };

  return (
    <main className="login-page">
      <div className="login-page__inner">
        <LoginHeader title={config.title} subtitle={config.subtitle} />

        {isAccessRestricted && (
          <AccessRestrictedAlert
            title={config.alertTitle}
            message={config.alertMessage}
          />
        )}

        <LoginForm
          config={config}
          email={email}
          password={password}
          keepActive={keepActive}
          isLoading={isLoading}
          fieldErrors={fieldErrors}
          errorMessage={errorMessage}
          onEmailChange={setEmail}
          onPasswordChange={setPassword}
          onKeepActiveChange={setKeepActive}
          onSubmit={handleSubmit}
        />

        <PageFooter
          registerPrompt={config.registerPrompt}
          registerLinkLabel={config.registerLinkLabel}
          registerHref={config.registerHref}
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

export default LoginPage;
