import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@features/auth/hooks/useAuth.js';

function ProtectedRoute({ children, requireRole }) {
  const { user, isLoading } = useAuth();

  if (isLoading) {
    return null;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (requireRole && user.role !== requireRole) {
    return <Navigate to="/bugs" replace />;
  }

  return children ?? <Outlet />;
}

export default ProtectedRoute;
