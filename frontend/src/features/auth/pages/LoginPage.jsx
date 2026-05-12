import { Navigate } from 'react-router-dom';
import loginConfig from '../loginConfig.json';
import { useLoginForm } from '../hooks/useLoginForm.js';
import LoginHeader from '../components/LoginHeader.jsx';
import AccessRestrictedAlert from '../components/AccessRestrictedAlert.jsx';
import LoginForm from '../components/LoginForm.jsx';
import PageFooter from '../components/PageFooter.jsx';
import './LoginPage.css';

function LoginPage() {
  const config = loginConfig;
  const form = useLoginForm(config);

  if (!form.isLoading && form.user) {
    return <Navigate to="/bugs" replace />;
  }

  return (
    <main className="login-page">
      <div className="login-page__inner">
        <LoginHeader title={config.title} subtitle={config.subtitle} />

        {form.isAccessRestricted && (
          <AccessRestrictedAlert
            title={config.alertTitle}
            message={config.alertMessage}
          />
        )}

        <LoginForm
          config={config}
          email={form.email}
          password={form.password}
          keepActive={form.keepActive}
          isLoading={form.isLoading}
          fieldErrors={form.fieldErrors}
          errorMessage={form.errorMessage}
          onEmailChange={form.setEmail}
          onPasswordChange={form.setPassword}
          onKeepActiveChange={form.setKeepActive}
          onSubmit={form.handleSubmit}
        />

        <PageFooter
          registerPrompt={config.registerPrompt}
          registerLinkLabel={config.registerLinkLabel}
          registerHref={config.registerHref}
          footer={config.footer}
        />
      </div>
    </main>
  );
}

export default LoginPage;
