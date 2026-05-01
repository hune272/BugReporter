import { createBrowserRouter, Navigate } from 'react-router-dom';
import LoginPage from './pages/auth/LoginPage.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';

export const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/dashboard" replace /> },
  { path: '/login', element: <LoginPage /> },
  {
    path: '/dashboard',
    element: (
      <ProtectedRoute>
        <main style={{ padding: 32 }}>Dashboard (placeholder)</main>
      </ProtectedRoute>
    ),
  },
]);
